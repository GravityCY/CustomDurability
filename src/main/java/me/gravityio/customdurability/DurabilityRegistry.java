package me.gravityio.customdurability;

import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class DurabilityRegistry {
    private static final Map<String, Integer> durability_overrides = new HashMap<>();
    public static void register(String itemId, int durability) {
        durability_overrides.put(itemId, durability);
    }

    public static int get(String itemId) {
        return durability_overrides.get(itemId);
    }

    public static void forEach(BiConsumer<String, Integer> consumer) {
        durability_overrides.forEach(consumer);
    }

    public static void clear() {
        durability_overrides.clear();
    }

    public static void setFrom(Map<String, Integer> map) {
        clear();
        map.forEach(DurabilityRegistry::register);
    }

    public static void toPacket(PacketByteBuf buf) {
        buf.writeMap(durability_overrides, PacketByteBuf::writeString, PacketByteBuf::writeInt);
    }

    public static void fromPacket(PacketByteBuf buf) {
        clear();
        var newMap = buf.readMap(PacketByteBuf::readString, PacketByteBuf::readInt);
        DurabilityRegistry.setFrom(newMap);
    }
}
