package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Mystica;

@FunctionalInterface
public interface AbilitySetFactory {

    AbilitySet create(
            Mystica main,
            AbilityManager manager,
            AbilityLookup lookup
    );

}
