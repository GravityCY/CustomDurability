package me.gravityio.customdurability.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.gravityio.customdurability.ModConfig;
import me.gravityio.customdurability.decorator.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.*;

import java.util.HashMap;
import java.util.Map;

public class ListCommand {
    public static TextDecorator ELEMENT_DISPLAY_DECORATOR;
    public static TextDecorator ELEMENT_BUTTONS_DECORATOR = new ElementButtonDecorator(
            "/cd set item %s %d", "/cd clear %s",
            Component.translatable("commands.customdurability.messages.edit"), Component.translatable("commands.customdurability.messages.edit.tooltip"),
            Component.translatable("commands.customdurability.messages.remove"), Component.translatable("commands.customdurability.messages.remove"));
    public static TagElementDecorator TAG_LIST_DECORATOR;
    public static TextDecorator.DecoratorList DEFAULT_DECORATOR;

    public static Style getStyleRunCommand(String command, Component tooltip) {
        return Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip));
    }

    public static Style getStyleSuggestCommand(String command, Component tooltip) {
        return Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip));
    }

    public static MutableComponent getListBuilder(HashMap<String, Integer> data, TextDecorator.DecoratorList decorator) {
        var message = Component.literal("");
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            decorator.decorate(entry, message);
        }
        return message;
    }

    public static Component getListMessage() {
        if (ModConfig.INSTANCE.durability_overrides.isEmpty()) {
            return Component.translatable("commands.customdurability.list.none");
        }
        var message = Component.translatable("commands.customdurability.list");
        message.append("\n");
        message.append(getListBuilder(ModConfig.INSTANCE.durability_overrides, DEFAULT_DECORATOR));
        return message;
    }

    public static void init(RegistryAccess registry) {
        ELEMENT_DISPLAY_DECORATOR = new ElementDisplayDecorator(registry);
        TAG_LIST_DECORATOR = new TagElementDecorator(registry, ELEMENT_DISPLAY_DECORATOR);
        DEFAULT_DECORATOR = ELEMENT_DISPLAY_DECORATOR.create().then(ELEMENT_BUTTONS_DECORATOR).then(SimpleDecorator.NEWLINE).then(TAG_LIST_DECORATOR);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var list = Commands.literal("list");

        list.executes(context -> {
            var source = context.getSource();
            source.sendSuccess(ListCommand::getListMessage, false);
            return 1;
        });

        return list;
    }
}
