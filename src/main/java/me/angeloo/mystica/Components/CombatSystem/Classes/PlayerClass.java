package me.angeloo.mystica.Components.CombatSystem.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityLookup;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySet;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySetFactory;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.*;
import me.angeloo.mystica.Components.ProfileComponents.StatProfile;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum PlayerClass {

    NONE("Unspecialized",
            NoneAbilities::new,
            List.of(),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(148, 140, 127),
            Material.WHITE_DYE,
            Collections.EMPTY_LIST
    ),


    ASSASSIN("Assassin",
            AssassinAbilities::new,
            List.of(SubClass.DUELIST, SubClass.ALCHEMIST),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(214, 61, 207),
            Material.PINK_DYE,

            List.of(
                    "High risk fighters who",
                    "rely on speed to deal",
                    "large amounts of damage")
    ),


    ELEMENTALIST("Elementalist",
            ElementalistAbilities::new,
            List.of(SubClass.PYROMANCER, SubClass.CONJURER),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(52, 151, 219),
            Material.CYAN_DYE,

            List.of(
                    "Powerful spellcasters who",
                    "burst their enemies from a",
                    "distance")
    ),


    MYSTIC("Mystic",
            MysticAbilities::new,
            List.of(SubClass.SHEPARD, SubClass.ARCANE, SubClass.CHAOS),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(155, 120, 197),
            Material.PURPLE_DYE,

            List.of(
                    "Spellcasters who balance",
                    "support skills with damage"
            )
    ),


    PALADIN("Paladin",
            PaladinAbilities::new,
            List.of(SubClass.TEMPLAR, SubClass.DAWN, SubClass.DIVINE),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(154, 125, 10),
            Material.YELLOW_DYE,

            List.of(
                    "Use their holy power",
                    "to take and deal damage")
    ),


    RANGER("Ranger",
            RangerAbilities::new,
            List.of(SubClass.SCOUT, SubClass.TAMER),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(34, 111, 80),
            Material.LIME_DYE,

            List.of(
                    "Strike enemies from afar",
                    "with arrows and animal",
                    "companions"
            )
    ),


    SHADOW_KNIGHT("Shadow Knight",
            ShadowKnightAbilities::new,
            List.of(SubClass.DOOM, SubClass.BLOOD),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(213, 33, 3),
            Material.RED_DYE,

            List.of(
                    "Use the power of spirits to",
                    "attack and defend for you"
            )
    ),


    WARRIOR("Warrior",
            WarriorAbilities::new,
            List.of(SubClass.GLADIATOR, SubClass.EXECUTIONER),
            new StatProfile(2, 15, 1, 1, 0),
            new Color(214, 126, 61),
            Material.ORANGE_DYE,

            List.of(
                    "Fearless frontline fighters",
                    "who overwhelm enemies with",
                    "strength and endurance"
            )
    );


    private final String displayName;
    private final AbilitySetFactory abilityFactory;
    private final List<SubClass> subclasses;
    private final StatProfile statProfile;
    private final Color color;
    private final Material weaponMaterial;
    private final List<String> description;

    PlayerClass(String displayName,
                AbilitySetFactory abilityFactory,
                List<SubClass> subclasses,
                StatProfile statProfile,
                Color color,
                Material weaponMaterial,
                List<String> description){
        this.displayName = displayName;
        this.abilityFactory = abilityFactory;
        this.subclasses = subclasses;
        this.statProfile = statProfile;
        this.color = color;
        this.weaponMaterial = weaponMaterial;

        this.description = new ArrayList<>(description);
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

    public Material getWeaponMaterial(){
        return weaponMaterial;
    }

    public Color getColor(){
        return color;
    }

    public List<String> getDescription(){return description;}

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
