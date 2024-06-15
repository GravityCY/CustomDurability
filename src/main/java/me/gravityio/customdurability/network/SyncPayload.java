package me.gravityio.customdurability.network;

import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.DurabilityRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A Packet to synchronize the server and the client with their materials and durabilities
 */
public class SyncPayload implements CustomPayload {
    public static Id<SyncPayload> ID = new CustomPayload.Id<>(Identifier.of(CustomDurabilityMod.MOD_ID, "material_sync"));
    public static final PacketCodec<PacketByteBuf, SyncPayload> CODEC = PacketCodec.of(SyncPayload::write, SyncPayload::new);
    private final Map<String, Integer> map;

    public SyncPayload(LinkedHashMap<String, Integer> map) {
        this.map = map;
    }

    public SyncPayload(PacketByteBuf buf) {
        CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Creating Sync Packet on Client.");
        var map = buf.readMap(PacketByteBuf::readString, PacketByteBuf::readInt);
        this.map = new LinkedHashMap<>();
        this.map.putAll(map);
    }

    public void write(PacketByteBuf buf) {
        buf.writeMap(DurabilityRegistry.getDurabilityOverrides(), PacketByteBuf::writeString, PacketByteBuf::writeInt);
    }

    public List<String> apply() {
        return DurabilityRegistry.setFrom(this.map);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
