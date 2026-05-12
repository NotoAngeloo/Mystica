package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import org.bukkit.entity.LivingEntity;

public class StatusInstance {

    protected final StatusEffect effect;
    protected double magnitude; // fixed or per-instance bonus
    protected int startingDurationTicks; //this is used for the hud displayer
    protected int remainingTicks;     // -1 for consumable effects / indefinite duration
    protected int livedTicks = 0; //for damage over time effects to NOT damage every tick sometimes
    protected final LivingEntity source; //source matters for damaging effects
    protected int stacks = 1;

    private boolean markedForRemoval = false;

    public StatusInstance(StatusEffect effect, int duration, double magnitude, LivingEntity source) {
        this.effect = effect;
        this.startingDurationTicks = duration;
        this.remainingTicks = duration;
        this.magnitude = magnitude;
        this.source = source;
    }

    public StatusEffect getEffect() {
        return effect;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public int getStartingDurationTicks(){
        return startingDurationTicks;
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

    //stacking effects
    public void onApply(LivingEntity entity, CombatContext combatContext, StatusApplicationResult result) {
        effect.onApply(entity, this, combatContext, result);
    }

    //non-stacking effects
    public void onApply(LivingEntity entity) {
        effect.onApply(entity, this);
    }

    public void onTick(LivingEntity entity, CombatContext combatContext) {
        effect.onTick(entity, this, combatContext);
    }

    //for effects that deal damage on expire
    public void onRemoveEffects(LivingEntity entity, CombatContext combatContext) {
        effect.onRemoveEffects(entity, this, combatContext);
    }

    public void onRemove(LivingEntity entity){
        effect.onRemove(entity);
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

    //USEFUL FOR GETTING AMOUNT OF STACKING EFFECTS
    public double getInstanceMagnitude(){
        return this.magnitude;
    }

    public int getLivedTicks(){
        return livedTicks;
    }

    public LivingEntity getSource(){
        return source;
    }

    public int getStacks(){
        return stacks;
    }

    public void editStackCount(int amount){
        this.stacks += amount;
    }

    public void markForRemoval(){
        this.markedForRemoval = true;
    }

    public boolean isMarkedForRemoval(){
        return markedForRemoval;
    }

}
