package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CompanionSpawnEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity companion;

    public CompanionSpawnEvent(LivingEntity companion){
        this.companion = companion;
    }

    public LivingEntity getCompanion(){
        return companion;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
