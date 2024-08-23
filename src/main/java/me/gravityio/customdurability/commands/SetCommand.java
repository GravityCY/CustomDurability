package me.gravityio.customdurability.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.ModConfig;
import me.gravityio.customdurability.ModEvents;
import me.gravityio.customdurability.WildcardMatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;

import java.util.ArrayList;

public class SetCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var setCmd = Commands.literal("set");

        setCmd.then(buildItem());
        setCmd.then(buildRaw());
        setCmd.then(buildTemp());

        return setCmd;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildTemp() {
        var tempCmd = Commands.literal("temp");

        var itemArg = Commands.argument("item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM));
        var durabilityArg = Commands.argument("durability", IntegerArgumentType.integer());

        durabilityArg.requires(CommandSourceStack::isPlayer);
        durabilityArg.executes(context -> {
            var source = context.getSource();
            var player = source.getPlayer();
            var uuid = player.getUUID();
            var item = ResourceOrTagKeyArgument.getResourceOrTagKey(context, "item", Registries.ITEM, new DynamicCommandExceptionType(o -> Component.literal("NOT A VALID ITEM")));
            var durability = IntegerArgumentType.getInteger(context, "durability");
            var str = item.asPrintable();

            ModConfig.INSTANCE.setDurabilityOverride(str, durability);
            ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
            ModConfig.INSTANCE.save();

            source.sendSuccess(() -> Component.translatable("commands.customdurability.set", str, durability), false);
            return 1;
        });

        itemArg.then(durabilityArg);

        return tempCmd;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRaw() {
        var rawCmd = Commands.literal("raw");

        var stringArg = Commands.argument("string", StringArgumentType.string());
        var durabilityArg = Commands.argument("durability",IntegerArgumentType.integer());
        durabilityArg.requires(CommandSourceStack::isPlayer);
        durabilityArg.executes(context -> {
            var source = context.getSource();
            var str = StringArgumentType.getString(context, "string");
            var durability = IntegerArgumentType.getInteger(context, "durability");

            var toAddIDList = new ArrayList<String>(16);
            for (Holder.Reference<Item> ref : CustomDurabilityMod.iterateDamageables()) {
                var res = ref.key().location().toString();
                if(!WildcardMatcher.matches(str, res)) continue;
                toAddIDList.add(res);
            }

            var message = Component.literal("");
            for (String id : toAddIDList) {
                message.append(Component.literal(id + "\n").withStyle(ChatFormatting.AQUA));
            }

            ModCommands.setContext(context, new ModCommands.SetItemCommandContext(toAddIDList, durability));

            var a = Style.EMPTY.applyFormat(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/cd confirm"));
            var b = Style.EMPTY.applyFormat(ChatFormatting.RED).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/cd cancel"));
            message.append(Component.literal("Add all?\n"));

            message.append(Component.literal("[Confirm] ").withStyle(a));
            message.append(Component.literal("[Cancel]").withStyle(b));

            source.sendSuccess(() -> message, false);
            return 1;
        });

        stringArg.then(durabilityArg);
        rawCmd.then(stringArg);
        return rawCmd;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildItem() {
        var itemCmd = Commands.literal("item");

        var itemArg = Commands.argument("item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM));
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
        itemArg.then(durabilityArg);
        itemCmd.then(itemArg);
        return itemCmd;
    }
}
