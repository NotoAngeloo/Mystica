package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic.*;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;
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
        forceOfWill = new ForceOfWill(main, manager);
        dreadfall = new Dreadfall(main, manager);
        aurora = new Aurora(main, manager, this);
        arcaneContract = new ArcaneContract(main, manager, this);
        lightSigil = new LightSigil(main, manager, this);
        arcaneMissiles = new ArcaneMissiles(main, manager);
        enlightenment = new Enlightenment(main, this, manager);
        mysticBasic = new MysticBasic(main, this);
    }

    public void useMysticAbility(LivingEntity caster, int abilityNumber){

        if(evilSpirit.getIfEvilSpirit(caster)){
            return;
        }

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equals(SubClass.Chaos)){

            switch (abilityNumber) {
                case 1 -> {
                    plagueCurse.use(caster);
                    return;
                }
                case 2 -> {
                    shadowOfDarkness.use(caster);
                    return;
                }
                case 3 -> {
                    healthAbsorb.use(caster);
                    return;
                }
                case 4 -> {
                    spiritualDescent.use(caster);
                    return;
                }
                case 5 -> {
                    warp.use(caster);
                    return;
                }
                case 6 -> {
                    chaosLash.use(caster);
                    return;
                }
                case 7 -> {
                    cursingVoice.use(caster);
                    return;
                }
                case 8 -> {
                    chaosVoid.use(caster);
                    return;
                }
            }

        }

        switch (abilityNumber) {
            case 1 -> {
                arcaneShield.use(caster);
                return;
            }
            case 2 -> {
                purifyingBlast.use(caster);
                return;
            }
            case 3 -> {
                forceOfWill.use(caster);
                return;
            }
            case 4 -> {
                dreadfall.use(caster);
                return;
            }
            case 5 -> {
                warp.use(caster);
                return;
            }
            case 6 -> {
                aurora.use(caster);
                return;
            }
            case 7 -> {
                arcaneContract.use(caster);
                return;
            }
            case 8 -> {
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

        switch (subclass) {
            case Chaos -> {
                evilSpirit.use(caster);
                return;
            }
            case Arcane -> {
                arcaneMissiles.use(caster);
                return;
            }
            case Shepard -> {
                enlightenment.use(caster);
                return;
            }
        }
    }

    public void useMysticBasic(LivingEntity caster){

        mysticBasic.useBasic(caster);
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
