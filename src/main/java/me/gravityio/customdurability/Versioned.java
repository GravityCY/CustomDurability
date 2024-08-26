package me.gravityio.customdurability;

import me.gravityio.customdurability.mixins.inter.DamageItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

//? if >=1.20.5 {
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
//?} else {
/*import net.minecraft.world.item.ArmorMaterials;
*///?}

public class Versioned {

    public static ResourceLocation parseId(String path) {
        //? if >=1.21 {
        return ResourceLocation.parse(path);
         //?} else {
        /*return new ResourceLocation(path);
        *///?}
    }

    public static ResourceLocation parseId(String id, String path) {
        //? if >=1.21 {
        return ResourceLocation.fromNamespaceAndPath(id, path);
         //?} else {
        /*return new ResourceLocation(id, path);
        *///?}
    }

    public static boolean isDamageable(Item item) {
        //? if >=1.20.5 {
        return item.components().has(DataComponents.MAX_DAMAGE);
        //?} else {
        /*return item.getMaxDamage() != 0;
        *///?}
    }

    public static int getArmorDurability(ArmorItem armorItem, int baseDurability) {
        //? if >=1.20.5 {
        return armorItem.getType().getDurability(baseDurability);
         //?} else {
        /*return ArmorMaterials.HEALTH_FUNCTION_FOR_TYPE.get(armorItem.getType()) * baseDurability;
        *///?}
    }

    public static void setDurability(Item item, int durability) {
        DamageItem dItem = (DamageItem) item;
        //? if >=1.20.5 {
        var components = (DataComponentMap.Builder.SimpleMap) item.components();
        if (dItem.customDurability$getOriginalMaxDamage() == null) {
            dItem.customDurability$setOriginalMaxDamage(components.get(DataComponents.MAX_DAMAGE));
        }
        components.map().put(DataComponents.MAX_DAMAGE, durability);
        //?} else {
        /*if (dItem.customDurability$getOriginalMaxDamage() == null) {
            dItem.customDurability$setOriginalMaxDamage(dItem.customDurability$getMaxDamage());
        }
        dItem.customDurability$setMaxDamage(durability);
        *///?}
    }
}
