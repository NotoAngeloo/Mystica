package me.angeloo.mystica.Components.Hud.DamageIndicator;

public class DamageSlot {

    private DamageEntry entry;

    private long expirationTime;


    private DamageEntry.AnimationStage lastStage;

    public boolean isActive(long now) {

        return entry != null &&
                expirationTime > now;
    }

    public void set(
            DamageEntry entry,
            long expirationTime
    ) {

        this.entry = entry;
        this.expirationTime = expirationTime;
        this.lastStage = null;
    }

    public void clear() {
        entry = null;
        lastStage = null;
    }

    public DamageEntry getEntry() {
        return entry;
    }

    public DamageEntry.AnimationStage getStage(long now){

        if(entry == null) {
            return null;
        }

        return entry.getStage(now);
    }

    public DamageEntry.AnimationStage getLastStage() {
        return lastStage;
    }

    public void setLastStage(
            DamageEntry.AnimationStage lastStage
    ) {

        this.lastStage = lastStage;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

}
