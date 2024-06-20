package me.angeloo.mystica.Managers;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.BuffsAndDebuffs.*;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class BuffAndDebuffManager {

    private final ProfileManager profileManager;

    private final Immune immune;
    private final Hidden hidden;
    private final Immobile immobile;
    private final Sleep sleep;
    private final Stun stun;
    private final GenericShield genericShield;
    private final SpeedUp speedUp;
    private final ShadowCrowsDebuff shadowCrowsDebuff;
    private final WindWallBuff windWallBuff;
    private final ConjuringForceBuff conjuringForceBuff;
    private final WildRoarBuff wildRoarBuff;
    private final Haste haste;
    private final GenericDamageReduction damageReduction;
    private final Silence silence;
    private final WellCrit wellCrit;
    private final Modest modest;
    private final KnockUp knockUp;
    private final FlamingSigilBuff flamingSigilBuff;
    private final BurningBlessingBuff burningBlessingBuff;
    private final PierceBuff pierceBuff;
    private final BladeTempestCrit bladeTempestCrit;
    private final ConcoctionDebuff concoctionDebuff;
    private final Pulled pulled;
    private final Blocking blocking;
    private final PassThrough passThrough;
    private final ArmorMelt armorMelt;

    public BuffAndDebuffManager(Mystica main){
        profileManager = main.getProfileManager();

        flamingSigilBuff = new FlamingSigilBuff(main);
        burningBlessingBuff = new BurningBlessingBuff(main);
        immune = new Immune(main);
        hidden = new Hidden(main);
        immobile = new Immobile(main);
        stun = immobile.getStun();
        sleep = immobile.getSleep();
        knockUp = new KnockUp(main);
        genericShield = new GenericShield();
        speedUp = new SpeedUp();
        shadowCrowsDebuff = new ShadowCrowsDebuff(main);
        windWallBuff = new WindWallBuff(main, this);
        conjuringForceBuff = new ConjuringForceBuff();
        wildRoarBuff = new WildRoarBuff(main);
        haste = new Haste(main);
        damageReduction = new GenericDamageReduction(main);
        silence = new Silence(main);
        wellCrit = new WellCrit();
        modest = new Modest(main);
        pierceBuff = new PierceBuff(main);
        bladeTempestCrit = new BladeTempestCrit(main);
        concoctionDebuff = new ConcoctionDebuff(main);
        pulled = new Pulled(main);
        blocking = new Blocking(main);
        passThrough = new PassThrough();
        armorMelt = new ArmorMelt(main);
    }

    public Immune getImmune(){return immune;}
    public Hidden getHidden(){return hidden;}
    public Immobile getImmobile() {return immobile;}
    public Sleep getSleep(){return sleep;}
    public Stun getStun(){return stun;}
    public WindWallBuff getWindWallBuff() {
        return windWallBuff;
    }
    public ConjuringForceBuff getConjuringForceBuff() {
        return conjuringForceBuff;
    }
    public SpeedUp getSpeedUp(){return speedUp;}
    public GenericShield getGenericShield(){return genericShield;}
    public ShadowCrowsDebuff getShadowCrowsDebuff(){return shadowCrowsDebuff;}
    public WildRoarBuff getWildRoarBuff(){return wildRoarBuff;}
    public Haste getHaste(){return haste;}
    public GenericDamageReduction getDamageReduction(){return damageReduction;}
    public Silence getSilence(){return silence;}
    public WellCrit getWellCrit(){return wellCrit;}
    public Modest getModest(){return modest;}
    public KnockUp getKnockUp(){return knockUp;}
    public FlamingSigilBuff getFlamingSigilBuff(){return flamingSigilBuff;}
    public BurningBlessingBuff getBurningBlessingBuff(){return burningBlessingBuff;}
    public PierceBuff getPierceBuff(){return pierceBuff;}
    public BladeTempestCrit getBladeTempestCrit(){return bladeTempestCrit;}
    public ConcoctionDebuff getConcoctionDebuff(){return concoctionDebuff;}
    public Pulled getPulled(){return pulled;}
    public Blocking getBlocking(){return blocking;}
    public PassThrough getPassThrough(){return passThrough;}
    public ArmorMelt getArmorMelt(){return armorMelt;}

    public void removeAllBuffsAndDebuffs(LivingEntity entity){
        flamingSigilBuff.removeAttackBuff(entity);
        flamingSigilBuff.removeHealthBuff(entity);
        burningBlessingBuff.removeHealthBuff(entity);
        immune.removeImmune(entity);
        sleep.forceWakeUp(entity);
        stun.removeStun(entity);
        immobile.removeImmobile(entity);
        genericShield.removeShields(entity);
        windWallBuff.removeWindwall(entity);
        shadowCrowsDebuff.removeCrowsDebuff(entity);
        wildRoarBuff.removeBuff(entity);
        haste.removeHaste(entity);
        damageReduction.removeReduction(entity);
        silence.removeSilence(entity);
        wellCrit.removeBonus(entity);
        modest.removeModest(entity);
        knockUp.removeKnockUp(entity);
        pierceBuff.removeBuff(entity);
        bladeTempestCrit.removeBonus(entity);
        concoctionDebuff.removeDebuff(entity);
        pulled.removePull(entity);
        blocking.removeBlocking(entity);
        armorMelt.removeMelt(entity);


        if(entity instanceof Player){
            Player player = (Player) entity;

            hidden.unhidePlayer(player);
            speedUp.removeSpeedUp(player);
            conjuringForceBuff.removeConjuringForceBuff(player);
            passThrough.removePassThrough(player);
        }


    }

    //attacker, defender
    public double getTotalDamageMultipliers(LivingEntity attacker, LivingEntity defender){

        return 1 +
                (shadowCrowsDebuff.getIncreasedDamageAmount(defender) +
                        concoctionDebuff.getIncreasedDamageAmount(defender) +
                wildRoarBuff.getMultiplier(attacker))
                * damageReduction.getReduction(defender);
    }

    public double getTotalDamageAddition(LivingEntity damager, LivingEntity entity){

        return 0 + conjuringForceBuff.getExtraDamageAmount(damager)
                - modest.getMultiplier(damager)
                + modest.getMultiplier(entity);

    }

    public double getTotalRangeModifier(LivingEntity entity) {

        return 0 + conjuringForceBuff.getRangeModifier(entity);
    }

    public int getCritBuffAmount(LivingEntity entity){
        return
                wellCrit.getWellCrit(entity)
                + bladeTempestCrit.getTempestCrit(entity);
    }

    public double getAttackBuffAmount(LivingEntity entity){
        return flamingSigilBuff.getAttackMultiplier(entity);
    }

    public double getHealthBuffAmount(LivingEntity entity){
        return flamingSigilBuff.getHealthMultiplier(entity)
                + burningBlessingBuff.getHealthMultiplier(entity);
    }

    public boolean getIfCantAct(LivingEntity entity){
        //use a bunch of || inbetween
        return sleep.getIfSleep(entity)
                || stun.getIfStun(entity)
                || knockUp.getIfKnockUp(entity);
    }

    public boolean getIfInterrupt(LivingEntity entity){
        return
                profileManager.getAnyProfile(entity).getIfDead()
                || getIfCantAct(entity)
                || silence.getSilence(entity)
                || knockUp.getIfKnockUp(entity)
                || pulled.getIfPulled(entity);
    }

    public boolean getIfBasicInterrupt(LivingEntity entity){


        return
                profileManager.getAnyProfile(entity).getIfDead()
                || entity.isDead()
                || getIfCantAct(entity)
                || knockUp.getIfKnockUp(entity)
                || pulled.getIfPulled(entity);


    }



}
