package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic.*;
import me.angeloo.mystica.Mystica;

import java.util.HashMap;
import java.util.Map;


public class MysticAbilities implements AbilitySet {



    private final Map<Integer, Ability> abilities = new HashMap<>();

    private final BasicAttackDefinition basic;

    public MysticAbilities(Mystica main, AbilityManager manager, AbilityLookup lookup){

        basic = new MysticBasic(main, manager);

        abilities.put(1, new ArcaneShield(main, manager));
        abilities.put(2, new PurifyingBlast(main, manager));
        abilities.put(3, new ForceOfWill(main, manager));
        abilities.put(4, new Dreadfall(main, manager));
        abilities.put(5, new Warp(main, manager));
        abilities.put(6, new Aurora(main, manager));
        abilities.put(7, new ArcaneContract(main, manager));
        abilities.put(8, new LightSigil(main, manager));

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
        return basic;
    }


}
