package me.gravityio.customdurability.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.gravityio.customdurability.ModConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ArmorMultiplierCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var armorMultiplier = Commands.literal("armorMultiplier");

        armorMultiplier.executes(context -> {
            var source = context.getSource();
            Component text;
            if (ModConfig.INSTANCE.armor_is_durability_multiplier) {
                text = Component.translatable("commands.customdurability.armor_multiplier.on");
            } else {
                text = Component.translatable("commands.customdurability.armor_multiplier.off");
            }
            source.sendSuccess(() -> text, false);
            return 1;
        });

        var onOffArg = Commands.argument("onOff", BoolArgumentType.bool());
        onOffArg.executes(context -> {
            var source = context.getSource();
            var onOff = BoolArgumentType.getBool(context, "onOff");
            ModConfig.INSTANCE.setArmorMultiplierEnabled(onOff);
            ModConfig.INSTANCE.save();
            Component text;
            if (onOff) {
                text = Component.translatable("commands.customdurability.armor_multiplier.set.on");
            } else {
                text = Component.translatable("commands.customdurability.armor_multiplier.set.off");
            }
            source.sendSuccess(() -> text, false);
            return 1;
        });

        armorMultiplier.then(onOffArg);
        return armorMultiplier;
    }
}
