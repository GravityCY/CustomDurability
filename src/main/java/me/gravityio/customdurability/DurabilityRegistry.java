package me.gravityio.customdurability;

import net.minecraft.network.PacketByteBuf;

import java.util.*;
import java.util.function.BiConsumer;

public class DurabilityRegistry {
    private static final LinkedHashMap<String, Integer> durabilityOverrides = new LinkedHashMap<>();
    public static void register(String itemId, int durability) {
        durabilityOverrides.put(itemId, durability);
    }

    public static int get(String itemId) {
        return durabilityOverrides.get(itemId);
    }

    public static void forEach(BiConsumer<String, Integer> consumer) {
        durabilityOverrides.forEach(consumer);
    }

    public static void clear() {
        durabilityOverrides.clear();
    }

    public static List<String> setFrom(Map<String, Integer> newDurabilities) {
        List<String> removed = new ArrayList<>();
        durabilityOverrides.forEach((k, v) -> {
            if (newDurabilities.containsKey(k)) return;
            removed.add(k);
        });
        clear();
        newDurabilities.forEach(DurabilityRegistry::register);
        return removed;
    }

    public static void toPacket(PacketByteBuf buf) {
        buf.writeMap(durabilityOverrides, PacketByteBuf::writeString, PacketByteBuf::writeInt);
    }

    public static List<String> fromPacket(PacketByteBuf buf) {
        var newMap = buf.readMap(PacketByteBuf::readString, PacketByteBuf::readInt);
        return DurabilityRegistry.setFrom(newMap);
    }
}
