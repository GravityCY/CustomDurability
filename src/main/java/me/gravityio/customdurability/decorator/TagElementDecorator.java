package me.gravityio.customdurability.decorator;

import me.gravityio.customdurability.Versioned;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Map;

public class TagElementDecorator extends TextDecorator {
    private final Registry<Item> itemRegistry;
    private final TextDecorator elementDisplayDecorator;

    public TagElementDecorator(RegistryAccess registry, TextDecorator elementDisplayDecorator) {
        super(registry);
        this.itemRegistry = super.registry.registry(Registries.ITEM).orElseThrow();
        this.elementDisplayDecorator = elementDisplayDecorator;
    }

    @Override
    protected void onDecorate(Map.Entry<String, Integer> entry, MutableComponent component) {
        var id = entry.getKey();
        var durability = entry.getValue();
        if (id.charAt(0) != '#') return;
        var tag = TagKey.create(this.itemRegistry.key(), Versioned.parseId(id.substring(1)));
        var list = this.itemRegistry.getTag(tag).orElse(null);
        if (list == null) return;
        for (Holder<Item> itemHolder : list) {
            var resourceKey = itemHolder.unwrap().left().orElse(null);
            if (resourceKey == null) continue;
            var itemId = resourceKey.location().toString();
            component.append("  ");
            this.elementDisplayDecorator.decorate(Map.entry(itemId, durability), component);
            component.append("\n");
        }
    }
}
