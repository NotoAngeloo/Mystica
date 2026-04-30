package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;


import me.angeloo.mystica.Components.CombatSystem.Abilities.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight.*;
import me.angeloo.mystica.Mystica;

import java.util.HashMap;
import java.util.Map;

public class ShadowKnightAbilities implements AbilitySet {


    private final Map<Integer, Ability> abilities = new HashMap<>();

    private final BasicAttackDefinition basic;

    public ShadowKnightAbilities(Mystica main, AbilityManager manager, AbilityLookup lookup){

        basic = new ShadowKnightBasic(main, manager, lookup);

        abilities.put(1, new Infection(main, manager));
        abilities.put(2, new SpiritualAttack(main, manager));
        abilities.put(3, new BurialGround(main, manager));
        abilities.put(4, new Bloodsucker(main, manager));
        abilities.put(5, new SoulReap(main, manager));
        abilities.put(6, new ShadowGrip(main, manager));
        abilities.put(7, new SpectralSteed(main, manager));
        abilities.put(8, new  Soulcrack(main, manager));


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
