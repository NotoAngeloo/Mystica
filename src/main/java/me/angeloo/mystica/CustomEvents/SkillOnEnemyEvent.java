package me.angeloo.mystica.CustomEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillOnEnemyEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    private final LivingEntity entity;
    private final LivingEntity caster;

    public SkillOnEnemyEvent(LivingEntity entity, LivingEntity caster){
        this.entity = entity;
        this.caster = caster;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public LivingEntity getCaster(){return caster;}


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
