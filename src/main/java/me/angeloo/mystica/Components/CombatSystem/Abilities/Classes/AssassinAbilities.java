package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin.*;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class AssassinAbilities implements AbilitySet {

    //the base abilities, ultimate is stored elsewhere because it is not base
    private final Map<Integer, Ability> abilities = new HashMap<>();

    private final BasicAttackDefinition basic;

    public AssassinAbilities(Mystica main, AbilityManager manager, AbilityLookup lookup){

        basic = new AssassinBasic(main, manager, lookup);

        abilities.put(1, new Assault(main, manager));
        abilities.put(2, new Laceration(main, manager));
        abilities.put(3, new WeaknessStrike(main, manager));
        abilities.put(4, new Pierce(main, manager));
        abilities.put(5, new Dash(main, manager));
        abilities.put(6, new BladeTempest(main, manager));
        abilities.put(7, new FlyingBlade(main, manager));
        abilities.put(8, new Stealth(main, manager));

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
