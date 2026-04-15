package me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns;

public class CooldownData {

    private double progress;
    private long lastUpdateTime;
    private final long baseCooldown; //ms

    public CooldownData(long baseCooldown){
        this.baseCooldown = baseCooldown;
        this.progress = 0.0;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public long getBaseCooldown(){
        return baseCooldown;
    }

    public double getProgress(){
        return progress;
    }

    public void setProgress(double progress){
        this.progress = progress;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long time) {
        this.lastUpdateTime = time;
    }
}
