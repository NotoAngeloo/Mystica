package me.angeloo.mystica.Components.Hud.Abilties;

public class AbilityRenderState {

    private final long remainingMillis;
    private final long baseCooldownMillis;

    public AbilityRenderState(long remainingMillis,
                              long baseCooldownMillis){
        this.remainingMillis = remainingMillis;
        this.baseCooldownMillis = baseCooldownMillis;
    }

    public long getRemainingMillis() {
        return Math.max(0, remainingMillis);
    }

    public long getBaseCooldownMillis(){
        return baseCooldownMillis;
    }

    public double getRemainingSeconds() {
        return getRemainingMillis() / 1000.0;
    }

    public double getCooldownPercent() {
        if (baseCooldownMillis <= 0) return 0;

        double pct = remainingMillis / (double) baseCooldownMillis;
        return Math.min(1.0, Math.max(0.0, pct));
    }

    public int getDisplaySeconds() {
        return Math.max(0, (int) Math.floor(getRemainingSeconds() + 0.999));
    }

    public boolean isOnCooldown() {
        return getRemainingMillis() > 0;
    }

    public boolean shouldShowCooldown() {
        return getRemainingMillis() > 50;
    }
}
