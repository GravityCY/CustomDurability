package me.gravityio.customdurability.mixins.impl;

import me.gravityio.customdurability.mixins.inter.DamageItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.*;

@Mixin(Item.class)
public class ItemMixin implements DamageItem {
    //? if <1.20.5 {
    /*@Mutable
    @Shadow @Final private int maxDamage;
    *///?}
    @Unique
    Integer originalMaxDamage = null;
    @Override
    public Integer customDurability$getOriginalMaxDamage() {
        return this.originalMaxDamage;
    }

    @Override
    public void customDurability$setOriginalMaxDamage(int originalMaxDamage) {
        this.originalMaxDamage = originalMaxDamage;
    }

    //? if <1.20.5 {
    /*@Override
    public int customDurability$getMaxDamage() {
        return this.maxDamage;
    }

    @Override
    public void customDurability$setMaxDamage(int durability) {
        this.maxDamage = durability;
    }
    *///?}
}
