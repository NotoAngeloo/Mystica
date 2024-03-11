package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardValueUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Player player;


    public BoardValueUpdateEvent(Player player){
        this.player = player;
    }

    public Player getPlayer(){return player;}

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
