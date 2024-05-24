package me.gravityio.customdurability.mixins.inter;

public interface DamageItem {
    Integer customDurability$getOriginalMaxDamage();
    void customDurability$setOriginalMaxDamage(int originalMaxDamage);
}
