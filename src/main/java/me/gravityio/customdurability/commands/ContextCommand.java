package me.gravityio.customdurability.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.Map;
import java.util.function.BiConsumer;

public class ContextCommand {

    public static BiConsumer<Map.Entry<String, Integer>, MutableComponent> DEFAULT_MODIFIER = ListCommand.ELEMENT_DISPLAY
            .andThen(ListCommand.getElementButtons("/cd context set %s %d", "/cd context clear %s"))
            .andThen(ListCommand.NEWLINE);

    public static MutableComponent getListMessage(ModCommands.ItemCommandContext context) {
        if (context.getAdditions().isEmpty()) {
            return Component.translatable("commands.customdurability.list.none");
        }

        var message = Component.translatable("commands.customdurability.context.list");
        message.append("\n");
        message.append(ListCommand.getListBuilder(context.getAdditions(), DEFAULT_MODIFIER));
        message.append("\n");

        var a = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cd context confirm"));
        var b = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cd context cancel"));

        message.append(Component.translatable("commands.customdurability.messages.add_all"));

        message.append(" ");
        message.append("§7[");
        message.append(Component.translatable("commands.customdurability.messages.confirm").withStyle(a));
        message.append("§7]");

        message.append(" ");
        message.append("§7[");
        message.append(Component.translatable("commands.customdurability.messages.cancel").withStyle(b));
        message.append("§7]");
        return message;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var contextCommand = Commands.literal("context");

        contextCommand.then(buildConfirm());
        contextCommand.then(buildCancel());
        contextCommand.then(buildSet());
        contextCommand.then(buildClear());
        contextCommand.then(buildList());

        return contextCommand;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildList() {
        var listCommand = Commands.literal("list");

        listCommand.executes(cmdContext -> {
            var source = cmdContext.getSource();

            var context = ModCommands.getContext(cmdContext);
            if (context == null) {
                source.sendSuccess(() -> Component.translatable("commands.customdurability.context.no_context"), false);
                return 0;
            }

            source.sendSuccess(() -> getListMessage(context), false);
            return 1;
        });

        return listCommand;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildCancel() {
        var confirm = Commands.literal("cancel");

        confirm.requires(CommandSourceStack::isPlayer);
        confirm.executes(cmdContext -> {
            var context = ModCommands.getContext(cmdContext);
            if (context == null) {
                cmdContext.getSource().sendFailure(Component.translatable("commands.customdurability.context.no_context"));
                return 0;
            }
            var ret = context.onCancel(cmdContext);
            ModCommands.removeContext(cmdContext);
            return ret;
        });

        return confirm;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildConfirm() {
        var confirm = Commands.literal("confirm");

        confirm.requires(CommandSourceStack::isPlayer);
        confirm.executes(cmdContext -> {
            var context = ModCommands.getContext(cmdContext);
            if (context == null) {
                cmdContext.getSource().sendFailure(Component.translatable("commands.customdurability.context.no_context"));
                return 0;
            }
            var ret = context.onConfirm(cmdContext);
            ModCommands.removeContext(cmdContext);
            return ret;
        });

        return confirm;
    }

    private static  LiteralArgumentBuilder<CommandSourceStack> buildSet() {
        var setCommand = Commands.literal("set");
        var itemArgument = Commands.argument("item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM));
        var durabilityArgument = Commands.argument("durability", IntegerArgumentType.integer(0));

        durabilityArgument.executes(cmdContext -> {
            var source = cmdContext.getSource();
            var item = ResourceOrTagKeyArgument.getResourceOrTagKey(cmdContext, "item", Registries.ITEM, new DynamicCommandExceptionType(o -> Component.literal("NOT A VALID ITEM")));
            var durability = IntegerArgumentType.getInteger(cmdContext, "durability");
            var str = item.asPrintable();

            var context = ModCommands.getContextNew(cmdContext);
            context.setAddition(durability, str);

            source.sendSuccess(() -> Component.translatable("commands.customdurability.context.set", str, durability), false);
            return 1;
        });

        itemArgument.then(durabilityArgument);
        setCommand.then(itemArgument);
        return setCommand;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildClear() {
        var clearCommand = Commands.literal("clear");

        clearCommand.executes(cmdContext -> {
            var context = ModCommands.getContext(cmdContext);
            if (context == null) {
                cmdContext.getSource().sendFailure(Component.translatable("commands.customdurability.context.no_context"));
                return 0;
            }
            context.getAdditions().clear();
            cmdContext.getSource().sendSuccess(() -> Component.translatable("commands.customdurability.context.clear.all"), false);
            return 1;
        });

        var itemArgument = Commands.argument("item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM));
        itemArgument.executes(cmdContext -> {
            var source = cmdContext.getSource();
            var item = ResourceOrTagKeyArgument.getResourceOrTagKey(cmdContext, "item", Registries.ITEM, new DynamicCommandExceptionType(o -> Component.literal("NOT A VALID ITEM")));
            var str = item.asPrintable();

            var context = ModCommands.getContext(cmdContext);
            if (context == null) {
                cmdContext.getSource().sendFailure(Component.translatable("commands.customdurability.context.no_context"));
                return 0;
            }

            if (!context.hasAddition(str)) {
                source.sendSuccess(() -> Component.translatable("commands.customdurability.clear.not_found", str), false);
                return 1;
            }

            context.removeAddition(str);
            var message = Component.translatable("commands.customdurability.context.clear.remove", str);
            message.append("\n");
            message.append(ContextCommand.getListMessage(context));
            source.sendSuccess(() -> message, false);
            return 1;
        });

        clearCommand.then(itemArgument);
        return clearCommand;
    }
}
