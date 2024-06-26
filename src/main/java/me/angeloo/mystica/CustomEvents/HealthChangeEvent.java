package me.angeloo.mystica.CustomEvents;

import org.bukkit.event.Event;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

public class HealthChangeEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;
    private final Boolean isPositive;

    public HealthChangeEvent(LivingEntity entity, Boolean isPositive){
        this.entity = entity;
        this.isPositive = isPositive;
    }


    public LivingEntity getEntity(){return entity;}

    public Boolean getIfPositive(){return isPositive;}


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
