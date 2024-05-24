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

    public interface DurabilityChangeEvent {
        void onChanged();
    }
}
