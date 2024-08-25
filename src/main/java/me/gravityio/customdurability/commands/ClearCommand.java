package me.gravityio.customdurability.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.ModConfig;
import me.gravityio.customdurability.ModEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

public class ClearCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        var clear = Commands.literal("clear");

        clear.executes(context -> {
            ModConfig.INSTANCE.durability_overrides.clear();
            ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
            ModConfig.INSTANCE.save();
            context.getSource().sendSuccess(() -> Component.translatable("commands.customdurability.clear.all"), false);
            return 1;
        });

        var itemArg = Commands.argument("item", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM));
        itemArg.executes(cmdContext -> {
            var source = cmdContext.getSource();
            var item = ResourceOrTagKeyArgument.getResourceOrTagKey(cmdContext, "item", Registries.ITEM, new DynamicCommandExceptionType(o -> () -> ""));
            var str = item.asPrintable();

            if (!ModConfig.INSTANCE.hasDurabilityOverride(str)) {
                source.sendSuccess(() -> Component.translatable("commands.customdurability.clear.not_found"), false);
                return 1;
            }
            CustomDurabilityMod.removeDurabilityOverride(str);
            var message = Component.translatable("commands.customdurability.clear.remove", str);
            message.append("\n");
            message.append(ListCommand.getListMessage());
            source.sendSuccess(() -> message, false);
            return 1;
        });

        clear.then(itemArg);
        return clear;
    }


}
