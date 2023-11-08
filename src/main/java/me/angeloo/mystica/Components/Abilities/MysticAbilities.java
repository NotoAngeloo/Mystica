package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Mystic.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;


public class MysticAbilities {

    private final ProfileManager profileManager;

    private final EvilSpirit evilSpirit;
    private final PlagueCurse plagueCurse;
    private final ChaosVoid chaosVoid;
    private final ShadowOfDarkness shadowOfDarkness;
    private final Warp warp;
    private final SpiritualDescent spiritualDescent;
    private final ChaosLash chaosLash;
    private final CursingVoice cursingVoice;
    private final HealthAbsorb healthAbsorb;
    //sigil here, for purifying blast no cast time buff
    private final ArcaneShield arcaneShield;
    private final PurifyingBlast purifyingBlast;
    private final MysticBasic mysticBasic;

    public MysticAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        evilSpirit = new EvilSpirit(main, manager);
        plagueCurse = new PlagueCurse(main, manager, this);
        chaosVoid = new ChaosVoid(main, manager);
        shadowOfDarkness = new ShadowOfDarkness(main, manager, this);
        warp = new Warp(main, manager);
        spiritualDescent = new SpiritualDescent(main, manager, this);
        chaosLash = new ChaosLash(main, manager, this);
        cursingVoice = new CursingVoice(main, manager);
        healthAbsorb = new HealthAbsorb(main, manager, this);
        arcaneShield = new ArcaneShield(main, manager);
        purifyingBlast = new PurifyingBlast(main, manager);
        mysticBasic = new MysticBasic(main, manager, this);
    }

    public void useMysticAbility(Player player, int abilityNumber){

        if(evilSpirit.getIfEvilSpirit(player)){
            return;
        }

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("chaos")){

            switch (abilityNumber){
                case 1:{
                    chaosVoid.use(player);
                    return;
                }
                case 2:{
                    plagueCurse.use(player);
                    return;
                }
                case 3:{
                    shadowOfDarkness.use(player);
                    return;
                }
                case 4:{
                    warp.use(player);
                    return;
                }
                case 5:{
                    spiritualDescent.use(player);
                    return;
                }
                case 6:{
                    chaosLash.use(player);
                    return;
                }
                case 7:{
                    cursingVoice.use(player);
                    return;
                }
                case 8:{
                    healthAbsorb.use(player);
                    return;
                }
            }

        }

        switch (abilityNumber){
            case 1:{
                arcaneShield.use(player);
                return;
            }
            case 2:{
                purifyingBlast.use(player);
                return;
            }
            case 4:{
                warp.use(player);
                return;
            }
        }

    }

    public void useMysticUltimate(Player player){

        if(evilSpirit.getIfEvilSpirit(player)){
            return;
        }

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "chaos":{
                evilSpirit.use(player);
                return;
            }
            case "arcane master":{

                return;
            }
            case "shepard":{

                return;
            }
        }
    }

    public void useMysticBasic(Player player){

        mysticBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("chaos")){

            switch (abilityNumber){
                case 1:
                    return chaosVoid.getCooldown(player);
                case 2:
                    return plagueCurse.getCooldown(player);
                case 3:
                    return shadowOfDarkness.getCooldown(player);
                case 4:
                    return warp.getCooldown(player);
                case 5:
                    return spiritualDescent.getCooldown(player);
                case 6:
                    return chaosLash.getCooldown(player);
                case 7:
                    return cursingVoice.getCooldown(player);
                case 8:
                    return healthAbsorb.getCooldown(player);
            }

        }

        switch (abilityNumber){
            case 1:
                return arcaneShield.getCooldown(player);
            case 2:
                return purifyingBlast.getCooldown(player);
            case 4:
                return warp.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "chaos":{
                return evilSpirit.getIfReady(player);
            }
            case "arcane master":{


            }
            case "shepard":{


            }
        }

        return 0;
    }

    public EvilSpirit getEvilSpirit(){return evilSpirit;}
    public PlagueCurse getPlagueCurse(){return plagueCurse;}
}
