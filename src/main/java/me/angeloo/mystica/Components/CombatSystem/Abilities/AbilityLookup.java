package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;

public interface AbilityLookup {

    Ability get(PlayerClass clazz, SubClass subClass, int abilityNumber);
    Ability get(PlayerClass clazz, int abilityNumber);

}
