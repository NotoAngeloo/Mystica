package me.angeloo.mystica.Components.CombatSystem.Classes;

import me.angeloo.mystica.Components.ProfileComponents.StatProfile;

public enum SubClass {

    DUELIST("Duelist",
            PlayerClass.ASSASSIN,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10)
    ),

    ALCHEMIST("Alchemist",
            PlayerClass.ASSASSIN,
            Role.DAMAGE,
            new StatProfile(0,15,0,0,0)
    ),

    PYROMANCER("Pyromancer",
            PlayerClass.ELEMENTALIST,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10)
    ),

    CONJURER("Conjurer",
            PlayerClass.ELEMENTALIST,
            Role.DAMAGE,
            new StatProfile(0,15,0,0,0)
    ),

    SHEPARD("Shepard",
            PlayerClass.MYSTIC,
            Role.HEALER,
            new StatProfile(0,15,0,0,0)
    ),

    ARCANE("Arcane",
            PlayerClass.MYSTIC,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10)
    ),

    CHAOS("Chaos",
            PlayerClass.MYSTIC,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10)
    ),

    TEMPLAR("Templar",
            PlayerClass.PALADIN,
            Role.TANK,
            new StatProfile(-1,15,1,1,0)
    ),

    DAWN("Dawn",
            PlayerClass.PALADIN,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10)
    ),

    DIVINE("Divine",
            PlayerClass.PALADIN,
            Role.HEALER,
            new StatProfile(0,15,0,0,0)
    ),

    SCOUT("Scout",
            PlayerClass.RANGER,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10)
    ),

    TAMER("Animal Tamer",
            PlayerClass.RANGER,
            Role.DAMAGE,
            new StatProfile(0,15,0,0,0)
    ),

    DOOM("Doom",
            PlayerClass.SHADOW_KNIGHT,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10)
    ),

    BLOOD("Blood",
            PlayerClass.SHADOW_KNIGHT,
            Role.TANK,
            new StatProfile(-1,15,1,1,0)
    ),

    GLADIATOR("Gladiator",
            PlayerClass.WARRIOR,
            Role.TANK,
            new StatProfile(-1,15,1,1,0)
    ),

    EXECUTIONER("Executioner",
            PlayerClass.WARRIOR,
            Role.DAMAGE,
            new StatProfile(1,0,0,0,10)
    );


    private final String displayName;
    private final PlayerClass parentClass;
    private final Role role;
    private final StatProfile statProfile;

    SubClass(
            String displayName,
            PlayerClass parentClass,
            Role role,
            StatProfile statProfile
    ) {

        this.displayName = displayName;
        this.parentClass = parentClass;
        this.role = role;
        this.statProfile = statProfile;
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
}
