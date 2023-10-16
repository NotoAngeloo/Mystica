package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillOnEnemyEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;
    private final Player player;

    public SkillOnEnemyEvent(LivingEntity entity, Player player){
        this.entity = entity;
        this.player = player;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Player getPlayer(){return player;}


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
