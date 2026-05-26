package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin.DuelistsFrenzy;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin.WickedConcoction;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist.ConjuringForce;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist.FieryWing;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger.StarVolley;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger.WildRoar;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight.Annihilation;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight.BloodShield;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior.DeathGaze;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior.GladiatorHeart;
import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;

import java.util.HashMap;
import java.util.Map;

public class AbilityResolver implements AbilityLookup{

    private final Map<PlayerClass, AbilitySet> classAbilities =
            new HashMap<>();

    private final Map<SubClass, Map<Integer, Ability>> subclassOverrides =
            new HashMap<>();

    private final Map<SubClass, Ability> ultimates =
            new HashMap<>();

    private final Map<SubClass, BasicAttackDefinition> subclassBasic =
            new HashMap<>();

    public AbilityResolver(
            Mystica main,
            AbilityManager manager
    ) {

        /*
         * Automatically initialize all class ability sets
         */

        for(PlayerClass clazz : PlayerClass.values()) {

            classAbilities.put(
                    clazz,
                    clazz.createAbilities(
                            main,
                            manager,
                            this
                    )
            );
        }

        registerOverrides(main, manager);
        registerUltimates(main, manager);
    }

    /*
     * Resolve ability with subclass overrides
     */

    public Ability resolve(
            PlayerClass clazz,
            SubClass subClass,
            int abilityNumber
    ) {

        /*
         * Ultimate abilities are handled separately
         */

        if(abilityNumber == -1) {
            return ultimates.get(subClass);
        }

        /*
         * Check subclass override
         */

        Map<Integer, Ability> overrides =
                subclassOverrides.get(subClass);

        if(overrides != null &&
                overrides.containsKey(abilityNumber)) {

            return overrides.get(abilityNumber);
        }

        /*
         * Fall back to base class abilities
         */

        AbilitySet set =
                classAbilities.get(clazz);

        return set.get(abilityNumber);
    }

    /*
     * Resolve base class ability only
     */

    public Ability resolve(
            PlayerClass clazz,
            int abilityNumber
    ) {

        if(abilityNumber == -1) {
            return null;
        }

        AbilitySet set =
                classAbilities.get(clazz);

        return set.get(abilityNumber);
    }

    public Ability resolveUltimate(
            SubClass subClass
    ) {

        return ultimates.get(subClass);
    }

    public BasicAttackDefinition resolveBasic(
            PlayerClass clazz,
            SubClass subClass
    ) {

        BasicAttackDefinition override =
                subclassBasic.get(subClass);

        if(override != null) {
            return override;
        }

        return classAbilities
                .get(clazz)
                .getBasic();
    }

    private void registerOverrides(
            Mystica main,
            AbilityManager manager
    ) {

        /*
         * Chaos overrides
         */

        Map<Integer, Ability> chaosOverrides =
                new HashMap<>();

        chaosOverrides.put(1,
                new PlagueCurse(main, manager));

        chaosOverrides.put(2,
                new ShadowOfDarkness(main, manager));

        chaosOverrides.put(3,
                new HealthAbsorb(main, manager));

        chaosOverrides.put(4,
                new SpiritualDescent(main, manager));

        chaosOverrides.put(6,
                new ChaosLash(main, manager));

        chaosOverrides.put(7,
                new CursingVoice(main, manager));

        chaosOverrides.put(8,
                new ChaosVoid(main, manager));

        subclassOverrides.put(
                SubClass.Chaos,
                chaosOverrides
        );

        subclassBasic.put(
                SubClass.Chaos,
                new ChaosBasic(main, manager)
        );

        /*
         * Divine overrides
         */

        Map<Integer, Ability> divineOverrides =
                new HashMap<>();

        divineOverrides.put(1,
                new DecreeHonor(main, manager));

        divineOverrides.put(2,
                new MercifulHealing(main, manager));

        divineOverrides.put(3,
                new HonorCounter(main, manager));

        divineOverrides.put(4,
                new DivineInfusion(main, manager));

        divineOverrides.put(5,
                new SpiritualGift(main, manager));

        divineOverrides.put(6,
                new SacredAegis(main));

        divineOverrides.put(7,
                new ModestCalling(main, manager));

        divineOverrides.put(8,
                new JusticeMark(main, manager));

        subclassOverrides.put(
                SubClass.Divine,
                divineOverrides
        );

        /*
         * Inject lookup into override abilities
         */

        for(Map<Integer, Ability> overrides :
                subclassOverrides.values()) {

            for(Ability ability : overrides.values()) {

                if(ability instanceof BaseAbility base) {
                    base.setLookup(this);
                }
            }
        }
    }

    private void registerUltimates(
            Mystica main,
            AbilityManager manager
    ) {

        ultimates.put(
                SubClass.Duelist,
                new DuelistsFrenzy(main, manager)
        );

        ultimates.put(
                SubClass.Alchemist,
                new WickedConcoction(main, manager)
        );

        ultimates.put(
                SubClass.Pyromancer,
                new FieryWing(main, manager)
        );

        ultimates.put(
                SubClass.Conjurer,
                new ConjuringForce(main)
        );

        ultimates.put(
                SubClass.Arcane,
                new ArcaneMissiles(main, manager)
        );

        ultimates.put(
                SubClass.Shepard,
                new Enlightenment(main, manager)
        );

        ultimates.put(
                SubClass.Chaos,
                new EvilSpirit(main, manager)
        );

        ultimates.put(
                SubClass.Templar,
                new SanctityShield(main)
        );

        ultimates.put(
                SubClass.Dawn,
                new LightWell(main, manager)
        );

        ultimates.put(
                SubClass.Divine,
                new Representative(main)
        );

        ultimates.put(
                SubClass.Scout,
                new StarVolley(main, manager)
        );

        ultimates.put(
                SubClass.Tamer,
                new WildRoar(main)
        );

        ultimates.put(
                SubClass.Doom,
                new Annihilation(main, manager)
        );

        ultimates.put(
                SubClass.Blood,
                new BloodShield(main, manager)
        );

        ultimates.put(
                SubClass.Gladiator,
                new GladiatorHeart(main)
        );

        ultimates.put(
                SubClass.Executioner,
                new DeathGaze(main, manager)
        );

        /*
         * Inject lookup into ultimates
         */

        for(Ability ultimate : ultimates.values()) {

            if(ultimate instanceof BaseAbility base) {
                base.setLookup(this);
            }
        }
    }

    @Override
    public Ability get(
            PlayerClass clazz,
            SubClass subClass,
            int abilityNumber
    ) {

        return resolve(
                clazz,
                subClass,
                abilityNumber
        );
    }

    @Override
    public Ability get(
            PlayerClass clazz,
            int abilityNumber
    ) {

        return resolve(
                clazz,
                abilityNumber
        );
    }


}
