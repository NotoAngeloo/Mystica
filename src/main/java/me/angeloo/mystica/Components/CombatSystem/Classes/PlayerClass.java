package me.angeloo.mystica.Components.CombatSystem.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityLookup;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySet;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySetFactory;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.*;
import me.angeloo.mystica.Components.ProfileComponents.StatProfile;
import me.angeloo.mystica.Mystica;

import java.awt.*;
import java.util.List;

public enum PlayerClass {
    NONE("Unspecialized",
            NoneAbilities::new,
            List.of(),
            new StatProfile(2, 15, 1, 1, 0),
            Color.WHITE
    ),


    ASSASSIN("Assassin",
            AssassinAbilities::new,
            List.of(SubClass.DUELIST, SubClass.ALCHEMIST),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(214, 61, 207)
    ),


    ELEMENTALIST("Elementalist",
            ElementalistAbilities::new,
            List.of(SubClass.PYROMANCER, SubClass.CONJURER),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(52, 151, 219)
    ),


    MYSTIC("Mystic",
            MysticAbilities::new,
            List.of(SubClass.SHEPARD, SubClass.ARCANE, SubClass.CHAOS),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(155, 120, 197)
    ),


    PALADIN("Paladin",
            PaladinAbilities::new,
            List.of(SubClass.TEMPLAR, SubClass.DAWN, SubClass.DIVINE),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(154, 125, 10)
    ),


    RANGER("Ranger",
            RangerAbilities::new,
            List.of(SubClass.SCOUT, SubClass.TAMER),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(34, 111, 80)
    ),


    SHADOW_KNIGHT("Shadow Knight",
            ShadowKnightAbilities::new,
            List.of(SubClass.DOOM, SubClass.BLOOD),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(213, 33, 3)
    ),


    WARRIOR("Warrior",
            WarriorAbilities::new,
            List.of(SubClass.GLADIATOR, SubClass.EXECUTIONER),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(214, 126, 61)
    );


    private final String displayName;
    private final AbilitySetFactory abilityFactory;
    private final List<SubClass> subclasses;
    private final StatProfile statProfile;
    private final Color color;

    PlayerClass(String displayName,
                AbilitySetFactory abilityFactory,
                List<SubClass> subclasses,
                StatProfile statProfile,
                Color color){
        this.displayName = displayName;
        this.abilityFactory = abilityFactory;
        this.subclasses = subclasses;
        this.statProfile = statProfile;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<SubClass> getSubclasses() {
        return subclasses;
    }

    public StatProfile getStatProfile() {
        return statProfile;
    }

    public Color getColor(){
        return color;
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
