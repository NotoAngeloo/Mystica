package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AiSignalEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity companion;

    private final String signal;

    public AiSignalEvent(LivingEntity companion, String signal){
        this.companion = companion;
        this.signal = signal;
    }

    public LivingEntity getCompanion(){
        return companion;
    }

    public String getSignal(){
        return signal;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
