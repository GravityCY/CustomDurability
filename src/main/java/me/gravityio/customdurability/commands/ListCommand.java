package me.gravityio.customdurability.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.gravityio.customdurability.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ListCommand {
    public static BiConsumer<Map.Entry<String, Integer>, MutableComponent> ELEMENT_DISPLAY = (entry, message) -> {
        var id = entry.getKey();
        var durability = entry.getValue();

        var string = " - '§b%s§r': %d".formatted(id, durability);
        message.append(string);
    };
    public static BiConsumer<Map.Entry<String, Integer>, MutableComponent> ELEMENT_BUTTONS_MODIFIER = getElementButtons("/cd set item %s %d", "/cd clear %s", Component.translatable("commands.customdurability.messages.edit.tooltip"), Component.translatable("commands.customdurability.messages.remove.tooltip"));
    public static BiConsumer<Map.Entry<String, Integer>, MutableComponent> NEWLINE = (entry, message) -> message.append("\n");
    public static BiConsumer<Map.Entry<String, Integer>, MutableComponent> DEFAULT_MODIFIER = ELEMENT_DISPLAY.andThen(ELEMENT_BUTTONS_MODIFIER).andThen(NEWLINE);

    public static Style getStyleRunCommand(String command, Component tooltip) {
        return Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip));
    }

    public static Style getStyleSuggestCommand(String command, Component tooltip) {
        return Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip));
    }

    public static BiConsumer<Map.Entry<String, Integer>, MutableComponent> getElementButtons(String setCommandFormat, String clearCommandFormat, Component setTooltip, Component removeTooltip) {
        return (entry, message) -> {
            var id = entry.getKey();
            var durability = entry.getValue();

            var setCommand = setCommandFormat.formatted(id, durability);
            var clearCommand = clearCommandFormat.formatted(id);

            message.append(" ");
            message.append("§7[");
            message.append(Component.translatable("commands.customdurability.messages.edit").withStyle(getStyleSuggestCommand(setCommand, setTooltip)));
            message.append("§7]");

            message.append(" ");
            message.append("§7[");
            message.append(Component.translatable("commands.customdurability.messages.remove").withStyle(getStyleRunCommand(clearCommand, removeTooltip)));
            message.append("§7]");
        };
    }

    public static MutableComponent getListBuilder(HashMap<String, Integer> data, BiConsumer<Map.Entry<String, Integer>, MutableComponent> elementModifier) {
        var message = Component.literal("");
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            elementModifier.accept(entry, message);
        }
        return message;
    }

    public static Component getListMessage() {
        if (ModConfig.INSTANCE.durability_overrides.isEmpty()) {
            return Component.translatable("commands.customdurability.list.none");
        }
        var message = Component.translatable("commands.customdurability.list");
        message.append("\n");
        message.append(getListBuilder(ModConfig.INSTANCE.durability_overrides, DEFAULT_MODIFIER));
        return message;
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
