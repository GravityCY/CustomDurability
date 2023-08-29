package me.gravityio.customdurability.client;

import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.DurabilityRegistry;
import me.gravityio.customdurability.network.SyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CustomDurabilityClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Joining a world!");
            CustomDurabilityMod.IS_INTEGRATED = client.isIntegratedServerRunning();
            CustomDurabilityMod.ALLOW_REGISTRY_MOD = CustomDurabilityMod.IS_INTEGRATED;
            CustomDurabilityMod.IN_WORLD = true;
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Disconnecting from a world!");
            CustomDurabilityMod.IS_INTEGRATED = false;
            CustomDurabilityMod.ALLOW_REGISTRY_MOD = true;
            CustomDurabilityMod.IN_WORLD = false;
        });

        ClientPlayNetworking.registerGlobalReceiver(SyncPacket.TYPE, (packet, player, responseSender) -> {
            if (CustomDurabilityMod.IS_INTEGRATED) return;
            CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Applying Sync Packet!");
            DurabilityRegistry.fromPacket(packet.buf);
            CustomDurabilityMod.INSTANCE.updateItems();
        });
    }
}
