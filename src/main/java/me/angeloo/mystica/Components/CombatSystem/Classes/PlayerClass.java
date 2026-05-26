package me.angeloo.mystica.Components.CombatSystem.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityLookup;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySet;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySetFactory;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.*;
import me.angeloo.mystica.Mystica;

public enum PlayerClass {
    NONE("Unspecialized",
            NoneAbilities::new),
    ASSASSIN("Assassin",
            AssassinAbilities::new),
    ELEMENTALIST("Elementalist",
            ElementalistAbilities::new),
    MYSTIC("Mystic",
            MysticAbilities::new),
    PALADIN("Paladin",
            PaladinAbilities::new),
    RANGER("Ranger",
            RangerAbilities::new),
    SHADOW_KNIGHT("Shadow Knight",
            ShadowKnightAbilities::new),
    WARRIOR("Warrior",
            WarriorAbilities::new);


    private final String displayName;

    private final AbilitySetFactory abilityFactory;

    PlayerClass(String displayName,
                AbilitySetFactory abilityFactory){
        this.displayName = displayName;
        this.abilityFactory = abilityFactory;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AbilitySet createAbilities(
            Mystica main,
            AbilityManager manager,
            AbilityLookup lookup
    ) {

        return abilityFactory.create(
                main,
                manager,
                lookup
        );
    }

}
