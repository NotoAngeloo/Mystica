package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Components.CombatSystem.Abilities.Ability;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;

public interface AbilitySet {

    Ability get(int abilityNumber);

    BasicAttackDefinition getBasic();


}
