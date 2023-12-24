package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.BuffsAndDebuffs.*;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class BuffAndDebuffManager {

    private final Immune immune;
    private final Hidden hidden;
    private final Immobile immobile;
    private final Sleep sleep;
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

    public BuffAndDebuffManager(Mystica main){
        immune = new Immune(main);
        hidden = new Hidden(main);
        immobile = new Immobile(main);
        sleep = new Sleep(main, immobile);
        genericShield = new GenericShield();
        speedUp = new SpeedUp();
        shadowCrowsDebuff = new ShadowCrowsDebuff(main);
        windWallBuff = new WindWallBuff(main);
        conjuringForceBuff = new ConjuringForceBuff();
        wildRoarBuff = new WildRoarBuff(main);
        haste = new Haste(main);
        damageReduction = new GenericDamageReduction(main);
        silence = new Silence(main);
        wellCrit = new WellCrit();
    }

    public Immune getImmune(){return immune;}
    public Hidden getHidden(){return hidden;}
    public Immobile getImmobile() {return immobile;}
    public Sleep getSleep(){return sleep;}
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

    public void removeAllBuffsAndDebuffs(Player player){
        immune.removeImmune(player);
        hidden.unhidePlayer(player);
        immobile.removeImmobile(player);
        sleep.forceWakeUp(player);
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
    }



    //attacker, defender
    public double getTotalDamageMultipliers(LivingEntity attacker, LivingEntity defender){

        return 1 +
                (shadowCrowsDebuff.getIncreasedDamageAmount(defender) +
                wildRoarBuff.getMultiplier(attacker))
                * damageReduction.getReduction(defender);
    }

    public double getTotalDamageAddition(Player player){

        return 0 + conjuringForceBuff.getExtraDamageAmount(player);

    }

    public double getTotalRangeModifier(Player player) {

        return 0 + conjuringForceBuff.getRangeModifier(player);
    }

    public int getCritBuffAmount(LivingEntity entity){
        return wellCrit.getWellCrit(entity);
    }


    public boolean getIfCantAct(LivingEntity entity){
        //use a bunch of || inbetween
        return sleep.getIfSleep(entity);
    }

    public boolean getIfInterrupt(LivingEntity entity){
        return getIfCantAct(entity) && getSilence().getSilence(entity);
    }
}
