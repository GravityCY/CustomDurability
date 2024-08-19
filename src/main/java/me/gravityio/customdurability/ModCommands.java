package me.gravityio.customdurability;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class ModCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> doBuild(CommandBuildContext registry, Commands.CommandSelection environment) {
        var cd = Commands.literal("cd");
        cd.requires(source -> source.hasPermission(4));

        var armorMultiplier = doBuildArmorMultiplier();
        var list = doBuildList();
        var clear = doBuildClear();
        var set = doBuildSet();

        cd.then(armorMultiplier)
                .then(list)
                .then(clear)
                .then(set);
        return cd;

    }

    private static LiteralArgumentBuilder<CommandSourceStack> doBuildArmorMultiplier() {
        var armorMultiplier = Commands.literal("armorMultiplier");

        armorMultiplier.executes(context -> {
            var source = context.getSource();
            Component text;
            if (ModConfig.INSTANCE.armor_is_durability_multiplier) {
                text = Component.translatable("commands.customdurability.armor_multiplier.on");
            } else {
                text = Component.translatable("commands.customdurability.armor_multiplier.off");
            }
            source.sendSuccess(() -> text, false);
            return 1;
        });

        var onOffArg = Commands.argument("onOff", BoolArgumentType.bool());
        onOffArg.executes(context -> {
            var source = context.getSource();
            var onOff = BoolArgumentType.getBool(context, "onOff");
            ModConfig.INSTANCE.armor_is_durability_multiplier(onOff);
            ModConfig.INSTANCE.save();
            Component text;
            if (onOff) {
                text = Component.translatable("commands.customdurability.armor_multiplier.set.on");
            } else {
                text = Component.translatable("commands.customdurability.armor_multiplier.set.off");
            }
            source.sendSuccess(() -> text, false);
            return 1;
        });

        armorMultiplier.then(onOffArg);
        return armorMultiplier;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> doBuildClear() {
        var clear = Commands.literal("clear");
        clear.executes(context -> {
            ModConfig.INSTANCE.durability_overrides.clear();
            ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
            ModConfig.INSTANCE.save();
            context.getSource().sendSuccess(() -> Component.translatable("commands.customdurability.clear"), false);
            return 1;
        });

        var idOrTagArg = Commands.argument("item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM));
        idOrTagArg.executes(context -> {
            var source = context.getSource();
            var item = ResourceOrTagKeyArgument.getResourceOrTagKey(context, "item", Registries.ITEM, new DynamicCommandExceptionType(o -> () -> ""));
            var str = item.asPrintable();

            if (!ModConfig.INSTANCE.hasDurabilityOverride(str)) {
                source.sendSuccess(() -> Component.translatable("commands.customdurability.clear.not_found"), false);
                return 1;
            }

            ModConfig.INSTANCE.removeDurabilityOverride(str);
            ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
            ModConfig.INSTANCE.save();

            source.sendSuccess(() -> Component.translatable("commands.customdurability.clear.remove", str), false);
            return 1;

        });
        clear.then(idOrTagArg);

        return clear;

    }

    private static LiteralArgumentBuilder<CommandSourceStack> doBuildSet() {
        var set = Commands.literal("set");
        var idOrTagArg = Commands.argument("item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM));
        var durabilityArg = Commands.argument("durability", IntegerArgumentType.integer());
        durabilityArg.executes(context -> {
            var source = context.getSource();
            var item = ResourceOrTagKeyArgument.getResourceOrTagKey(context, "item", Registries.ITEM, new DynamicCommandExceptionType(o -> Component.literal("NOT A VALID ITEM")));
            var durability = IntegerArgumentType.getInteger(context, "durability");
            var str = item.asPrintable();

            ModConfig.INSTANCE.setDurabilityOverride(str, durability);
            ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
            ModConfig.INSTANCE.save();

            source.sendSuccess(() -> Component.translatable("commands.customdurability.set", str, durability), false);
            return 1;
        });

        idOrTagArg.then(durabilityArg);
        set.then(idOrTagArg);
        return set;

    }

    private static LiteralArgumentBuilder<CommandSourceStack> doBuildList() {
        var list = Commands.literal("list");
        list.executes(context -> {
            var source = context.getSource();
            if (ModConfig.INSTANCE.durability_overrides.isEmpty()) {
                source.sendSuccess(() -> Component.translatable("commands.customdurability.list.none"), false);

            } else {
                source.sendSuccess(() -> Component.translatable("commands.customdurability.list"), false);
                for (Map.Entry<String, Integer> entry : ModConfig.INSTANCE.durability_overrides.entrySet()) {
                    Component text = Component.literal(" - '§b%s§r': %d".formatted(entry.getKey(), entry.getValue()));
                    source.sendSuccess(() -> text, false);
                }

            }
            return 1;
        });
        return list;

    }

}
