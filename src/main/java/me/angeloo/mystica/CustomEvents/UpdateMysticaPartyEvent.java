package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateMysticaPartyEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;


    public UpdateMysticaPartyEvent(LivingEntity entity){
        this.entity = entity;
    }

    public LivingEntity getEntity(){
        return this.entity;
    }


    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }

}
