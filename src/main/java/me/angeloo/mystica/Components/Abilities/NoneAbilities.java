package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.None.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Mystica;
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

    public void useNoneAbility(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                kick.use(player);
                return;
            }
            case 2:{
                flail.use(player);
                return;
            }
            case 3:{
                pocketSand.use(player);
                return;
            }
            case 4:{
                dash.use(player);
                return;
            }
            case 5:{
                noneRoll.use(player);
                return;
            }

            case 6:{
                bandage.use(player);
                return;
            }
            case 7:{
                block.use(player);
                return;
            }
            case 8:{
                adrenaline.use(player);
                return;
            }
        }
    }



    public void useNoneBasic(Player player){
        noneBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:
                return kick.getCooldown(player);
            case 2:
                return flail.getCooldown(player);
            case 3:
                return pocketSand.getCooldown(player);
            case 4:
                return dash.getCooldown(player);
            case 5:
                return noneRoll.getCooldown(player);
            case 6:
                return bandage.getCooldown(player);
            case 7:
                return block.getCooldown(player);
            case 8:
                return adrenaline.getCooldown(player);

        }

        return 0;
    }

    public void resetCooldowns(Player player){
        adrenaline.resetCooldown(player);
        bandage.resetCooldown(player);
        block.resetCooldown(player);
        dash.resetCooldown(player);
        flail.resetCooldown(player);
        kick.resetCooldown(player);
        noneRoll.resetCooldown(player);
        pocketSand.resetCooldown(player);
    }

    public Adrenaline getAdrenaline(){return adrenaline;}

}
