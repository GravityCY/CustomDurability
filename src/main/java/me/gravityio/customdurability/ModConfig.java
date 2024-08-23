package me.gravityio.customdurability;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses my scuffed YACL Auto Config Library
 */
public class ModConfig {

    public static final char SEPARATOR = ',';
    public static ModConfig INSTANCE = new ModConfig();
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("customdurability.json");
    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setLenient()
            .setPrettyPrinting()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    public LinkedHashMap<String, Integer> durability_overrides = Util.make(new LinkedHashMap<>(), map -> {
        LinkedHashMap<String, Integer> tools = new LinkedHashMap<>();
        tools.put("#cd:tools/wood", 59);

        LinkedHashMap<String, Integer> armor = new LinkedHashMap<>();
        armor.put("#cd:armor/leather", 5);

        LinkedHashMap<String, Integer> random = new LinkedHashMap<>();
        random.put("bow", 384);

        map.putAll(tools);
        map.putAll(armor);
        map.putAll(random);
    });

    public boolean armor_is_durability_multiplier = true;

    public void armor_is_durability_multiplier(boolean val) {
        this.armor_is_durability_multiplier = val;
        ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
    }

    public List<String> getDurabilityOverrides() {
        List<String> overrides = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : this.durability_overrides.entrySet()) {
            var temp = entry.getKey() + SEPARATOR + entry.getValue();
            overrides.add(temp);
        }

        return overrides;
    }

    public void setDurabilityOverride(String idOrTagStr, int value) {
        var idWithTag = new IdentifierWithTag(idOrTagStr);
        this.removeDuplicates(idWithTag);
        this.durability_overrides.put(idWithTag.toShortString(), value);
    }

    public void removeDurabilityOverrideRaw(String idOrTagStr) {
        var idWithTag = new IdentifierWithTag(idOrTagStr);
        this.removeDuplicates(idWithTag);
        this.durability_overrides.remove(idWithTag.toShortString()) ;
    }

    public boolean hasDurabilityOverride(String idOrTagStr) {
        var idWithTag = new IdentifierWithTag(idOrTagStr);
        var shortBool = this.durability_overrides.containsKey(idWithTag.toShortString());
        var longBool = this.durability_overrides.containsKey(idWithTag.toFullString());
        return shortBool || longBool;
    }

    public void removeDuplicates(IdentifierWithTag idOrTag) {
        if (!idOrTag.isDefault()) return;
        this.durability_overrides.remove(idOrTag.toFullString());
    }

    public void save() {
        try {
            Files.writeString(PATH, GSON.toJson(this), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            CustomDurabilityMod.LOGGER.info("Saved config successfully.");
        } catch (IOException e) {
            CustomDurabilityMod.LOGGER.error("Failed to save config: {}", e.getMessage());
        }
    }

    public void load() {
        try(var fr = new FileReader(PATH.toFile())) {
            var other = GSON.fromJson(fr, ModConfig.class);
            this.armor_is_durability_multiplier = other.armor_is_durability_multiplier;
            this.durability_overrides = other.durability_overrides;
            CustomDurabilityMod.LOGGER.info("Loaded config successfully.");
        } catch (FileNotFoundException e) {
            this.save();
        } catch (JsonSyntaxException | IOException e) {
            CustomDurabilityMod.LOGGER.error("Failed to load config: {}", e.getMessage());
        }
    }
}
