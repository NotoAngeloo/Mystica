package me.angeloo.mystica.Managers;

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

    public void removeAllBuffsAndDebuffs(Player player){
        flamingSigilBuff.removeAttackBuff(player);
        flamingSigilBuff.removeHealthBuff(player);
        burningBlessingBuff.removeHealthBuff(player);
        immune.removeImmune(player);
        hidden.unhidePlayer(player);
        immobile.removeImmobile(player);
        sleep.forceWakeUp(player);
        stun.removeStun(player);
        speedUp.removeSpeedUp(player);
        genericShield.removeShields(player);
        windWallBuff.removeWindwall(player);
        conjuringForceBuff.removeConjuringForceBuff(player);
        shadowCrowsDebuff.removeCrowsDebuff(player);
        wildRoarBuff.removeBuff(player);
        haste.removeHaste(player);
        damageReduction.removeReduction(player);
        silence.removeSilence(player);
        wellCrit.removeBonus(player);
        modest.removeModest(player);
        knockUp.removeKnockUp(player);
        pierceBuff.removeBuff(player);
        bladeTempestCrit.removeBonus(player);
        concoctionDebuff.removeDebuff(player);
    }

    //attacker, defender
    public double getTotalDamageMultipliers(LivingEntity attacker, LivingEntity defender){

        return 1 +
                (shadowCrowsDebuff.getIncreasedDamageAmount(defender) +
                        concoctionDebuff.getIncreasedDamageAmount(defender) +
                wildRoarBuff.getMultiplier(attacker))
                * damageReduction.getReduction(defender);
    }

    public double getTotalDamageAddition(Player player, LivingEntity entity){

        return 0 + conjuringForceBuff.getExtraDamageAmount(player)
                - modest.getMultiplier(player)
                + modest.getMultiplier(entity);

    }

    public double getTotalRangeModifier(Player player) {

        return 0 + conjuringForceBuff.getRangeModifier(player);
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
                || knockUp.getIfKnockUp(entity);
    }
}
