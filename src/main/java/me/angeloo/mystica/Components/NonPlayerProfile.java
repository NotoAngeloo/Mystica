package me.angeloo.mystica.Components;

import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Components.ProfileComponents.Stats;

public abstract class NonPlayerProfile implements Profile{

    private double currentHealth;
    private Stats stats;
    private Boolean isMovable;
    private Boolean immortality;
    private final Boolean passive;
    private final Boolean object;
    private final Yield yield;

    public NonPlayerProfile(double currentHealth, Stats stats, Boolean isMovable, Boolean immortality,Boolean passive, Boolean object, Yield yield) {
        this.currentHealth = currentHealth;
        this.stats = stats;
        this.isMovable = isMovable;
        this.immortality = immortality;
        this.passive = passive;
        this.object = object;
        this.yield = yield;
    }

    @Override
    public double getCurrentHealth(){return currentHealth;}
    @Override
    public void setCurrentHealth(double currentHealth) {
        this.currentHealth = currentHealth;
    }
    @Override
    public Stats getStats() {
        return stats;
    }
    @Override
    public void setStats(Stats stats) {
        this.stats = stats;
    }
    @Override
    public Boolean getIsMovable(){
        return isMovable;
    }
    @Override
    public void setIsMovable(Boolean which){
        this.isMovable = which;
    }
    @Override
    public Boolean getImmortality(){return immortality;}
    @Override
    public Boolean getIfObject(){return object;}
    @Override
    public void setImmortality(boolean immortality){this.immortality = immortality;}
    @Override
    public Boolean getIsPassive(){return passive;}
    @Override
    public Yield getYield(){return yield;}
}
