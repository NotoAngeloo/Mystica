package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class StatusInstance {

    protected final StatusEffect effect;
    protected double magnitude; // fixed or per-instance bonus
    protected int remainingTicks;     // -1 for consumable effects / indefinite duration

    public StatusInstance(StatusEffect effect, int duration, double magnitude) {
        this.effect = effect;
        this.remainingTicks = duration;
        this.magnitude = magnitude;
    }

    public StatusEffect getEffect() {
        return effect;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    /**
     * Tick down the effect.
     * @return true if the effect has expired and should be removed
     */
    public boolean tickDown() {
        // Only decrement for timed effects
        if (remainingTicks > 0) {
            remainingTicks--;
            return remainingTicks <= 0;
        }
        return false; // consumable/indefinite effects never expire automatically
    }

    // Hooks called by manager
    public void onApply(LivingEntity entity) {
        updateHud(entity);
        effect.onApply(entity, this);
    }

    public void onTick(LivingEntity entity) {
        updateHud(entity);
        effect.onTick(entity, this);
    }

    public void onRemove(LivingEntity entity) {
        updateHud(entity);
        effect.onRemove(entity, this);
    }

    /**
     * Called when the entity takes damage or triggers the effect
     */
    public void onDamage(LivingEntity entity, double amount) {
        effect.onDamage(entity, this, amount);
        return;
    }

    public void endNow(){
        this.remainingTicks = 0;
    }

    private void updateHud(LivingEntity entity) {
        if (entity instanceof Player player) {
            Bukkit.getServer().getPluginManager().callEvent(
                    new HudUpdateEvent(player, BarType.Status)
            );
        }
    }

}
