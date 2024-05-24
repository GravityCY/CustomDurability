package me.gravityio.customdurability;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Map;

public class ModCommands {

    public static LiteralArgumentBuilder<ServerCommandSource> doBuild(CommandRegistryAccess registry, CommandManager.RegistrationEnvironment environment) {
        var cd = CommandManager.literal("cd");
        cd.requires(source -> source.hasPermissionLevel(4));

        var armorMultiplier = doBuildArmorMultiplier();
        var list = doBuildList();
        var clear = doBuildClear();
        var set = doBuildSet();

        cd
                .then(armorMultiplier)
                .then(list)
                .then(clear)
                .then(set);
        return cd;

    }

    private static LiteralArgumentBuilder<ServerCommandSource> doBuildArmorMultiplier() {
        var armorMultiplier = CommandManager.literal("armorMultiplier");

        armorMultiplier.executes(context -> {
            var source = context.getSource();
            Text text;
            if (ModConfig.INSTANCE.armor_is_durability_multiplier) {
                text = Text.translatable("commands.customdurability.armor_multiplier.on");
            } else {
                text = Text.translatable("commands.customdurability.armor_multiplier.off");
            }
            source.sendFeedback(() -> text, false);
            return 1;
        });

        var onOffArg = CommandManager.argument("onOff", BoolArgumentType.bool());
        onOffArg.executes(context -> {
            var source = context.getSource();
            var onOff = BoolArgumentType.getBool(context, "onOff");
            ModConfig.INSTANCE.armor_is_durability_multiplier(onOff);
            ModConfig.INSTANCE.save();
            Text text;
            if (onOff) {
                text = Text.translatable("commands.customdurability.armor_multiplier.set.on");
            } else {
                text = Text.translatable("commands.customdurability.armor_multiplier.set.off");
            }
            source.sendFeedback(() -> text, false);
            return 1;
        });

        armorMultiplier.then(onOffArg);
        return armorMultiplier;
    }

    private static LiteralArgumentBuilder<ServerCommandSource> doBuildClear() {
        var clear = CommandManager.literal("clear");
        clear.executes(context -> {
            ModConfig.INSTANCE.durability_overrides.clear();
            ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
            ModConfig.INSTANCE.save();
            context.getSource().sendFeedback(() -> Text.translatable("commands.customdurability.clear"), false);
            return 1;
        });

        var idOrTagArg = CommandManager.argument("item", RegistryPredicateArgumentType.registryPredicate(RegistryKeys.ITEM));
        idOrTagArg.executes(context -> {
            var source = context.getSource();
            var item = RegistryPredicateArgumentType.getPredicate(context, "item", RegistryKeys.ITEM, new DynamicCommandExceptionType(o -> () -> ""));
            var str = item.asString();

            if (!ModConfig.INSTANCE.hasDurabilityOverride(str)) {
                source.sendFeedback(() -> Text.translatable("commands.customdurability.clear.not_found"), false);
                return 1;
            }

            ModConfig.INSTANCE.removeDurabilityOverride(str);
            ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
            ModConfig.INSTANCE.save();

            source.sendFeedback(() -> Text.translatable("commands.customdurability.clear.remove", str), false);
            return 1;

        });
        clear.then(idOrTagArg);

        return clear;

    }

    private static LiteralArgumentBuilder<ServerCommandSource> doBuildSet() {
        var set = CommandManager.literal("set");
        var idOrTagArg = CommandManager.argument("item", RegistryPredicateArgumentType.registryPredicate(RegistryKeys.ITEM));
        var durabilityArg = CommandManager.argument("durability", IntegerArgumentType.integer());
        durabilityArg.executes(context -> {
            var source = context.getSource();
            var item = RegistryPredicateArgumentType.getPredicate(context, "item", RegistryKeys.ITEM, new DynamicCommandExceptionType(o -> Text.literal("NOT A VALID ITEM")));
            var durability = IntegerArgumentType.getInteger(context, "durability");
            var str = item.asString();

            ModConfig.INSTANCE.setDurabilityOverride(str, durability);
            ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
            ModConfig.INSTANCE.save();

            source.sendFeedback(() -> Text.translatable("commands.customdurability.set", str, durability), false);
            return 1;
        });

        idOrTagArg.then(durabilityArg);
        set.then(idOrTagArg);
        return set;

    }

    private static LiteralArgumentBuilder<ServerCommandSource> doBuildList() {
        var list = CommandManager.literal("list");
        list.executes(context -> {
            var source = context.getSource();
            if (ModConfig.INSTANCE.durability_overrides.isEmpty()) {
                source.sendFeedback(() -> Text.translatable("commands.customdurability.list.none"), false);

            } else {
                source.sendFeedback(() -> Text.translatable("commands.customdurability.list"), false);
                for (Map.Entry<String, Integer> entry : ModConfig.INSTANCE.durability_overrides.entrySet()) {
                    Text text = Text.literal(" - '§b%s§r': %d".formatted(entry.getKey(), entry.getValue()));
                    source.sendFeedback(() -> text, false);
                }

            }
            return 1;
        });
        return list;

    }

}
