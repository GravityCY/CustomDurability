package me.gravityio.customdurability.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.WildcardMatcher;
import me.gravityio.customdurability.commands.argument.AnyStringArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.ArrayList;

public class SetCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var setCmd = Commands.literal("set");

        setCmd.then(SetCommand.buildItem());
        setCmd.then(SetCommand.buildWildcard());

        return setCmd;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildWildcard() {
        var rawCmd = Commands.literal("wildcard");

        var stringArg = Commands.argument("string", AnyStringArgument.string());
        var durabilityArg = Commands.argument("durability", IntegerArgumentType.integer());
        durabilityArg.requires(CommandSourceStack::isPlayer);
        durabilityArg.executes(cmdContext -> {
            var source = cmdContext.getSource();
            var str = AnyStringArgument.get(cmdContext, "string");
            var durability = IntegerArgumentType.getInteger(cmdContext, "durability");

            var toAddIDList = new ArrayList<String>(16);
            for (Holder.Reference<Item> ref : CustomDurabilityMod.iterateDamageables()) {
                var res = ref.key().location().toString();
                if(!WildcardMatcher.matches(str, res)) continue;
                toAddIDList.add(res);
            }

            var context = ModCommands.getContextNew(cmdContext);
            context.setAdditionAll(durability, toAddIDList.toArray(String[]::new));
            source.sendSuccess(() -> ContextCommand.getListMessage(context), false);
            return 1;
        });

        stringArg.then(durabilityArg);
        rawCmd.then(stringArg);
        return rawCmd;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildItem() {
        var itemCmd = Commands.literal("item");

        var itemArg = Commands.argument("item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM))
                .suggests(new ItemSuggestionProvider());
        var durabilityArg = Commands.argument("durability", IntegerArgumentType.integer());

        durabilityArg.executes(context -> {
            var source = context.getSource();
            var item = ResourceOrTagKeyArgument.getResourceOrTagKey(context, "item", Registries.ITEM, new DynamicCommandExceptionType(o -> Component.literal("NOT A VALID ITEM")));
            var durability = IntegerArgumentType.getInteger(context, "durability");
            var str = item.asPrintable();

            CustomDurabilityMod.setDurabilityOverride(str, durability);

            source.sendSuccess(() -> Component.translatable("commands.customdurability.set", str, durability), false);
            return 1;
        });
        itemArg.then(durabilityArg);
        itemCmd.then(itemArg);
        return itemCmd;
    }
}
