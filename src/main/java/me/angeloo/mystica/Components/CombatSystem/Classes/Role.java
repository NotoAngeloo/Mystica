package me.angeloo.mystica.Components.CombatSystem.Classes;

public enum Role {

    TANK("Tank"),

    HEALER("Healer"),

    DAMAGE("Damage");

    private final String displayName;

    Role(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }

}
