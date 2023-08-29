package me.gravityio.customdurability.network;

import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.DurabilityRegistry;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * A Packet to synchronize the server and the client with their materials and durabilities
 */
public class SyncPacket implements FabricPacket {
    public static PacketType<SyncPacket> TYPE = PacketType.create(new Identifier(CustomDurabilityMod.MOD_ID, "material_sync"), SyncPacket::new);

    public PacketByteBuf buf;
    public SyncPacket(PacketByteBuf buf) {
        this.buf = buf;
        this.buf.retain();
    }

    public SyncPacket() {
    }

    @Override
    public void write(PacketByteBuf buf) {
        DurabilityRegistry.toPacket(buf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
