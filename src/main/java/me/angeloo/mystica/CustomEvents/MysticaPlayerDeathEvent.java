package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MysticaPlayerDeathEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity mysticaPlayer;

    public MysticaPlayerDeathEvent(LivingEntity mysticaPlayer){
        this.mysticaPlayer = mysticaPlayer;
    }

    public LivingEntity getMysticaPlayer(){
        return mysticaPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }
}
