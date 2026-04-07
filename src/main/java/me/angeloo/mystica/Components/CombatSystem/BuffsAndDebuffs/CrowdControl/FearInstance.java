package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

import java.util.Random;
import org.bukkit.util.Vector;

public class FearInstance extends StatusInstance {

    private Vector direction;

    public FearInstance(StatusEffect effect, int duration, double magnitude){
        super(effect, duration, magnitude);
    }

    @Override
    public void onApply(LivingEntity entity){
        super.onApply(entity);
        Vector d  = entity.getLocation().getDirection().normalize().multiply(0.2);
        int angle = (int) (Math.random() * 360);
        double radians = Math.toRadians(angle);
        d.rotateAroundY(radians);
        d.setY(0);
        direction = d;
    }

    @Override
    public void onTick(LivingEntity entity){
        super.onTick(entity);
        entity.setVelocity(direction);
    }

}
