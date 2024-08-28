package me.gravityio.customdurability.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import me.gravityio.customdurability.decorator.ElementButtonDecorator;
import me.gravityio.customdurability.decorator.SimpleDecorator;
import me.gravityio.customdurability.decorator.TextDecorator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import java.util.Map;
import java.util.function.Predicate;

public class ContextCommand {
    public static final Predicate<Item> IS_TOOL = item -> item instanceof DiggerItem || item instanceof FishingRodItem || item instanceof ShearsItem;

    public static final Predicate<Item> IS_WEAPON = item -> item instanceof SwordItem || item instanceof CrossbowItem
            || item instanceof BowItem || item instanceof TridentItem
            //? if >=1.20.5 {
            || item instanceof MaceItem
            //?}
    ;
    public static final Predicate<Item> IS_ARMOUR = item -> item instanceof ArmorItem || item instanceof ShieldItem
            || item instanceof ElytraItem
            //? if >=1.20.5 {
            || item instanceof AnimalArmorItem
            //?}
    ;

    public static final Component NO_CONTEXT = Component.translatable("commands.customdurability.context.no_context");

    public static TextDecorator ELEMENT_BUTTON_DECORATOR = new ElementButtonDecorator(
            "/cd context set item %s %d", "/cd context clear %s",
            Component.translatable("commands.customdurability.messages.edit"), Component.translatable("commands.customdurability.messages.context.edit.tooltip"),
            Component.translatable("commands.customdurability.messages.remove"), Component.translatable("commands.customdurability.messages.context.remove.tooltip"));
    public static TextDecorator.DecoratorList DEFAULT_DECORATOR;

    public static void init(RegistryAccess registry) {
        DEFAULT_DECORATOR = ListCommand.ELEMENT_DISPLAY_DECORATOR.create()
                .then(ELEMENT_BUTTON_DECORATOR)
                .then(SimpleDecorator.NEWLINE);
    }

    public static MutableComponent getListMessage(ModCommands.ItemCommandContext context) {
        if (context.getAdditions().isEmpty()) {
            return Component.translatable("commands.customdurability.list.none");
        }

        var message = Component.translatable("commands.customdurability.context.list");
        message.append("\n");
        message.append(ListCommand.getListBuilder(context.getAdditions(), DEFAULT_DECORATOR));
        message.append("\n");

        message.append(Component.translatable("commands.customdurability.messages.filter"));

        message.append(" ");
        message.append(Component.translatable("commands.customdurability.filter_types.tool").withStyle(ListCommand.getStyleRunCommand("/cd context filter TOOL", Component.translatable("commands.customdurability.filter_types.tool.tooltip"))));
        message.append(" ");
        message.append(Component.translatable("commands.customdurability.filter_types.weapon").withStyle(ListCommand.getStyleRunCommand("/cd context filter WEAPON", Component.translatable("commands.customdurability.filter_types.weapon.tooltip"))));
        message.append(" ");
        message.append(Component.translatable("commands.customdurability.filter_types.armour").withStyle(ListCommand.getStyleRunCommand("/cd context filter ARMOUR", Component.translatable("commands.customdurability.filter_types.armour.tooltip"))));
        message.append(" ");
        message.append(Component.translatable("commands.customdurability.filter_types.other").withStyle(ListCommand.getStyleRunCommand("/cd context filter OTHER", Component.translatable("commands.customdurability.filter_types.other.tooltip"))));

        message.append("\n");

        message.append(Component.translatable("commands.customdurability.messages.add_all"));

        message.append(" ");
        message.append("§7[");
        message.append(Component.translatable("commands.customdurability.messages.confirm").withStyle(ListCommand.getStyleRunCommand("/cd context confirm", Component.translatable("commands.customdurability.messages.context.confirm.tooltip"))));
        message.append("§7]");

        message.append(" ");
        message.append("§7[");
        message.append(Component.translatable("commands.customdurability.messages.cancel").withStyle(ListCommand.getStyleRunCommand("/cd context cancel", Component.translatable("commands.customdurability.messages.context.cancel.tooltip"))));
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
        contextCommand.then(buildFilter());

        return contextCommand;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildFilter() {
        var filterCommand = Commands.literal("filter");

        var typeArg = Commands.argument("type", StringArgumentType.word());
        typeArg.suggests((context, builder) -> {
            builder.suggest("TOOL");
            builder.suggest("WEAPON");
            builder.suggest("ARMOUR");
            builder.suggest("OTHER");
            return builder.buildFuture();
        });

        typeArg.executes(cmdContext -> {
            var type = StringArgumentType.getString(cmdContext, "type");
            var context = ModCommands.getContext(cmdContext);
            if (context == null) {
                cmdContext.getSource().sendFailure(NO_CONTEXT);
                return 0;
            }
            switch (type) {
                case "TOOL" -> context.filter(IS_TOOL);
                case "WEAPON" -> context.filter(IS_WEAPON);
                case "ARMOUR" -> context.filter(IS_ARMOUR);
                case "OTHER" -> context.filter(IS_TOOL.or(IS_WEAPON).or(IS_ARMOUR).negate());
                default -> {
                    cmdContext.getSource().sendFailure(Component.literal("Unknown filter type"));
                    return 0;
                }
            }

            cmdContext.getSource().sendSuccess(() -> ContextCommand.getListMessage(context), false);
            return 1;
        });

        filterCommand.then(typeArg);
        return filterCommand;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildList() {
        var listCommand = Commands.literal("list");

        listCommand.executes(cmdContext -> {
            var source = cmdContext.getSource();

            var context = ModCommands.getContext(cmdContext);
            if (context == null) {
                source.sendSuccess(() -> NO_CONTEXT, false);
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

        setCommand.then(buildSetItem());
        setCommand.then(buildSetAll());

        return setCommand;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildSetAll() {
        var allCommand = Commands.literal("all");
        var durabilityArgument = Commands.argument("durability", IntegerArgumentType.integer(0));
        durabilityArgument.executes(cmdContext -> {
            var durability = IntegerArgumentType.getInteger(cmdContext, "durability");
            var context = ModCommands.getContext(cmdContext);
            if (context == null) {
                cmdContext.getSource().sendFailure(NO_CONTEXT);
                return 0;
            }
            for (Map.Entry<String, Integer> entry : context.getAdditions().entrySet()) {
                entry.setValue(durability);
            }
            var message = Component.translatable("commands.customdurability.context.set.all", durability);
            message.append("\n").append(getListMessage(context));
            cmdContext.getSource().sendSuccess(() -> message, false);
            return 1;
        });
        allCommand.then(durabilityArgument);

        return allCommand;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildSetItem() {
        var itemCommand = Commands.literal("item");

        var itemArgument = Commands.argument("item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM));
        itemArgument.suggests(new ItemSuggestionProvider());

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
        itemCommand.then(itemArgument);

        return itemCommand;
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
