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
                    "large amount of damage."
                    )
    ),

    ALCHEMIST("Alchemist",
            PlayerClass.ASSASSIN,
            Role.DAMAGE,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    PYROMANCER("Pyromancer",
            PlayerClass.ELEMENTALIST,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    CONJURER("Conjurer",
            PlayerClass.ELEMENTALIST,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    SHEPARD("Shepard",
            PlayerClass.MYSTIC,
            Role.HEALER,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    ARCANE("Arcane",
            PlayerClass.MYSTIC,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    CHAOS("Chaos",
            PlayerClass.MYSTIC,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.MAGICAL,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    TEMPLAR("Templar",
            PlayerClass.PALADIN,
            Role.TANK,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(-1,15,1,1,0),
            Collections.EMPTY_LIST
    ),

    DAWN("Dawn",
            PlayerClass.PALADIN,
            Role.DAMAGE,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    DIVINE("Divine",
            PlayerClass.PALADIN,
            Role.HEALER,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    SCOUT("Scout",
            PlayerClass.RANGER,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.PHYSICAL,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    TAMER("Animal Tamer",
            PlayerClass.RANGER,
            Role.DAMAGE,
            Range.RANGED,
            SubclassDamageType.PHYSICAL,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    DOOM("Doom",
            PlayerClass.SHADOW_KNIGHT,
            Role.DAMAGE,
            Range.MELEE,
            SubclassDamageType.MAGICAL,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    BLOOD("Blood",
            PlayerClass.SHADOW_KNIGHT,
            Role.TANK,
            Range.MELEE,
            SubclassDamageType.MAGICAL,
            new StatProfile(-1,15,1,1,0),
            Collections.EMPTY_LIST
    ),

    GLADIATOR("Gladiator",
            PlayerClass.WARRIOR,
            Role.TANK,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(-1,15,1,1,0),
            Collections.EMPTY_LIST
    ),

    EXECUTIONER("Executioner",
            PlayerClass.WARRIOR,
            Role.DAMAGE,
            Range.MELEE,
            SubclassDamageType.PHYSICAL,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
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
