package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Mystic.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.SubClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class MysticAbilities {

    private final ProfileManager profileManager;
    private final Mana mana;
    private final Consolation consolation;
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
        mana = new Mana(main, manager);
        consolation = new Consolation(main);
        evilSpirit = new EvilSpirit(main, manager);
        plagueCurse = new PlagueCurse(main, manager, this);
        chaosVoid = new ChaosVoid(main, manager);
        shadowOfDarkness = new ShadowOfDarkness(main, manager, this);
        warp = new Warp(main, manager);
        spiritualDescent = new SpiritualDescent(main, manager, this);
        chaosLash = new ChaosLash(main, manager, this);
        cursingVoice = new CursingVoice(main, manager);
        healthAbsorb = new HealthAbsorb(main, manager);
        arcaneShield = new ArcaneShield(main, manager, this);
        purifyingBlast = new PurifyingBlast(main, manager, this);
        forceOfWill = new ForceOfWill(main, manager, this);
        dreadfall = new Dreadfall(main, manager);
        aurora = new Aurora(main, manager, this);
        arcaneContract = new ArcaneContract(main, manager, this);
        lightSigil = new LightSigil(main, manager, this);
        arcaneMissiles = new ArcaneMissiles(main, manager);
        enlightenment = new Enlightenment(main, manager, this);
        mysticBasic = new MysticBasic(main, manager, this);
    }

    public void useMysticAbility(LivingEntity caster, int abilityNumber){

        if(evilSpirit.getIfEvilSpirit(caster)){
            return;
        }

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equals(SubClass.Chaos)){

            switch (abilityNumber){
                case 1:{
                    chaosVoid.use(caster);
                    return;
                }
                case 2:{
                    plagueCurse.use(caster);
                    return;
                }
                case 3:{
                    shadowOfDarkness.use(caster);
                    return;
                }
                case 4:{
                    warp.use(caster);
                    return;
                }
                case 5:{
                    spiritualDescent.use(caster);
                    return;
                }
                case 6:{
                    chaosLash.use(caster);
                    return;
                }
                case 7:{
                    cursingVoice.use(caster);
                    return;
                }
                case 8:{
                    healthAbsorb.use(caster);
                    return;
                }
            }

        }

        switch (abilityNumber){
            case 1:{
                arcaneShield.use(caster);
                return;
            }
            case 2:{
                purifyingBlast.use(caster);
                return;
            }
            case 3:{
                forceOfWill.use(caster);
                return;
            }
            case 4:{
                dreadfall.use(caster);
                return;
            }
            case 5:{
                warp.use(caster);
                return;
            }
            case 6:{
                aurora.use(caster);
                return;
            }
            case 7:{
                arcaneContract.use(caster);
                return;
            }
            case 8:{
                lightSigil.use(caster);
                return;
            }
        }

    }

    public void useMysticUltimate(LivingEntity caster){

        if(evilSpirit.getIfEvilSpirit(caster)){
            return;
        }

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass){
            case Chaos:{
                evilSpirit.use(caster);
                return;
            }
            case Arcane:{
                arcaneMissiles.use(caster);
                return;
            }
            case Shepard:{
                enlightenment.use(caster);
                return;
            }
        }
    }

    public void useMysticBasic(LivingEntity caster){

        mysticBasic.useBasic(caster);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equals(SubClass.Chaos)){

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

        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass){
            case Chaos:{
                return 0;
            }
            case Arcane:{
                return arcaneMissiles.getSkillCooldown();
            }
            case Shepard:{
                return enlightenment.getSkillCooldown();
            }
        }

        return 0;
    }

    public int getPlayerUltimateCooldown(Player player){

        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass){
            case Chaos:{
                return 0;
            }
            case Arcane:{
                return arcaneMissiles.getPlayerCooldown(player);
            }
            case Shepard:{
                return enlightenment.getPlayerCooldown(player);
            }
        }

        return 0;
    }

    public void resetCooldowns(LivingEntity caster){
        arcaneContract.resetCooldown(caster);
        arcaneMissiles.resetCooldown(caster);
        arcaneShield.resetCooldown(caster);
        aurora.resetCooldown(caster);
        chaosLash.resetCooldown(caster);
        chaosVoid.resetCooldown(caster);
        cursingVoice.resetCooldown(caster);
        dreadfall.resetCooldown(caster);
        enlightenment.resetCooldown(caster);
        forceOfWill.resetCooldown(caster);
        healthAbsorb.resetCooldown(caster);
        lightSigil.resetCooldown(caster);
        plagueCurse.resetCooldown(caster);
        purifyingBlast.resetCooldown(caster);
        shadowOfDarkness.resetCooldown(caster);
        spiritualDescent.resetCooldown(caster);
        warp.resetCooldown(caster);
    }

    public int getChaosMysticModelData(Player player){
        return evilSpirit.returnWhichItem(player);
    }

    public EvilSpirit getEvilSpirit(){return evilSpirit;}
    public PlagueCurse getPlagueCurse(){return plagueCurse;}
    public PurifyingBlast getPurifyingBlast(){return purifyingBlast;}
    public ChaosVoid getChaosVoid(){return chaosVoid;}
    public ShadowOfDarkness getShadowOfDarkness(){return shadowOfDarkness;}
    public Warp getWarp() {
        return warp;
    }
    public SpiritualDescent getSpiritualDescent(){return spiritualDescent;}
    public ChaosLash getChaosLash(){return chaosLash;}
    public CursingVoice getCursingVoice(){return cursingVoice;}
    public HealthAbsorb getHealthAbsorb(){return healthAbsorb;}
    public  ArcaneShield getArcaneShield(){return arcaneShield;}
    public ForceOfWill getForceOfWill(){return forceOfWill;}
    public Dreadfall getDreadfall(){return dreadfall;}
    public Aurora getAurora(){return aurora;}
    public ArcaneContract getArcaneContract(){return arcaneContract;}
    public LightSigil getLightSigil(){return lightSigil;}
    public MysticBasic getMysticBasic(){return mysticBasic;}
    public ArcaneMissiles getArcaneMissiles(){return arcaneMissiles;}
    public Enlightenment getEnlightenment(){return enlightenment;}
    public Consolation getConsolation(){return consolation;}
    public Mana getMana(){return mana;}
}
