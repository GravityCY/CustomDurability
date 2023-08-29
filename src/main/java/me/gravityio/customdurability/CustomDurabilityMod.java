package me.gravityio.customdurability;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import me.gravityio.customdurability.mixins.impl.BaseDurabilityAccessor;
import me.gravityio.customdurability.mixins.impl.MaxDamageAccessor;
import me.gravityio.customdurability.network.SyncPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
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

public class CustomDurabilityMod implements ModInitializer, PreLaunchEntrypoint {
    public static final String MOD_ID = "customdurability";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean DEBUG = false;

    public static CustomDurabilityMod INSTANCE;
    public static MinecraftServer SERVER = null;
    public static boolean ALLOW_REGISTRY_MOD = true;
    public static boolean IS_INTEGRATED = false;
    public static boolean IN_WORLD = false;

    public static void DEBUG(String message, Object... objects) {
        if (!DEBUG) return;

        LOGGER.info(message, objects);
    }


    @Override
    public void onPreLaunch() {
        INSTANCE = this;

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            DEBUG = true;
        }

        MixinExtrasBootstrap.init();

        ModConfig.GSON.load();
        ModConfig.INSTANCE = ModConfig.GSON.getConfig();
    }

    @Override
    public void onInitialize() {

        // When the server starts we update the registry with the durabilities
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            DEBUG("[CustomDurabilityMod] Server Started");
            CustomDurabilityMod.SERVER = server;
            CustomDurabilityMod.IN_WORLD = true;
            this.updateRegistry();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            DEBUG("[CustomDurabilityMod] Server Stopped");
            CustomDurabilityMod.SERVER = null;
            CustomDurabilityMod.IN_WORLD = false;
        });

        // When the durability is changed in our config, we update the items with the new durabilities and send the new registry to all players
        ModEvents.ON_DURABILITY_CHANGED.register(() -> {
            if (!CustomDurabilityMod.IN_WORLD || !CustomDurabilityMod.ALLOW_REGISTRY_MOD) return;
            this.updateRegistry();
            DEBUG("[CustomDurabilityMod] Durability Registry Changed Sending Sync Packet to All Players!");
            CustomDurabilityMod.SERVER.getPlayerManager().getPlayerList().forEach(player ->
                    ServerPlayNetworking.send(player, new SyncPacket()));
        });

        ModEvents.ON_AFTER_SYNC_DATAPACK.register((player) -> {
            DEBUG("{} joined the server! Sending Sync Packet", player.getName());
            ServerPlayNetworking.send(player, new SyncPacket());
        });

    }

    public void updateRegistry() {
        DurabilityRegistry.setFrom(ModConfig.INSTANCE.durability_overrides);
        this.updateItems();
    }

    public void updateItems() {
        DurabilityRegistry.forEach(this::onDurabilityChanged);
    }

    // When our registry changes we update the items with the new durabilities
    public void onDurabilityChanged(String changedItemStringId, int newDurability) {
        var isTag = changedItemStringId.charAt(0) == '#';
        TagKey<Item> tag = null;
        if (isTag) {
            var idString = changedItemStringId.substring(1);
            var id = new Identifier(idString);
            tag = TagKey.of(RegistryKeys.ITEM, id);
        }

        if (isTag) {
            var optEntryList = Registries.ITEM.getEntryList(tag);
            if (optEntryList.isEmpty()) {
                DEBUG("[CustomDurabilityMod] No items under tag {} exist!", tag);
                return;
            }
            var entryList = optEntryList.get();
            for (RegistryEntry<Item> itemRegistryEntry : entryList) {
                var item = itemRegistryEntry.value();
                this.setMaxDamage(item, newDurability);
            }
        } else {
            var id = new Identifier(changedItemStringId);
            var item = Registries.ITEM.get(id);
            this.setMaxDamage(item, newDurability);
        }
    }

    public void setMaxDamage(Item item, int newMaxDamage){
        var damageItem = (MaxDamageAccessor) item;
        if (damageItem instanceof ArmorItem armor && ModConfig.INSTANCE.armor_is_durability_multiplier)
            newMaxDamage = BaseDurabilityAccessor.BASE_DURABILITY().get(armor.getType()) * newMaxDamage;
        DEBUG("[CustomDurabilityMod] Updating durability of {} to {}", item, newMaxDamage);
        damageItem.setMaxDamage(newMaxDamage);
    }


}
