package me.gravityio.customdurability;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.GsonConfigInstance;
import me.gravityio.yaclutils.annotations.Config;
import me.gravityio.yaclutils.annotations.elements.ScreenOption;
import me.gravityio.yaclutils.api.ConfigFrame;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.nio.file.Path;
import java.util.*;

@Config(namespace = CustomDurabilityMod.MOD_ID)
public class ModConfig implements ConfigFrame<ModConfig> {

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

    public static ModConfig INSTANCE;
    @ConfigEntry
    public LinkedHashMap<String, Integer> durability_overrides = Util.make(new LinkedHashMap<>(), map -> {
        LinkedHashMap<String, Integer> tools = new LinkedHashMap<>();
        tools.put("#customdurability:tools/wood", 59);
        tools.put("#customdurability:tools/stone", 131);
        tools.put("#customdurability:tools/iron", 250);
        tools.put("#customdurability:tools/gold", 32);
        tools.put("#customdurability:tools/diamond", 1561);
        tools.put("#customdurability:tools/netherite", 2031);

        LinkedHashMap<String, Integer> armor = new LinkedHashMap<>();
        armor.put("#customdurability:armor/leather", 5);
        armor.put("#customdurability:armor/chainmail", 15);
        armor.put("#customdurability:armor/iron", 15);
        armor.put("#customdurability:armor/gold", 7);
        armor.put("#customdurability:armor/diamond", 33);
        armor.put("#customdurability:armor/netherite", 37);
        armor.put("turtle_helmet", 25);

        LinkedHashMap<String, Integer> random = new LinkedHashMap<>();
        random.put("bow", 384);
        random.put("elytra", 432);
        random.put("flint_and_steel", 64);
        random.put("shears", 238);
        random.put("shield", 336);

        map.putAll(tools);
        map.putAll(armor);
        map.putAll(random);
    });
    public List<String> getDurabilityOverrides() {
        List<String> overrides = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : durability_overrides.entrySet()) {
            var temp = entry.getKey() + ";" + entry.getValue();
            overrides.add(temp);
        }

        return overrides;
    }
    public void setDurabilityOverrides(List<String> overrides) {
        durability_overrides.clear();
        for (String override : overrides) {
            var split = override.split(";", 2);
            if (split.length != 2) continue;
            var key = split[0];
            var value = Integer.parseInt(split[1]);
            durability_overrides.put(key, value);
        }
        ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
    }
    @ConfigEntry
    @ScreenOption(index = 0)
    public boolean armor_is_durability_multiplier = true;
}
