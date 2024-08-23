package me.gravityio.customdurability.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ConfirmCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var confirm = Commands.literal("confirm");

        confirm.requires(CommandSourceStack::isPlayer);
        confirm.executes(cmdContext -> {
            var context = ModCommands.getContext(cmdContext);
            if (context == null) return 0;
            context.onConfirm(cmdContext);
            return 1;
        });

        return confirm;
    }
}
