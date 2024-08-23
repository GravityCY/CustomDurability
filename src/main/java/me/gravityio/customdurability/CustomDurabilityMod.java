package me.gravityio.customdurability;

import me.gravityio.customdurability.commands.ModCommands;
import me.gravityio.customdurability.mixins.inter.DamageItem;
import me.gravityio.customdurability.network.SyncPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?}

/**
 * The entrypoint to CustomDurability<br>
 * A mod that allows to change the durability of items
 */
public class CustomDurabilityMod implements ModInitializer {
    public static final String MOD_ID = "customdurability";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean IS_DEBUG = false;

    public static CustomDurabilityMod INSTANCE;
    public static MinecraftServer SERVER = null;

    public static void DEBUG(String message, Object... objects) {
        if (!IS_DEBUG) return;

        LOGGER.info(message, objects);
    }

    public static ResourceLocation id(String path) {
        return Versioned.parseId(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        IS_DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();
        ModConfig.INSTANCE.load();
        //? if >=1.20.5 {
        PayloadTypeRegistry.playS2C().register(SyncPayload.TYPE, SyncPayload.CODEC);
        //?}

        // When the server starts we update the registry with the durabilities
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            DEBUG("[CustomDurabilityMod] Server Started");
            CustomDurabilityMod.SERVER = server;
            this.updateRegistry();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            DEBUG("[CustomDurabilityMod] Server Stopped");
            CustomDurabilityMod.SERVER = null;
        });

        // When the durability is changed in our config, we update the items with the new durabilities and send the new registry to all players
        ModEvents.ON_DURABILITY_CHANGED.register(() -> {
            this.updateRegistry();
            LOGGER.info("[CustomDurabilityMod] Durability registry changed sending Sync Packet to all players!");
            CustomDurabilityMod.SERVER.getPlayerList().getPlayers().forEach(player ->
                    ServerPlayNetworking.send(player, new SyncPayload(DurabilityRegistry.getDurabilityOverrides())));
        });


        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            LOGGER.info("[CustomDurabilityMod] Syncing packets for player '{}', sending Sync Packet!", player.getName().getString());
            ServerPlayNetworking.send(player, new SyncPayload(DurabilityRegistry.getDurabilityOverrides()));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) ->
                dispatcher.register(ModCommands.build(registry, environment))
        );

    }

    public void updateRegistry() {
        LOGGER.info("[CustomDurabilityMod] Updating Durability Registry");
        List<String> removed = DurabilityRegistry.setFrom(ModConfig.INSTANCE.durability_overrides);
        this.updateItems(removed);
    }

    public void updateItems(List<String> removed) {
        DEBUG("[CustomDurabilityMod] Updating Items from the Minecraft Registry with new durabilities.");
        DurabilityRegistry.forEach(this::onDurabilityChanged);
        removed.forEach(this::onResetDurability);
    }

    // When our registry changes we update the items with the new durabilities
    public void onDurabilityChanged(String itemIdOrTag, int newDurability) {
        for (Item item : this.getItemsByIdOrTag(itemIdOrTag)) {
            if (!Versioned.isDamageable(item)) continue;

            if (newDurability <= 0) {
                this.setOriginalDamage(item);
            } else {
                this.setMaxDamage(item, newDurability);
            }
        }
    }

    public void onResetDurability(String itemIdOrTag) {
        for (Item item : this.getItemsByIdOrTag(itemIdOrTag)) {
            if (!Versioned.isDamageable(item)) continue;

            this.setOriginalDamage(item);
        }
    }

    public void setOriginalDamage(Item item) {
        var damageItem = (DamageItem) item;
        var originalDamage = damageItem.customDurability$getOriginalMaxDamage();
        DEBUG("[CustomDurabilityMod] Setting original durability of {} to {}", item, originalDamage);
        this.setMaxDamageRaw(item, originalDamage);
    }

    public void setMaxDamage(Item item, int newMaxDamage) {
        if (item instanceof ArmorItem armor && ModConfig.INSTANCE.armor_is_durability_multiplier) {
            newMaxDamage = Versioned.getArmorDurability(armor, newMaxDamage);
        }
        this.setMaxDamageRaw(item, newMaxDamage);
    }

    public void setMaxDamageRaw(Item item, int newMaxDamage) {
        DEBUG("[CustomDurabilityMod] Updating durability of {} to {}", item, newMaxDamage);
        Versioned.setDurability(item, newMaxDamage);
    }

    public boolean isTag(String id){
        return id.charAt(0) == '#';
    }

    public TagKey<Item> getTag(String tag) {
        if (!this.isTag(tag)) return null;
        var idString = tag.substring(1);
        var id = Versioned.parseId(idString);
        return TagKey.create(Registries.ITEM, id);
    }

    public List<Item> getItemsByRegex(String regex) {
        List<Item> items = new ArrayList<>(16);
        BuiltInRegistries.ITEM.holders().forEach(itemReference -> {
            var v = itemReference.key().toString();
            if (!Pattern.matches(regex, v)) return;
            items.add(itemReference.value());
        });
        return items;
    }

    public List<Item> getItemsByIdOrTag(String idOrTag) {
        List<Item> items = new ArrayList<>();
        TagKey<Item> tag = this.getTag(idOrTag);
        if (tag != null) {
            var optEntryList = BuiltInRegistries.ITEM.getTag(tag);
            if (optEntryList.isPresent()) {
                var entryList = optEntryList.get();
                for (var itemRegistryEntry : entryList) {
                    var item = itemRegistryEntry.value();
                    items.add(item);
                }
            } else {
                DEBUG("[CustomDurabilityMod] No items under tag {} exist!", tag);
            }
        } else {
            items.add(BuiltInRegistries.ITEM.get(Versioned.parseId(idOrTag)));
        }
        return items;
    }

    public static Iterable<Holder.Reference<Item>> iterateDamageables() {
        var it = BuiltInRegistries.ITEM.holders().iterator();
        return () -> new Iterator<>() {
            Holder.Reference<Item> next;
            @Override
            public boolean hasNext() {
                while (it.hasNext()) {
                    var temp = it.next();
                    if (!temp.value().components().has(DataComponents.MAX_DAMAGE)) continue;
                    this.next = temp;
                    return true;
                }
                return false;
            }

            @Override
            public Holder.Reference<Item> next() {
                return this.next;
            }
        };
    }

    public static void removeDurabilityOverride(String idOrTagStr) {
        ModConfig.INSTANCE.removeDurabilityOverrideRaw(idOrTagStr);
        ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
        ModConfig.INSTANCE.save();
    }

    public static void removeDurabilityOverride(String... idOrTagStrs) {
        for (String idOrTagStr : idOrTagStrs) {
            ModConfig.INSTANCE.removeDurabilityOverrideRaw(idOrTagStr);
        }
        ModEvents.ON_DURABILITY_CHANGED.invoker().onChanged();
        ModConfig.INSTANCE.save();
    }


}
