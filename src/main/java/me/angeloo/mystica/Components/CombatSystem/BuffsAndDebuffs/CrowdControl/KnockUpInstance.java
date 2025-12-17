package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class KnockUpInstance extends StatusInstance {

    public KnockUpInstance(StatusEffect effect, int duration, double magnitude) {
        super(effect, duration, magnitude); // magnitude could control upward speed
    }

    @Override
    public void onApply(LivingEntity entity) {
        super.onApply(entity);
        // Prevent movement while airborne
        // Launch upwards
        //maybe do it here in future instead of skills
        //Vector up = new Vector(0, magnitude, 0);
        //entity.setVelocity(up);
    }

    @Override
    public void onTick(LivingEntity entity) {
        super.onTick(entity);
        // Check if on ground
        if (entity.isOnGround()) {
            remainingTicks = 0; // mark for removal
        }
    }

    @Override
    public void onRemove(LivingEntity entity) {
        super.onRemove(entity);
    }

}
