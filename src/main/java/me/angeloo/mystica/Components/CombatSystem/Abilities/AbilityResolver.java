package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.*;
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
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;

public class AbilityResolver{

    private final Map<PlayerClass, AbilitySet> classAbilities = new HashMap<>();
    private final Map<SubClass, Map<Integer, Ability>> subclassOverrides = new HashMap<>();
    private final Map<SubClass, Ability> ultimates = new HashMap<>();


    public AbilityResolver(Mystica main, AbilityManager manager) {

        classAbilities.put(PlayerClass.Elementalist,
                new ElementalistAbilities(main, manager));
        classAbilities.put(PlayerClass.Assassin,
                new AssassinAbilities(main, manager));
        classAbilities.put(PlayerClass.Mystic,
                new MysticAbilities(main, manager));
        classAbilities.put(PlayerClass.NONE,
                new NoneAbilities(main, manager));
        classAbilities.put(PlayerClass.Ranger,
                new PaladinAbilities(main, manager));


        registerOverrides(main, manager);
        registerUltimates(main, manager);
    }

    public Ability resolve(PlayerClass clazz,
                           int abilityNumber){

        if(abilityNumber == -1){
            return null;
            //becuse cant get ultimate from this method
        }

        AbilitySet set = classAbilities.get(clazz);

        return set.get(abilityNumber);
    }

    public Ability resolve(PlayerClass clazz,
                           SubClass subClass,
                           int abilityNumber){

        if(abilityNumber == -1){
            return ultimates.get(subClass);
        }

        Map<Integer, Ability> overrides = subclassOverrides.get(subClass);

        if(overrides != null && overrides.containsKey(abilityNumber)){
            return overrides.get(abilityNumber);
        }

        AbilitySet set = classAbilities.get(clazz);

        return set.get(abilityNumber);
    }

    private void registerOverrides(Mystica main, AbilityManager manager){

        Map<Integer, Ability> chaosOverrides = new HashMap<>();
        chaosOverrides.put(1, new PlagueCurse(main, manager));
        chaosOverrides.put(2, new ShadowOfDarkness(main, manager));
        chaosOverrides.put(3, new HealthAbsorb(main, manager));
        chaosOverrides.put(4, new SpiritualDescent(main, manager));
        //ability 5 is the same
        chaosOverrides.put(6, new ChaosLash(main, manager));
        chaosOverrides.put(7, new CursingVoice(main, manager));
        chaosOverrides.put(8, new ChaosVoid(main, manager));
        subclassOverrides.put(SubClass.Chaos, chaosOverrides);

        Map<Integer, Ability> divineOverrides = new HashMap<>();
        divineOverrides.put(1, new DecreeHonor(main, manager));
        divineOverrides.put(2, new MercifulHealing(main, manager));
        divineOverrides.put(3, new HonorCounter(main, manager));
        divineOverrides.put(4, new DivineInfusion(main, manager));
        divineOverrides.put(5, new SpiritualGift(main, manager));
        divineOverrides.put(6, new SacredAegis(main, manager));
        divineOverrides.put(7, new ModestCalling(main, manager));
        divineOverrides.put(8, new JusticeMark(main, manager));
        subclassOverrides.put(SubClass.Divine, divineOverrides);

    }

    private void registerUltimates(Mystica main, AbilityManager manager){
        ultimates.put(SubClass.Duelist, new DuelistsFrenzy(main, manager));
        ultimates.put(SubClass.Alchemist, new WickedConcoction(main, manager));
        ultimates.put(SubClass.Pyromancer, new FieryWing(main, manager));
        ultimates.put(SubClass.Conjurer, new ConjuringForce(main, manager));
        ultimates.put(SubClass.Arcane, new ArcaneMissiles(main, manager));
        ultimates.put(SubClass.Shepard, new Enlightenment(main, manager));
        ultimates.put(SubClass.Chaos, new EvilSpirit(main, manager));
        ultimates.put(SubClass.Templar, new SanctityShield(main, manager));
        ultimates.put(SubClass.Dawn, new LightWell(main, manager));
        ultimates.put(SubClass.Divine, new Representative(main, manager));
        ultimates.put(SubClass.Scout, new StarVolley(main, manager));
        ultimates.put(SubClass.Tamer, new WildRoar(main, manager));
        ultimates.put(SubClass.Doom, new Annihilation(main, manager));
        ultimates.put(SubClass.Blood, new BloodShield(main, manager));
        ultimates.put(SubClass.Gladiator, new GladiatorHeart(main, manager));
        ultimates.put(SubClass.Executioner, new DeathGaze(main, manager));
    }

}
