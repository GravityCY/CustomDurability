package me.gravityio.customdurability;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.ibm.icu.text.Normalizer2;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import me.gravityio.yaclutils.annotations.Config;
import me.gravityio.yaclutils.annotations.Setter;
import me.gravityio.yaclutils.annotations.elements.ScreenOption;
import me.gravityio.yaclutils.api.ConfigFrame;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.nio.file.Path;
import java.util.*;

/**
 * Uses my scuffed YACL Auto Config Library
 */
@Config(namespace = CustomDurabilityMod.MOD_ID)
public class ModConfig implements ConfigFrame<ModConfig> {

    public static final char SEPARATOR = ',';

    public static ModConfig INSTANCE;
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("customdurability.json");
    public static final GsonBuilder GSON_BUILDER = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    public static GsonConfigInstance<ModConfig> GSON = GsonConfigInstance.createBuilder(ModConfig.class)
            .setPath(PATH)
            .overrideGsonBuilder(GSON_BUILDER)
            .build();

    @Override
    public void onBeforeBuildCategory(String category, ModConfig defaults, ConfigCategory.Builder builder) {
        var list = ListOption.<String>createBuilder()
                .name(Text.translatable("yacl.customdurability.overrides.label"))
                .description(OptionDescription.of(Text.translatable("yacl.customdurability.overrides.description")))
                .controller(StringControllerBuilder::create)
                .initial("")
                .binding(defaults.getDurabilityOverrides(), this::getDurabilityOverrides, this::setDurabilityOverrides);
        builder.group(list.build());
    }

    @Override
    public void onFinishBuilding(ModConfig defaults, YetAnotherConfigLib.Builder builder) {
        builder.save(() -> {
            GSON.save();
            ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
        });
    }

    @ConfigEntry
    public LinkedHashMap<String, Integer> durability_overrides = Util.make(new LinkedHashMap<>(), map -> {
        LinkedHashMap<String, Integer> tools = new LinkedHashMap<>();
        tools.put("#customdurability:tools/wood", 59);

        LinkedHashMap<String, Integer> armor = new LinkedHashMap<>();
        armor.put("#customdurability:armor/leather", 5);

        LinkedHashMap<String, Integer> random = new LinkedHashMap<>();
        random.put("bow", 384);

        map.putAll(tools);
        map.putAll(armor);
        map.putAll(random);
    });

    @ConfigEntry
    @ScreenOption(index = 0)
    public boolean armor_is_durability_multiplier = true;

    @Setter
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

    public void setDurabilityOverrides(List<String> overrides) {
        this.durability_overrides.clear();
        for (String override : overrides) {
            this.setDurabilityOverride(override);
        }
    }

    public void setDurabilityOverride(String idOrTagAndDurability) {
        var splitArray = idOrTagAndDurability.split(String.valueOf(SEPARATOR), 2);
        if (splitArray.length != 2) return;
        var key = splitArray[0];
        var second = splitArray[1];
        this.setDurabilityOverride(key, second);
    }

    public void setDurabilityOverride(String idOrTag, String value) {
        if (!isValidInt(value)) {
            CustomDurabilityMod.LOGGER.error("Invalid Durability Entered '%s'! Expected a Whole Number!".formatted(value));
            return;
        }
        this.setDurabilityOverride(idOrTag, Integer.parseInt(value));
    }

    public void setDurabilityOverride(String idOrTagStr, int value) {
        var idWithTag = new IdentifierWithTag(idOrTagStr);
        this.removeDuplicates(idWithTag);
        this.durability_overrides.put(idWithTag.toShortString(), value);
    }

    public void removeDurabilityOverride(String idOrTagStr) {
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

    public boolean isValid(String override) {
        var split = override.split(String.valueOf(SEPARATOR), 2);
        if (split.length != 2) return false;
        if (isValidInt(split[1])) return false;
        return true;
    }

    private static boolean isValidInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
