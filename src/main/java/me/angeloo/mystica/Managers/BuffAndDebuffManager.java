package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.BuffsAndDebuffs.*;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class BuffAndDebuffManager {

    private final GenericShield genericShield;
    private final SpeedUp speedUp;
    private final ShadowCrowsDebuff shadowCrowsDebuff;
    private final WindWallBuff windWallBuff;
    private final ConjuringForceBuff conjuringForceBuff;
    private final WildRoarBuff wildRoarBuff;
    private final Haste haste;

    public BuffAndDebuffManager(Mystica main){
        genericShield = new GenericShield();
        speedUp = new SpeedUp();
        shadowCrowsDebuff = new ShadowCrowsDebuff(main);
        windWallBuff = new WindWallBuff(main);
        conjuringForceBuff = new ConjuringForceBuff();
        wildRoarBuff = new WildRoarBuff(main);
        haste = new Haste(main);
    }


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

    public void removeAllBuffsAndDebuffs(Player player){
        speedUp.removeSpeedUp(player);
        genericShield.removeShields(player);
        windWallBuff.removeWindwall(player);
        conjuringForceBuff.removeConjuringForceBuff(player);
        shadowCrowsDebuff.removeCrowsDebuff(player);
        wildRoarBuff.removeBuff(player);
        haste.removeHaste(player);
    }



    //attacker, defender
    public double getTotalDamageMultipliers(LivingEntity attacker, LivingEntity defender){

        return 1 +
                shadowCrowsDebuff.getIncreasedDamageAmount(defender) +
                wildRoarBuff.getMultiplier(attacker);
    }

    public double getTotalDamageAddition(Player player){

        return 0 + conjuringForceBuff.getExtraDamageAmount(player);

    }

    public double getTotalRangeModifier(Player player) {

        return 0 + conjuringForceBuff.getRangeModifier(player);
    }

    public float getSpeedUp(Player player){
        return speedUp.getSpeedUpAmount(player);
    }
}
