package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

public class StatusApplicationResult {

    public final boolean addedStack;
    public final boolean reachedMaxStacks;
    public final boolean wasAlreadyAtMax;

    public StatusApplicationResult(boolean addedStack, boolean reachedMaxStacks, boolean wasAlreadyAtMax) {
        this.addedStack = addedStack;
        this.reachedMaxStacks = reachedMaxStacks;
        this.wasAlreadyAtMax = wasAlreadyAtMax;
    }

}
