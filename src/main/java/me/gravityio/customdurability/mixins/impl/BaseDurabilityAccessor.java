package me.gravityio.customdurability.mixins.impl;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;

@Mixin(ArmorMaterials.class)
public interface BaseDurabilityAccessor {
    @Accessor("BASE_DURABILITY")
    static EnumMap<ArmorItem.Type, Integer> BASE_DURABILITY() {
        throw new AssertionError();
    }
}
