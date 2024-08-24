package me.gravityio.customdurability.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.customdurability.CustomDurabilityMod;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ModCommands {
    private static final HashMap<UUID, ItemCommandContext> contexts = new HashMap<>();

    public static @NotNull ItemCommandContext getContextNew(CommandContext<CommandSourceStack> source) {
        var player = source.getSource().getPlayer();
        var context = contexts.get(player.getUUID());
        if (context == null) {
            setContext(source, context = new ItemCommandContext());
        }
        return context;
    }

    public static ItemCommandContext getContext(CommandContext<CommandSourceStack> source) {
        var player = source.getSource().getPlayer();
        return contexts.get(player.getUUID());
    }

    public static void removeContext(CommandContext<CommandSourceStack> cmdContext) {
        contexts.remove(cmdContext.getSource().getPlayer().getUUID());
    }

    public static void setContext(CommandContext<CommandSourceStack> cmdContext, @Nullable ItemCommandContext context) {
        if (context == null) {
            contexts.remove(cmdContext.getSource().getPlayer().getUUID());
        } else {
            contexts.put(cmdContext.getSource().getPlayer().getUUID(), context);
        }
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext registry, Commands.CommandSelection environment) {
        var cd = Commands.literal("cd");
        cd.requires(source -> source.hasPermission(4));

        cd.then(ArmorMultiplierCommand.build())
                .then(ListCommand.build())
                .then(ClearCommand.build())
                .then(SetCommand.build())
                .then(TempCommand.build());

        return cd;
    }

    public static class ItemCommandContext {
        private final HashMap<String, Integer> additionsMap = new HashMap<>();

        public boolean hasAddition(String str) {
            return this.additionsMap.containsKey(str);
        }

        public void setAddition(int durability, String str) {
            this.additionsMap.put(str, durability);
        }

        public void setAdditionAll(int durability, String... str) {
            for (String s : str) {
                this.additionsMap.put(s, durability);
            }
        }

        public boolean removeAddition(String str) {
            return this.additionsMap.remove(str) != null;
        }

        public int onConfirm(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSuccess(() -> Component.literal("Added context to the config!").withStyle(ChatFormatting.GREEN), false);
            CustomDurabilityMod.setDurabilityOverrides(this.additionsMap);
            return 1;
        }

        public int onCancel(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSuccess(() -> Component.literal("Cancelled the Context!").withStyle(ChatFormatting.RED), false);
            return 1;
        }

        public int display(CommandContext<CommandSourceStack> context) {
            var message = Component.literal("Temporary Additions:\n");
            for (Map.Entry<String, Integer> entry : this.additionsMap.entrySet()) {
                var id = entry.getKey();
                var dura = entry.getValue();
                var format = "%s: %d\n".formatted(id, dura);
                message.append(Component.literal(format));
            }
            context.getSource().sendSuccess(() -> message, false);
            return 1;
        }

        public HashMap<String, Integer> getAdditions() {
            return this.additionsMap;
        }

        private enum ContextType {
            CLEAR, SET
        }
    }
}
