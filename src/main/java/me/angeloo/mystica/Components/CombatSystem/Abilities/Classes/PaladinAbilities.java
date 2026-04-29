package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin.*;
import me.angeloo.mystica.Mystica;

import java.util.HashMap;
import java.util.Map;

public class PaladinAbilities implements AbilitySet {



    private final Map<Integer, Ability> abilities = new HashMap<>();

    public PaladinAbilities(Mystica main, AbilityManager manager, AbilityLookup lookup){


        abilities.put(1, new TorahSword(main, manager));
        abilities.put(2, new DivineGuidance(main, manager));
        abilities.put(3, new ReigningSword(main, manager));
        abilities.put(4, new CovenantSword(main, manager));
        abilities.put(5, new OrderShield(main, manager));
        abilities.put(6, new GloryOfPaladins(main, manager));
        abilities.put(7, new DuranceOfTruth(main, manager));
        abilities.put(8, new Judgement(main, manager));

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
