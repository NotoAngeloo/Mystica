package me.angeloo.mystica.Components.Hud.DamageIndicator;

import me.angeloo.mystica.Utility.Enums.DamageType;

public class DamageEntry {

    private final int amount;

    private final boolean healing;

    private final boolean crit;

    private final long timestamp;

    public DamageEntry(
            int amount,
            boolean healing,
            boolean crit,
            long timestamp
    ) {

        this.amount = amount;
        this.healing = healing;
        this.crit = crit;
        this.timestamp = timestamp;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isHealing(){
        return healing;
    }

    public boolean isCrit() {
        return crit;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
