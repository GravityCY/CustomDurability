package me.gravityio.customdurability.client;

import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.network.SyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class CustomDurabilityClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworking.registerGlobalReceiver(SyncPayload.ID, (payload, context) -> {
            if (client.isIntegratedServerRunning()) return;
            CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Received Sync Packet from Server.");
            var removed = payload.apply();
            CustomDurabilityMod.INSTANCE.updateItems(removed);
        });
    }
}
