package me.gravityio.customdurability.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CancelCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var confirm = Commands.literal("cancel");

        confirm.requires(CommandSourceStack::isPlayer);
        confirm.executes(cmdContext -> {
            var context = ModCommands.getContext(cmdContext);
            if (context == null) return 0;
            context.onCancel(cmdContext);
            return 1;
        });

        return confirm;
    }
}
