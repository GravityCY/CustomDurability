package me.gravityio.customdurability;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import me.gravityio.customdurability.mixins.inter.DamageItem;
import me.gravityio.customdurability.network.SyncPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The entrypoint to CustomDurability<br>
 * A mod that allows to change the durability of items
 */
public class CustomDurabilityMod implements ModInitializer, PreLaunchEntrypoint {
    public static final String MOD_ID = "customdurability";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean IS_DEBUG = false;

    public static CustomDurabilityMod INSTANCE;
    public static MinecraftServer SERVER = null;

    public static void DEBUG(String message, Object... objects) {
        if (!IS_DEBUG) return;

        LOGGER.info(message, objects);
    }


    @Override
    public void onPreLaunch() {
        INSTANCE = this;
        IS_DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

        MixinExtrasBootstrap.init();
        ModConfig.INSTANCE.load();
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(SyncPayload.ID, SyncPayload.CODEC);

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
            CustomDurabilityMod.SERVER.getPlayerManager().getPlayerList().forEach(player ->
                    ServerPlayNetworking.send(player, new SyncPayload(DurabilityRegistry.getDurabilityOverrides())));
        });


        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            LOGGER.info("[CustomDurabilityMod] {} joined the server, sending Sync Packet!", player.getName().getString());
            ServerPlayNetworking.send(player, new SyncPayload(DurabilityRegistry.getDurabilityOverrides()));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) ->
                dispatcher.register(ModCommands.doBuild(registry, environment))
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
            if (!item.getComponents().contains(DataComponentTypes.MAX_DAMAGE)) continue;

            if (newDurability <= 0) {
                this.setOriginalDamage(item);
            } else {
                this.setMaxDamage(item, newDurability);
            }
        }
    }

    public void onResetDurability(String itemIdOrTag) {
        for (Item item : getItemsByIdOrTag(itemIdOrTag)) {
            if (!item.getComponents().contains(DataComponentTypes.MAX_DAMAGE)) continue;

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
        if (item instanceof ArmorItem armor && ModConfig.INSTANCE.armor_is_durability_multiplier)
            newMaxDamage = armor.getType().getMaxDamage(newMaxDamage);
        setMaxDamageRaw(item, newMaxDamage);
    }

    public void setMaxDamageRaw(Item item, int newMaxDamage) {
        DEBUG("[CustomDurabilityMod] Updating durability of {} to {}", item, newMaxDamage);
        var components = (ComponentMap.Builder.SimpleComponentMap) item.getComponents();
        DamageItem dItem = (DamageItem) item;
        if (dItem.customDurability$getOriginalMaxDamage() == null) {
            dItem.customDurability$setOriginalMaxDamage(item.getComponents().get(DataComponentTypes.MAX_DAMAGE));
        }
        components.map().put(DataComponentTypes.MAX_DAMAGE, newMaxDamage);
    }

    public boolean isTag(String id){
        return id.charAt(0) == '#';
    }

    public TagKey<Item> getTag(String tag) {
        if (!this.isTag(tag)) return null;
        var idString = tag.substring(1);
        var id = new Identifier(idString);
        return TagKey.of(RegistryKeys.ITEM, id);
    }

    public List<Item> getItemsByIdOrTag(String idOrTag) {
        List<Item> items = new ArrayList<>();
        TagKey<Item> tag = this.getTag(idOrTag);
        if (tag != null) {
            var optEntryList = Registries.ITEM.getEntryList(tag);
            if (optEntryList.isPresent()) {
                var entryList = optEntryList.get();
                for (RegistryEntry<Item> itemRegistryEntry : entryList) {
                    var item = itemRegistryEntry.value();
                    items.add(item);
                }
            } else {
                DEBUG("[CustomDurabilityMod] No items under tag {} exist!", tag);
            }
        } else {
            items.add(Registries.ITEM.get(new Identifier(idOrTag)));
        }
        return items;
    }


}
