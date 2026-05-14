package me.angeloo.mystica.Components.Hud.DamageIndicator;

import me.angeloo.mystica.Utility.Enums.DamageType;

public class DamageEntry {

    enum AnimationStage{
        POP,
        SETTLE,
        FADE
    }

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

    public long age(long now){
        return now - timestamp;
    }

    public AnimationStage getStage(long now){

        long age = now - timestamp;

        if(age<80) return AnimationStage.POP;

        if(age<650) return AnimationStage.SETTLE;

        return AnimationStage.FADE;
    }

}
