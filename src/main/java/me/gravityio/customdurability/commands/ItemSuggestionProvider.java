package me.gravityio.customdurability.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.gravityio.customdurability.CustomDurabilityMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class ItemSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        var input = builder.getInput().substring(builder.getStart());
        for (TagKey<Item> damageableTag : ModCommands.getDamageableTags()) {
            var str = "#" + damageableTag.location();
            if (!str.startsWith(input)) continue;
            builder.suggest(str);
        }
        for (Holder.Reference<Item> allDamageable : CustomDurabilityMod.iterateDamageables()) {
            var str = allDamageable.key().location().toString();
            if (!str.startsWith(input)) continue;
            builder.suggest(str);
        }
        return builder.buildFuture();
    }
}
