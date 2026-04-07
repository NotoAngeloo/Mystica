package me.angeloo.mystica.CustomEvents;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RemoveStealthEffectEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;


    public RemoveStealthEffectEvent(LivingEntity entity){
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
