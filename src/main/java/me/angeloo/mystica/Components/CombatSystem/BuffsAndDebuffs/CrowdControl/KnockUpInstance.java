package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class KnockUpInstance extends StatusInstance {

    public KnockUpInstance(StatusEffect effect, int duration, double magnitude, LivingEntity source) {
        super(effect, duration, magnitude, source); // magnitude could control upward speed
    }

    @Override
    public void onApply(LivingEntity entity) {
        super.onApply(entity);

        Vector velocity = (new Vector(0, effect.getMagnitude(), 0));
        entity.setVelocity(velocity);
    }

    @Override
    public void onTick(LivingEntity entity, CombatContext combatContext) {
        super.onTick(entity, combatContext);
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
