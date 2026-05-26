package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Components.CombatSystem.Abilities.Ability;


public abstract class BaseAbility implements Ability {

    private final String id;
    protected AbilityLookup lookup;

    protected BaseAbility(String id){
        this.id = id;
    }

    @Override
    public final String getId(){
        return id;
    }

    public void setLookup(AbilityLookup lookup){
        this.lookup = lookup;
    }
}
