package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class SleepInstance extends StatusInstance {

    public SleepInstance(StatusEffect effect, int duration) {
        super(effect, duration, 0); // magnitude can be 0 unless needed
    }

    @Override
    public void onApply(LivingEntity entity) {
        super.onApply(entity);
        // Prevent movement or abilities
        //ImmobilizeManager.setImmobilized(entity, true);
    }

    @Override
    public void onRemove(LivingEntity entity) {
        super.onRemove(entity);
        //ImmobilizeManager.setImmobilized(entity, false);
    }

    @Override
    public void onDamage(LivingEntity entity, double amount) {
        // Sleep dispelled on damage
        this.remainingTicks = 0; // mark for removal
    }

}
