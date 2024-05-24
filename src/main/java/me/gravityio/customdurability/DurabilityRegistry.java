package me.gravityio.customdurability;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public static LinkedHashMap<String, Integer> getDurabilityOverrides() {
        return durabilityOverrides;
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
}
