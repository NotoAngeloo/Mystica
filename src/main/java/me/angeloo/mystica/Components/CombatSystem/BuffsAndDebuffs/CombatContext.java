package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;

public class CombatContext {

    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    public CombatContext(DamageCalculator damageCalculator, ChangeResourceHandler changeResourceHandler, CooldownManager cooldownManager){
        this.damageCalculator = damageCalculator;
        this.changeResourceHandler = changeResourceHandler;
        this.cooldownManager = cooldownManager;
    }

    public DamageCalculator getDamageCalculator(){
        return damageCalculator;
    }

    public ChangeResourceHandler getChangeResourceHandler(){
        return changeResourceHandler;
    }

    public CooldownManager getCooldownManager(){return cooldownManager;}

}
