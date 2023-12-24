package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Paladin.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class PaladinAbilities {

    private final ProfileManager profileManager;

    private final GloryOfPaladins gloryOfPaladins;
    private final PaladinBasic paladinBasic;
    private final TorahSword torahSword;
    private final DivineGuidance divineGuidance;
    private final CovenantSword covenantSword;
    private final ReigningSword reigningSword;
    private final OrderShield orderShield;
    private final DuranceOfTruth duranceOfTruth;
    private final Judgement judgement;

    public PaladinAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        judgement = new Judgement(main, manager);
        gloryOfPaladins = new GloryOfPaladins(main, manager);
        paladinBasic = new PaladinBasic(main, manager, this);
        torahSword = new TorahSword(main, manager, this);
        divineGuidance = new DivineGuidance(main, manager);
        covenantSword = new CovenantSword(main, manager);
        reigningSword = new ReigningSword(main, manager);
        orderShield = new OrderShield(main, manager);
        duranceOfTruth = new DuranceOfTruth(main, manager);

    }

    public void usePaladinAbility(Player player, int abilityNumber){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("divine")){
            switch (abilityNumber){
                case 1:{
                    return;
                }
                case 2:{
                    return;
                }
                case 3:{
                    return;
                }
                case 4:{
                    return;
                }
                case 5:{
                    return;
                }
                case 6:{
                    return;
                }
                case 7:{
                    return;
                }
                case 8:{
                    return;
                }
            }
        }

        switch (abilityNumber){
            case 1:{
                torahSword.use(player);
                return;
            }
            case 2:{
                divineGuidance.use(player);
                return;
            }
            case 3:{
                reigningSword.use(player);
                return;
            }
            case 4:{
                covenantSword.use(player);
                return;
            }
            case 5:{
                orderShield.use(player);
                return;
            }
            case 6:{
                gloryOfPaladins.use(player);
                return;
            }
            case 7:{
                duranceOfTruth.use(player);
                return;
            }
            case 8:{
                judgement.use(player);
                return;
            }
        }
    }

    public void usePaladinUltimate(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "templar":{
                return;
            }
            case "divine":{
                return;
            }
            case "dawn":{
                return;
            }
        }
    }

    public void usePaladinBasic(Player player){
        paladinBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("divine")){
            switch (abilityNumber){
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
            }
        }

        switch (abilityNumber){
            case 1:
                return torahSword.getCooldown(player);
            case 2:
                return divineGuidance.getCooldown(player);
            case 3:
                return reigningSword.getCooldown(player);
            case 4:
                return covenantSword.getCooldown(player);
            case 5:
                return orderShield.getCooldown(player);
            case 6:
                return gloryOfPaladins.getCooldown(player);
            case 7:
                return duranceOfTruth.getCooldown(player);
            case 8:
                return judgement.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "templar":

            case "divine":

            case "dawn":

        }

        return 0;
    }

    public GloryOfPaladins getGloryOfPaladins(){
        return gloryOfPaladins;
    }
    public Judgement getJudgement(){return judgement;}

}
