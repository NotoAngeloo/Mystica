package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;

public class CombatContext {

    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    public CombatContext(DamageCalculator damageCalculator, ChangeResourceHandler changeResourceHandler){
        this.damageCalculator = damageCalculator;
        this.changeResourceHandler = changeResourceHandler;
    }

    public DamageCalculator getDamageCalculator(){
        return damageCalculator;
    }

    public ChangeResourceHandler getChangeResourceHandler(){
        return changeResourceHandler;
    }

}
