package me.angeloo.mystica.Components.CombatSystem.Classes;

import me.angeloo.mystica.Components.ProfileComponents.StatProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum SubClass {

    DUELIST("Duelist",
            PlayerClass.ASSASSIN,
            Role.DAMAGE,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(1,0,0,0,10),
            List.of(
                    "A master of sustained single",
                    "target damage.",
                    "Grants the skill \"Duelist's Frenzy\".",
                    "Consumes all combo points to deal a",
                    "massive amount of damage."
                    )
    ),

    ALCHEMIST("Alchemist",
            PlayerClass.ASSASSIN,
            Role.DAMAGE,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(0,15,0,0,0),
            List.of(
                    "A master of poisons and potions",
                    "capable of reducing armor and",
                    "healing allies.",
                    "Grants the skill \"Wicked Concoction\".",
                    "Increase or decrease damage",
                    "taken of your target."
            )
    ),

    PYROMANCER("Pyromancer",
            PlayerClass.ELEMENTALIST,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(1,0,0,0,10),
            List.of(
                    "A master of burst damage.",
                    "Grants the skill \"Fiery Wing\".",
                    "Deals a large amount of",
                    "damage from a distance."
            )
    ),

    CONJURER("Conjurer",
            PlayerClass.ELEMENTALIST,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(0,15,0,0,0),
            List.of(
                    "A master of team utility.",
                    "Grants the skill \"Conjuring Force\".",
                    "Increases the damage and",
                    "range of all alies within",
                    "the circle by a flat amount."
            )
    ),

    SHEPARD("Shepard",
            PlayerClass.MYSTIC,
            Role.HEALER,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(0,15,0,0,0),
            List.of(
                    "A master of healing and shielding",
                    "allies.",
                    "Grants the skill \"Enlightenment\".",
                    "Restores the health of",
                    "all marked allies."
            )
    ),

    ARCANE("Arcane",
            PlayerClass.MYSTIC,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(1,0,0,0,10),
            List.of(
                    "A balance between supportive",
                    "skills and damage skills.",
                    "Grants the skill \"Arcane Missiles\".",
                    "Deals continuous damage to the",
                    "target."
            )
    ),

    CHAOS("Chaos",
            PlayerClass.MYSTIC,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(1,0,0,0,10),
            List.of(
                    "Coming soon"
            )
    ),

    TEMPLAR("Templar",
            PlayerClass.PALADIN,
            Role.TANK,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(-1,15,1,1,0),
            List.of(
                    "A master of self healing",
                    "and defensive skills.",
                    "Grants the skill \"Sanctity Shield\".",
                    "Grants a shield that heals as",
                    "long as it is active."
            )
    ),

    DAWN("Dawn",
            PlayerClass.PALADIN,
            Role.DAMAGE,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(1,0,0,0,10),
            List.of(
                    "A master of self healing",
                    "and damage skills.",
                    "Grants the skill \"Light Well\".",
                    "Which boosts the critical",
                    "chance of yourself and allies"
            )
    ),

    DIVINE("Divine",
            PlayerClass.PALADIN,
            Role.HEALER,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(0,15,0,0,0),
            List.of(
                    "Coming soon"
            )
    ),

    SCOUT("Scout",
            PlayerClass.RANGER,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.PHYSICAL,
            new StatProfile(1,0,0,0,10),
            List.of(
                    "A master of long range",
                    "tactical combat.",
                    "Grants the skill \"Star Volley\".",
                    "Its cooldown is reduced every",
                    "time you critically strike."
            )
    ),

    TAMER("Animal Tamer",
            PlayerClass.RANGER,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.PHYSICAL,
            new StatProfile(0,15,0,0,0),
            List.of(
                    "A master of team buffs.",
                    "Grants the skill \"Wild Roar\".",
                    "Multiply the damage of all",
                    "buffed allies for a time."
            )
    ),

    DOOM("Doom",
            PlayerClass.SHADOW_KNIGHT,
            Role.DAMAGE,
            Range.MELEE,
            SubclassDamageType.MAGICAL,
            new StatProfile(1,0,0,0,10),
            List.of(
                    "A master of sustained damage.",
                    "Grants the skill \"Annihilation\".",
                    "Increase the potency of your",
                    "damage over time ability after",
                    "dealing great damage."
            )
    ),

    BLOOD("Blood",
            PlayerClass.SHADOW_KNIGHT,
            Role.TANK,
            Range.MELEE,
            SubclassDamageType.MAGICAL,
            new StatProfile(-1,15,1,1,0),
            List.of(
                    "A master of survivability.",
                    "Grants the skill \"Blood Shield\".",
                    "Immediately heal half missing",
                    "health and grant a shield",
                    "equal to what was restored."
            )
    ),

    GLADIATOR("Gladiator",
            PlayerClass.WARRIOR,
            Role.TANK,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(-1,15,1,1,0),
            List.of(
                    "A master of damage reduction.",
                    "Grants the skill \"Gladiator Heart\".",
                    "Gain a shield, and while active",
                    "take reduced damage."
            )
    ),

    EXECUTIONER("Executioner",
            PlayerClass.WARRIOR,
            Role.DAMAGE,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(1,0,0,0,10),
            List.of(
                    "A master of crowd control",
                    "Grants the skill \"Death Gaze\".",
                    "Pulls the target to your",
                    "location, or you to theirs."
            )
    );


    private final String displayName;
    private final PlayerClass parentClass;
    private final Role role;
    private final Range range;
    private final SubclassDamageType damageType;
    private final StatProfile statProfile;
    private final List<String> description;

    SubClass(
            String displayName,
            PlayerClass parentClass,
            Role role,
            Range range,
            SubclassDamageType damageType,
            StatProfile statProfile,
            List<String> description
    ) {

        this.displayName = displayName;
        this.parentClass = parentClass;
        this.role = role;
        this.range = range;
        this.damageType = damageType;
        this.statProfile = statProfile;

        this.description = new ArrayList<>(description);
    }

    public String getDisplayName() {
        return displayName;
    }

    public PlayerClass getParentClass() {
        return parentClass;
    }

    public Role getRole(){
        return role;
    }

    public Range getRange(){
        return range;
    }

    public SubclassDamageType getDamageType(){return damageType;}

    public StatProfile getStatProfile() {
        return statProfile;
    }

    public List<String> getDescription(){return description;}
}
