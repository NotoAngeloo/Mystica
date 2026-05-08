package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.None.*;
import me.angeloo.mystica.Mystica;

import java.util.HashMap;
import java.util.Map;

public class NoneAbilities implements AbilitySet {


    private final Map<Integer, Ability> abilities = new HashMap<>();

    //TODO: make unspecialized its own thing. take abilties from other classes to use temp

    public NoneAbilities(Mystica main, AbilityManager manager, AbilityLookup lookup){
        //noneBasic = new NoneBasic(main);
        //dash = new Dash(main, manager);
        //noneRoll = new NoneRoll(main, manager);
        //kick = new Kick(main, manager);

        abilities.put(1, new Kick(main));
        abilities.put(2, new Dash(main, manager));
        abilities.put(3, new NoneRoll(main, manager));

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
