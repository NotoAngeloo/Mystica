package me.angeloo.mystica.Components.Hud.DamageIndicator;

public class DamageSlot {

    private DamageEntry entry;

    private long expirationTime;

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
    }

    public void clear() {

        entry = null;
    }

    public DamageEntry getEntry() {
        return entry;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

}
