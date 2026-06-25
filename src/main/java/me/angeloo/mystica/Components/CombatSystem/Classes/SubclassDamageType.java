package me.angeloo.mystica.Components.CombatSystem.Classes;

public enum SubclassDamageType {

    PHYSICAL("Physical"),

    MAGICAL("Magical"),

    HYBRID("Hybrid");

    private final String displayName;

    SubclassDamageType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }

}
