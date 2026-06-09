package me.angeloo.mystica.Components.CombatSystem.Classes;

import me.angeloo.mystica.Components.ProfileComponents.StatProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum SubClass {

    DUELIST("Duelist",
            PlayerClass.ASSASSIN,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10),
            List.of(
                    "This is a test description",
                    "here is another line")
    ),

    ALCHEMIST("Alchemist",
            PlayerClass.ASSASSIN,
            Role.DAMAGE,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    PYROMANCER("Pyromancer",
            PlayerClass.ELEMENTALIST,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    CONJURER("Conjurer",
            PlayerClass.ELEMENTALIST,
            Role.DAMAGE,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    SHEPARD("Shepard",
            PlayerClass.MYSTIC,
            Role.HEALER,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    ARCANE("Arcane",
            PlayerClass.MYSTIC,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    CHAOS("Chaos",
            PlayerClass.MYSTIC,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    TEMPLAR("Templar",
            PlayerClass.PALADIN,
            Role.TANK,
            new StatProfile(-1,15,1,1,0),
            Collections.EMPTY_LIST
    ),

    DAWN("Dawn",
            PlayerClass.PALADIN,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    DIVINE("Divine",
            PlayerClass.PALADIN,
            Role.HEALER,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    SCOUT("Scout",
            PlayerClass.RANGER,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    TAMER("Animal Tamer",
            PlayerClass.RANGER,
            Role.DAMAGE,
            new StatProfile(0,15,0,0,0),
            Collections.EMPTY_LIST
    ),

    DOOM("Doom",
            PlayerClass.SHADOW_KNIGHT,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    ),

    BLOOD("Blood",
            PlayerClass.SHADOW_KNIGHT,
            Role.TANK,
            new StatProfile(-1,15,1,1,0),
            Collections.EMPTY_LIST
    ),

    GLADIATOR("Gladiator",
            PlayerClass.WARRIOR,
            Role.TANK,
            new StatProfile(-1,15,1,1,0),
            Collections.EMPTY_LIST
    ),

    EXECUTIONER("Executioner",
            PlayerClass.WARRIOR,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10),
            Collections.EMPTY_LIST
    );


    private final String displayName;
    private final PlayerClass parentClass;
    private final Role role;
    private final StatProfile statProfile;
    private final List<String> description;

    SubClass(
            String displayName,
            PlayerClass parentClass,
            Role role,
            StatProfile statProfile,
            List<String> description
    ) {

        this.displayName = displayName;
        this.parentClass = parentClass;
        this.role = role;
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

    public StatProfile getStatProfile() {
        return statProfile;
    }

    public List<String> getDescription(){return description;}
}
