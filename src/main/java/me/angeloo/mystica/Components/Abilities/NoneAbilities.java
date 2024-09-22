package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.None.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;

public class NoneAbilities {

    private final NoneBasic noneBasic;
    private final Dash dash;
    private final NoneRoll noneRoll;
    private final Kick kick;

    public NoneAbilities(Mystica main, AbilityManager manager){
        noneBasic = new NoneBasic(main, manager, this);
        dash = new Dash(main, manager);
        noneRoll = new NoneRoll(main, manager);
        kick = new Kick(main, manager);
    }

    public void useNoneAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                kick.use(caster);
                return;
            }

            case 2:{
                dash.use(caster);
                return;
            }
            case 3:{
                noneRoll.use(caster);
                return;
            }
        }
    }



    public void useNoneBasic(LivingEntity caster){
        noneBasic.useBasic(caster);
    }

    public int getAbilityCooldown(LivingEntity caster, int abilityNumber){

        switch (abilityNumber){
            case 1:
                return kick.getCooldown(caster);
            case 2:
                return dash.getCooldown(caster);
            case 3:
                return noneRoll.getCooldown(caster);
        }

        return 0;
    }

    public void resetCooldowns(LivingEntity caster){
        dash.resetCooldown(caster);
        kick.resetCooldown(caster);
        noneRoll.resetCooldown(caster);
    }


}
