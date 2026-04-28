package me.angeloo.mystica.Components.Hud.StatusEffects;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;

public class StatusEffectRenderState {

    private final StatusEffect effect;
    private final int remainingTicks;
    private final int maxTicks;

    public StatusEffectRenderState(StatusInstance instance) {
        this.effect = instance.getEffect();
        this.remainingTicks = instance.getRemainingTicks();
        this.maxTicks = effect.getDuration(); // authoritative
    }

    public boolean hasDuration() {
        return maxTicks > 0;
    }

    public double getRemainingSeconds() {
        if (!hasDuration()) return -1;
        return remainingTicks / 20.0;
    }

    public int getDisplaySeconds() {
        if (!hasDuration()) return -1;
        return Math.max(0, (int) Math.ceil(getRemainingSeconds()));
    }

    public double getPercent() {
        if (!hasDuration()) return 0;

        double pct = remainingTicks / (double) maxTicks;
        return Math.min(1.0, Math.max(0.0, pct));
    }

    public boolean shouldShowRadial() {
        return hasDuration();
    }



}
