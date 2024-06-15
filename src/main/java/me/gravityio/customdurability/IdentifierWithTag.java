package me.gravityio.customdurability;

import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class IdentifierWithTag {
    Identifier id;
    boolean isTag;

    public IdentifierWithTag(Identifier id, boolean isTag) {
        this.id = id;
        this.isTag = isTag;
    }

    public IdentifierWithTag(String key) {
        this.isTag = key.charAt(0) == '#';
        if (this.isTag) {
            this.id = Identifier.of(key.substring(1));
        } else {
            this.id = Identifier.of(key);
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

    public static IdentifierWithTag from(RegistryPredicateArgumentType.RegistryPredicate<?> item) {
        boolean isTag = item.getKey().right().isPresent();
        Identifier id = item.getKey().map(RegistryKey::getValue, TagKey::id);
        return new IdentifierWithTag(id, isTag);
    }
}