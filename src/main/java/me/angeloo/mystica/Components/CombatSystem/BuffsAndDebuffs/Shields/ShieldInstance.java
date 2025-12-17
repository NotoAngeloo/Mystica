package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class ShieldInstance extends StatusInstance {

    private double amount;
    private double excess = 0;

    public ShieldInstance(StatusEffect effect, int duration, double amount){
        super(effect, duration, amount);
        this.amount = amount;
    }

    public double getRemaining(){
        return amount;
    }

    public double getExcess(){
        return excess;
    }

    @Override
    public void onApply(LivingEntity entity) {
        super.onApply(entity);
    }

    @Override
    public void onDamage(LivingEntity entity, double incomingDamage) {

        double newAmount = amount - incomingDamage;

        if (newAmount > 0) {
            amount = newAmount; // still some shield left
            return;
        }

        // Shield fully consumed; pass excess damage through
        excess = -newAmount;

        amount = 0;
        remainingTicks = 0; // mark for removal


        return;
    }

}
