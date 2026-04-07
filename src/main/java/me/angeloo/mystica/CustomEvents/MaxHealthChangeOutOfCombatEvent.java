package me.angeloo.mystica.CustomEvents;

import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MaxHealthChangeOutOfCombatEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Player player;




    public MaxHealthChangeOutOfCombatEvent(Player player){
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
