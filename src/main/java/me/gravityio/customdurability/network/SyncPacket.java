package me.gravityio.customdurability.network;

import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.DurabilityRegistry;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * A Packet to synchronize the server and the client with their materials and durabilities
 */
public class SyncPacket implements FabricPacket {
    public static PacketType<SyncPacket> TYPE = PacketType.create(new Identifier(CustomDurabilityMod.MOD_ID, "material_sync"), SyncPacket::new);
    private final LinkedHashMap<String, Integer> map;

    public SyncPacket(LinkedHashMap<String, Integer> map) {
        this.map = map;
    }

    public SyncPacket(PacketByteBuf buf) {
        CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Creating Sync Packet on Client.");
        var map = buf.readMap(PacketByteBuf::readString, PacketByteBuf::readInt);
        this.map = new LinkedHashMap<>();
        this.map.putAll(map);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeMap(DurabilityRegistry.getDurabilityOverrides(), PacketByteBuf::writeString, PacketByteBuf::writeInt);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public List<String> apply() {
        return DurabilityRegistry.setFrom(this.map);
    }
}
