package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatusUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Boolean clear;
    private final Player player;


    public StatusUpdateEvent(Player player, Boolean clear){
        this.player = player;
        this.clear = clear;
    }

    public Player getPlayer(){
        return player;
    }

    public Boolean getClear(){return clear;}


    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }
}
