package me.angeloo.mystica.CustomEvents;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HudUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final String barType;
    private boolean forced;



    public HudUpdateEvent(Player player, String barType, boolean forced){
        this.player = player;
        this.barType = barType;
        this.forced = forced;
    }

    public Player getPlayer(){return player;}
    public String getBarType(){return barType;}
    public boolean getIfForced(){return forced;}

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
