package me.gravityio.customdurability.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.ModConfig;
import me.gravityio.customdurability.ModEvents;
import me.gravityio.customdurability.WildcardMatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;

import java.util.*;

public class ModCommands {
    private static final HashMap<UUID, ItemCommandContext> contexts = new HashMap<>();

    public static ItemCommandContext getContext(CommandContext<CommandSourceStack> source) {
        var player = source.getSource().getPlayer();
        if (player == null) return null;
        return contexts.get(player.getUUID());
    }

    public static void setContext(CommandContext<CommandSourceStack> context, ItemCommandContext setItemCommandContext) {
        contexts.put(context.getSource().getPlayer().getUUID(), setItemCommandContext);
    }

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext registry, Commands.CommandSelection environment) {
        var cd = Commands.literal("cd");
        cd.requires(source -> source.hasPermission(4));

        var armorMultiplier = ArmorMultiplierCommand.build();
        var list = ListCommand.build();
        var clear = ClearCommand.build();
        var set = SetCommand.build();
        var confirm = ConfirmCommand.build();
        var cancel = CancelCommand.build();

        cd.then(armorMultiplier)
                .then(list)
                .then(clear)
                .then(set)
                .then(confirm)
                .then(cancel);

        return cd;
    }

    public abstract static class ItemCommandContext {
        public final List<String> toAdd;

        public ItemCommandContext(List<String> toAdd) {
            this.toAdd = toAdd;
        }

        abstract int onConfirm(CommandContext<CommandSourceStack> context);
        abstract int onCancel(CommandContext<CommandSourceStack> context);

        private enum ContextType {
            CLEAR, SET
        }
    }

    public static class ClearItemCommandContext extends ItemCommandContext {

        public ClearItemCommandContext(List<String> toAdd) {
            super(toAdd);
        }

        @Override
        int onConfirm(CommandContext<CommandSourceStack> context) {
            return 0;
        }

        @Override
        int onCancel(CommandContext<CommandSourceStack> context) {
            return 0;
        }
    }

    public static class SetItemCommandContext extends ItemCommandContext {

        private final int durability;

        public SetItemCommandContext(List<String> toAdd, int durability) {
            super(toAdd);
            this.durability = durability;
        }

        @Override
        int onConfirm(CommandContext<CommandSourceStack> context) {
            return 0;
        }

        @Override
        int onCancel(CommandContext<CommandSourceStack> context) {
            return 0;
        }
    }

}
