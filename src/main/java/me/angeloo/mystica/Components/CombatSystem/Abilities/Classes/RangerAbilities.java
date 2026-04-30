package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger.*;
import me.angeloo.mystica.Mystica;

import java.util.HashMap;
import java.util.Map;

public class RangerAbilities implements AbilitySet {


    private final Map<Integer, Ability> abilities = new HashMap<>();

    private final BasicAttackDefinition basic;

    public RangerAbilities(Mystica main, AbilityManager manager, AbilityLookup lookup){

        basic = new RangerBasic(main, manager, lookup);

        abilities.put(1, new BitingRain(main, manager));
        abilities.put(2, new ShadowCrows(main, manager));
        abilities.put(3, new Relentless(main, manager));
        abilities.put(4, new RazorWind(main, manager));
        abilities.put(5, new BlessedArrow(main, manager));
        abilities.put(6, new RallyingCry(main, manager));
        abilities.put(7, new WildSpirit(main, manager));
        abilities.put(8, new Roll(main, manager));

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
