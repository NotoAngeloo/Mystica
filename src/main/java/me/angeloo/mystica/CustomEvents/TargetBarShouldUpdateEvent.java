package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TargetBarShouldUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity target;


    public TargetBarShouldUpdateEvent(LivingEntity target){
        this.target = target;
    }

    public LivingEntity getTarget(){
        return target;
    }


    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }

}
