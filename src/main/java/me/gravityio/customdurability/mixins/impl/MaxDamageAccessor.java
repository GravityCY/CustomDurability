package me.gravityio.customdurability.mixins.impl;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Make the item's max damage mutable
 */
@Mixin(Item.class)
public interface MaxDamageAccessor {
    @Accessor("maxDamage")
    @Mutable
    int getMaxDamage();
    @Accessor("maxDamage")
    @Mutable
    void setMaxDamage(int maxDamage);
}
