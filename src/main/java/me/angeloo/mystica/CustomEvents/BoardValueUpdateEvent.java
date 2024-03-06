package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardValueUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity damager;
    private final LivingEntity damaged;

    public BoardValueUpdateEvent(LivingEntity damager, LivingEntity damaged){
        this.damager = damager;
        this.damaged = damaged;
    }

    public LivingEntity getDamager(){
        return damager;
    }

    public LivingEntity getDamaged(){
        return damaged;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
