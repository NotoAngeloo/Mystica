package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Glory_Of_Paladins implements StatusEffect {

    @Override
    public String getId() {
        return "glory_of_paladins";
    }

    @Override
    public int getDuration(){
        return 15 * 20;
    }

    @Override
    public int getPriority(){
        return 1;
    }
}
