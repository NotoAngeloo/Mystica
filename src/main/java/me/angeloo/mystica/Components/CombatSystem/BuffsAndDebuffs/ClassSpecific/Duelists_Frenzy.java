package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;

public class Duelists_Frenzy implements StatusEffect {

    @Override
    public String getId() {
        return "duelists_frenzy";
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
