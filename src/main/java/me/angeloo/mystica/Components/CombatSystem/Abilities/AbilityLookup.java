package me.angeloo.mystica.Components.CombatSystem.Abilities;


import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;

public interface AbilityLookup {

    Ability get(PlayerClass clazz, SubClass subClass, int abilityNumber);

    //this is for when subclass does not matter. not an ultimate, nor an override
    Ability get(PlayerClass clazz, int abilityNumber);
}
