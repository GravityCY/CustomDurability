package me.gravityio.customdurability.mixins.inter;

public interface DamageItem {
    Integer customDurability$getOriginalMaxDamage();
    void customDurability$setOriginalMaxDamage(int originalMaxDamage);
    //? if <1.20.5 {
    /*int customDurability$getMaxDamage();
    void customDurability$setMaxDamage(int durability);
    *///?}
}
