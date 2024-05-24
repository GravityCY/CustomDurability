package me.gravityio.customdurability.client;

import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.network.SyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class CustomDurabilityClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworking.registerGlobalReceiver(SyncPacket.TYPE, (packet, player, responseSender) -> {
            if (client.isIntegratedServerRunning()) return;
            CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Received Sync Packet from Server.");
            var removed = packet.apply();
            CustomDurabilityMod.INSTANCE.updateItems(removed);
        });
    }
}
