package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class PartyUpdateWhenObservingEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final List<LivingEntity> mParty;

    public PartyUpdateWhenObservingEvent(List<LivingEntity> mParty){
        this.mParty = mParty;
    }

    public List<LivingEntity> getMParty(){
        return mParty;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }

}
