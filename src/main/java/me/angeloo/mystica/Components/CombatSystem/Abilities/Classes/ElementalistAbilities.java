package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySet;
import me.angeloo.mystica.Mystica;

import java.util.HashMap;
import java.util.Map;

public class ElementalistAbilities implements AbilitySet {


    private final Map<Integer, Ability> abilities = new HashMap<>();

    private final BasicAttackDefinition basic;

    public ElementalistAbilities(Mystica main, AbilityManager manager, AbilityLookup lookup){

        basic = new ElementalistBasic(main);

        abilities.put(1, new IceBolt(main, manager));
        abilities.put(2, new FieryMagma(main, manager));
        abilities.put(3, new DescendingInferno(main, manager));
        abilities.put(4, new WindrushForm(main, manager));
        abilities.put(5, new WindWall(main, manager));
        abilities.put(6, new DragonBreathing(main, manager));
        abilities.put(7, new ElementalBreath(main));
        abilities.put(8, new ElementalMatrix(main));

        for(Ability ability : abilities.values()){
            if(ability instanceof BaseAbility base){
                base.setLookup(lookup);
            }
        }

        //elementalistBasic = new ElementalistBasic(main);
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
