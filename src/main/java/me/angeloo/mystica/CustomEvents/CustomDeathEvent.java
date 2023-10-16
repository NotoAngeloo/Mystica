package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomDeathEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final Player playerWhoKilled;

    private final LivingEntity entityWhoDied;

    public CustomDeathEvent(Player playerWhoKilled, LivingEntity entityWhoDied){
        this.playerWhoKilled = playerWhoKilled;
        this.entityWhoDied = entityWhoDied;
    }

    public Player getPlayerWhoKilled(){
        return playerWhoKilled;
    }

    public LivingEntity getEntityWhoDied() {
        return entityWhoDied;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }

}
