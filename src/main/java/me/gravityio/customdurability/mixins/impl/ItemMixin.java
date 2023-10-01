package me.gravityio.customdurability.mixins.impl;

import me.gravityio.customdurability.mixins.inter.DamageItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.*;

@Mixin(Item.class)
public class ItemMixin implements DamageItem {
    @Mutable
    @Shadow @Final private int maxDamage;
    @Unique
    Integer originalMaxDamage = null;
    @Override
    public Integer customDurability$getOriginalMaxDamage() {
        return originalMaxDamage;
    }

    @Override
    public void customDurability$setOriginalMaxDamage(int originalMaxDamage) {
        this.originalMaxDamage = originalMaxDamage;
    }

    @Override
    public int customDurability$getMaxDamage() {
        return this.maxDamage;
    }

    @Override
    public void customDurability$setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }
}
