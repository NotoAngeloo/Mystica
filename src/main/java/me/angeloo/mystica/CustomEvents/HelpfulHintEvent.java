package me.angeloo.mystica.CustomEvents;

import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class HelpfulHintEvent extends Event{

    public static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final String whatHint;


    public HelpfulHintEvent(Player player, String whatHint){
        this.player = player;
        this.whatHint = whatHint;
    }


    public Player getPlayer(){return player;}
    public String getWhatHint(){return whatHint;}

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
