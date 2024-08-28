package me.gravityio.customdurability.decorator;

import me.gravityio.customdurability.commands.ListCommand;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;

public class ElementButtonDecorator extends TextDecorator {

    private final String clearCommandFormat;
    private final String setCommandFormat;
    private final MutableComponent edit;
    private final MutableComponent editTooltip;
    private final MutableComponent remove;
    private final MutableComponent removeTooltip;

    public ElementButtonDecorator(String setCommandFormat, String clearCommandFormat,
                                  MutableComponent edit, MutableComponent editTooltip,
                                  MutableComponent remove, MutableComponent removeTooltip) {
        this.setCommandFormat = setCommandFormat;
        this.clearCommandFormat = clearCommandFormat;
        this.edit = edit;
        this.editTooltip = editTooltip;
        this.remove = remove;
        this.removeTooltip = removeTooltip;
    }

    @Override
    public void onDecorate(Map.Entry<String, Integer> entry, MutableComponent component) {
        var id = entry.getKey();
        var durability = entry.getValue();

        var setCommand = this.setCommandFormat.formatted(id, durability);
        var clearCommand = this.clearCommandFormat.formatted(id);

        component.append(" ");
        component.append("§7[");
        component.append(this.edit.copy().withStyle(ListCommand.getStyleSuggestCommand(setCommand, this.editTooltip)));
        component.append("§7]");

        component.append(" ");
        component.append("§7[");
        component.append(this.remove.copy().withStyle(ListCommand.getStyleRunCommand(clearCommand, this.removeTooltip)));
        component.append("§7]");
    }

}
