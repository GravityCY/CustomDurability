package me.gravityio.customdurability;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModEvents {

    /**
     * An event of when the config durability changes from the config
     */
    public static final Event<DurabilityChangeEvent> ON_DURABILITY_CHANGED = EventFactory.createArrayBacked(DurabilityChangeEvent.class,
    (listeners) -> () -> {
        for (DurabilityChangeEvent listener : listeners) {
            listener.onChanged();
        }
    });

    /**
     * After the server sends the tags sync packet
     */
    public static final Event<OnAfterSyncDatapack> ON_AFTER_SYNC_DATAPACK = EventFactory.createArrayBacked(OnAfterSyncDatapack.class,
    (listeners) -> (ServerPlayerEntity player) -> {
        for (OnAfterSyncDatapack listener : listeners) {
            listener.onAfterSyncDatapack(player);
        }
    });

    public interface DurabilityChangeEvent {
        void onChanged();
    }

    public interface OnAfterSyncDatapack {
        void onAfterSyncDatapack(ServerPlayerEntity player);
    }
}
