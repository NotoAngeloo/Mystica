package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class WindWallShield implements StatusEffect {

    @Override
    public String getId() {
        return "wind_wall";
    }


    //on application, it is ALWAYS 1/4th max hp
    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }
}
