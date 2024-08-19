package me.gravityio.customdurability.client;

import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.network.SyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class CustomDurabilityClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Minecraft client = Minecraft.getInstance();
        //? if >=1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(SyncPayload.TYPE, (payload, context) -> {
            if (client.isLocalServer()) return;
            CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Received Sync Packet from Server.");
            var removed = payload.apply();
            CustomDurabilityMod.INSTANCE.updateItems(removed);
        });
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(SyncPayload.TYPE, (packet, player, responseSender) -> {
            if (client.isLocalServer()) return;
            CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Received Sync Packet from Server.");
            var removed = packet.apply();
            CustomDurabilityMod.INSTANCE.updateItems(removed);
        });
        *///?}
    }
}
