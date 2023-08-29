package me.gravityio.customdurability.mixins.impl;

import me.gravityio.customdurability.ModEvents;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class SyncDatapackMixin {
    @Inject(method = "onPlayerConnect", at = @At(value="INVOKE", target="net/minecraft/server/PlayerManager.sendCommandTree (Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void onPlayerConnectAndSendSync(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ModEvents.ON_AFTER_SYNC_DATAPACK.invoker().onAfterSyncDatapack(player);
    }
}
