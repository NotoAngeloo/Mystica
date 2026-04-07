package me.angeloo.mystica.CustomEvents;


import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HudUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;

    private final BarType barType;



    public HudUpdateEvent(LivingEntity entity, BarType barType){
        this.entity = entity;
        this.barType = barType;
    }

    public LivingEntity getEntity(){return entity;}
    public BarType getBarType(){return barType;}

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
