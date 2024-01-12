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
    private final ArcaneShield arcaneShield;
    private final PurifyingBlast purifyingBlast;
    private final ForceOfWill forceOfWill;
    private final Dreadfall dreadfall;
    private final Aurora aurora;
    private final ArcaneContract arcaneContract;
    private final LightSigil lightSigil;
    private final ArcaneMissiles arcaneMissiles;
    private final Enlightenment enlightenment;
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
        healthAbsorb = new HealthAbsorb(main, manager);
        arcaneShield = new ArcaneShield(main, manager);
        purifyingBlast = new PurifyingBlast(main, manager);
        forceOfWill = new ForceOfWill(main, manager);
        dreadfall = new Dreadfall(main, manager);
        aurora = new Aurora(main, manager);
        arcaneContract = new ArcaneContract(main, manager);
        lightSigil = new LightSigil(main, manager, this);
        arcaneMissiles = new ArcaneMissiles(main, manager);
        enlightenment = new Enlightenment(main, manager, this);
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
            case 3:{
                forceOfWill.use(player);
                return;
            }
            case 4:{
                dreadfall.use(player);
                return;
            }
            case 5:{
                warp.use(player);
                return;
            }
            case 6:{
                aurora.use(player);
                return;
            }
            case 7:{
                arcaneContract.use(player);
                return;
            }
            case 8:{
                lightSigil.use(player);
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
                arcaneMissiles.use(player);
                return;
            }
            case "shepard":{
                enlightenment.use(player);
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
            case 3:
                return forceOfWill.getCooldown(player);
            case 4:
                return dreadfall.getCooldown(player);
            case 5:
                return warp.getCooldown(player);
            case 6:
                return aurora.getCooldown(player);
            case 7:
                return arcaneContract.getCooldown(player);
            case 8:
                return lightSigil.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "chaos":{
                return 0;
            }
            case "arcane master":{
                return arcaneMissiles.getCooldown(player);
            }
            case "shepard":{
                return enlightenment.getCooldown(player);
            }
        }

        return 0;
    }

    public int getChaosMysticModelData(Player player){
        return evilSpirit.returnWhichItem(player);
    }

    public EvilSpirit getEvilSpirit(){return evilSpirit;}
    public PlagueCurse getPlagueCurse(){return plagueCurse;}
    public PurifyingBlast getPurifyingBlast(){return purifyingBlast;}
}
