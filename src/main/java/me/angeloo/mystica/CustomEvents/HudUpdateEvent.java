package me.angeloo.mystica.CustomEvents;

import me.angeloo.mystica.Managers.HudManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HudUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final String barType;



    public HudUpdateEvent(Player player, String barType){
        this.player = player;
        this.barType = barType;
    }

    public Player getPlayer(){return player;}
    public String getBarType(){return barType;}
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
