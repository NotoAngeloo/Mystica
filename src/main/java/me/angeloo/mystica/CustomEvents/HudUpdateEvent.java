package me.angeloo.mystica.CustomEvents;


import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HudUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final BarType barType;
    private boolean forced;



    public HudUpdateEvent(Player player, BarType barType, boolean forced){
        this.player = player;
        this.barType = barType;
        this.forced = forced;
    }

    public Player getPlayer(){return player;}
    public BarType getBarType(){return barType;}
    public boolean getIfForced(){return forced;}

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
