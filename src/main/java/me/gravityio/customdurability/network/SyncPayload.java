package me.gravityio.customdurability.network;

import me.gravityio.customdurability.CustomDurabilityMod;
import me.gravityio.customdurability.DurabilityRegistry;
import net.minecraft.network.FriendlyByteBuf;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//? if >=1.20.5 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
//?} else {
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
*///?}

/**
 * A Packet to synchronize the server and the client with their materials and durabilities
 */
//? if >=1.20.5 {
public class SyncPayload implements CustomPacketPayload {
    public static Type<SyncPayload> TYPE = new CustomPacketPayload.Type<>(CustomDurabilityMod.id("material_sync"));
    public static final StreamCodec<FriendlyByteBuf, SyncPayload> CODEC = StreamCodec.ofMember(SyncPayload::write, SyncPayload::new);
//?} else {
/*public class SyncPayload implements FabricPacket {
    public static PacketType<SyncPayload> TYPE = PacketType.create(CustomDurabilityMod.id("material_sync"), SyncPayload::new);
*///?}
    private final Map<String, Integer> map;

    public SyncPayload(LinkedHashMap<String, Integer> map) {
        this.map = map;
    }

    public SyncPayload(FriendlyByteBuf buf) {
        CustomDurabilityMod.DEBUG("[CustomDurabilityClientMod] Creating Sync Packet on Client.");
        var map = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
        this.map = new LinkedHashMap<>();
        this.map.putAll(map);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeMap(DurabilityRegistry.getDurabilityOverrides(), FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
    }

    public List<String> apply() {
        return DurabilityRegistry.setFrom(this.map);
    }

    //? if >=1.20.5 {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*@Override
    public PacketType<?> getType() {
        return TYPE;
    }
    *///?}
}
