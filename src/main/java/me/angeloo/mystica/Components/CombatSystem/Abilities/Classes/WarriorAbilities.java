package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior.*;
import me.angeloo.mystica.Mystica;

import java.util.HashMap;
import java.util.Map;

public class WarriorAbilities implements AbilitySet {


    private final Map<Integer, Ability> abilities = new HashMap<>();

    public WarriorAbilities(Mystica main, AbilityManager manager, AbilityLookup lookup){


        abilities.put(1, new LavaQuake(main, manager));
        abilities.put(2, new SearingChains(main, manager));
        abilities.put(3, new TempestRage(main, manager));
        abilities.put(4, new MeteorCrater(main, manager));
        abilities.put(5, new AnvilDrop(main, manager));
        abilities.put(6, new FlamingSigil(main, manager));
        abilities.put(7, new MagmaSpikes(main, manager));
        abilities.put(8, new BurningBlessing(main, manager));

        for(Ability ability : abilities.values()){
            if(ability instanceof BaseAbility base){
                base.setLookup(lookup);
            }
        }
    }

    @Override
    public Ability get(int abilityNumber){
        return abilities.get(abilityNumber);
    }

    @Override
    public BasicAttackDefinition getBasic() {
        return null;
    }



}
