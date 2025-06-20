package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MysticaEnemyDeathEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity playerWhoKilled;

    private final LivingEntity entityWhoDied;

    public MysticaEnemyDeathEvent(LivingEntity playerWhoKilled, LivingEntity entityWhoDied){
        this.playerWhoKilled = playerWhoKilled;
        this.entityWhoDied = entityWhoDied;
    }

    public LivingEntity getPlayerWhoKilled(){
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
