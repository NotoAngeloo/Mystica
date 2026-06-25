package me.angeloo.mystica.Components.CombatSystem.Classes;

public enum Range {

    RANGED("Ranged"),
    MELEE("Melee");

    private final String displayName;

    Range(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
