package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.None.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NoneAbilities {

    private final NoneBasic noneBasic;
    private final Adrenaline adrenaline;
    private final Dash dash;
    private final NoneRoll noneRoll;
    private final Flail flail;
    private final Kick kick;
    private final PocketSand pocketSand;
    private final Bandage bandage;
    private final Block block;

    public NoneAbilities(Mystica main, AbilityManager manager){
        adrenaline = new Adrenaline(main, manager);
        noneBasic = new NoneBasic(main, manager, this);
        dash = new Dash(main, manager);
        noneRoll = new NoneRoll(main, manager);
        flail = new Flail(main, manager, this);
        kick = new Kick(main, manager, this);
        pocketSand = new PocketSand(main, manager, this);
        bandage = new Bandage(main, manager);
        block = new Block(main, manager);
    }

    public void useNoneAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                kick.use(caster);
                return;
            }
            case 2:{
                flail.use(caster);
                return;
            }
            case 3:{
                pocketSand.use(caster);
                return;
            }
            case 4:{
                dash.use(caster);
                return;
            }
            case 5:{
                noneRoll.use(caster);
                return;
            }

            case 6:{
                bandage.use(caster);
                return;
            }
            case 7:{
                block.use(caster);
                return;
            }
            case 8:{
                adrenaline.use(caster);
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
                return flail.getCooldown(caster);
            case 3:
                return pocketSand.getCooldown(caster);
            case 4:
                return dash.getCooldown(caster);
            case 5:
                return noneRoll.getCooldown(caster);
            case 6:
                return bandage.getCooldown(caster);
            case 7:
                return block.getCooldown(caster);
            case 8:
                return adrenaline.getCooldown(caster);

        }

        return 0;
    }

    public void resetCooldowns(LivingEntity caster){
        adrenaline.resetCooldown(caster);
        bandage.resetCooldown(caster);
        block.resetCooldown(caster);
        dash.resetCooldown(caster);
        flail.resetCooldown(caster);
        kick.resetCooldown(caster);
        noneRoll.resetCooldown(caster);
        pocketSand.resetCooldown(caster);
    }

    public Adrenaline getAdrenaline(){return adrenaline;}

}
