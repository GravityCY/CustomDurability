package me.gravityio.customdurability.mixins.impl;

import me.gravityio.customdurability.mixins.inter.DamageItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.*;

@Mixin(Item.class)
public class ItemMixin implements DamageItem {
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
}
