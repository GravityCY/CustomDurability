package me.gravityio.customdurability.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.gravityio.customdurability.ModConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class ListCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
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
