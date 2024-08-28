package me.gravityio.customdurability.decorator;

import me.gravityio.customdurability.Versioned;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.Map;

public class ElementDisplayDecorator extends TextDecorator {

    Registry<Item> itemRegistry;

    public ElementDisplayDecorator(RegistryAccess registry) {
        super(registry);
        this.itemRegistry = registry.registry(Registries.ITEM).orElseThrow();
    }

    @Override
    public void onDecorate(Map.Entry<String, Integer> entry, MutableComponent component) {
        var id = entry.getKey();
        var durability = entry.getValue();

        if (id.charAt(0) != '#') {
            var res = Versioned.parseId(id);
            var item = this.itemRegistry.get(res);
            if (item instanceof ArmorItem armor) {
                var armorDurability = Versioned.getArmorDurability(armor, durability);
                String format = " - '§b%s§r': %d (%d)".formatted(id, durability, armorDurability);
                component.append(format);
                return;
            }
        }
        var format = " - '§b%s§r': %d".formatted(id, durability);
        component.append(format);
    }
}
