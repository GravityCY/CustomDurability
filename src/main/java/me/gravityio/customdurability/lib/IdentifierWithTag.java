package me.gravityio.customdurability.lib;

import me.gravityio.customdurability.Versioned;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class IdentifierWithTag {
    ResourceLocation id;
    boolean isTag;

    public IdentifierWithTag(ResourceLocation id, boolean isTag) {
        this.id = id;
        this.isTag = isTag;
    }

    public IdentifierWithTag(String key) {
        this.isTag = key.charAt(0) == '#';
        if (this.isTag) {
            this.id = Versioned.parseId(key.substring(1));
        } else {
            this.id = Versioned.parseId(key);
        }
    }

    public boolean isDefault() {
        return this.id.getNamespace().equals("minecraft");
    }

    public String toFullString() {
        return this.isTag ? "#" + this.id.toString() : this.id.toString();
    }

    public String toShortString() {
        String str = this.isDefault() ? this.id.getPath() : this.id.toString();
        return this.isTag ? "#" + str : str;
    }

    public static IdentifierWithTag from(ResourceOrTagKeyArgument.Result<?> item) {
        boolean isTag = item.unwrap().right().isPresent();
        ResourceLocation id = item.unwrap().map(ResourceKey::location, TagKey::location);
        return new IdentifierWithTag(id, isTag);
    }
}