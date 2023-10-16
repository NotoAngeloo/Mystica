package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.BuffsAndDebuffs.ConjuringForceBuff;
import me.angeloo.mystica.Components.BuffsAndDebuffs.SpeedUp;
import me.angeloo.mystica.Components.BuffsAndDebuffs.WindWallBuff;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;


public class BuffAndDebuffManager {

    private final SpeedUp speedUp;
    private final WindWallBuff windWallBuff;
    private final ConjuringForceBuff conjuringForceBuff;

    public BuffAndDebuffManager(Mystica main){
        speedUp = new SpeedUp();
        windWallBuff = new WindWallBuff(main);
        conjuringForceBuff = new ConjuringForceBuff();
    }

    public WindWallBuff getWindWallBuff() {
        return windWallBuff;
    }

    public ConjuringForceBuff getConjuringForceBuff() {
        return conjuringForceBuff;
    }

    public SpeedUp getSpeedUp(){return speedUp;}

    public double getTotalDamageMultipliers(Player player){

        return 1;
    }

    public double getTotalDamageAddition(Player player){

        double total = 0 +
                conjuringForceBuff.getExtraDamageAmount(player);
        //add more later

        return total;

    }

    public double getTotalRangeModifier(Player player){

        double total = 0 +
                conjuringForceBuff.getRangeModifier(player);

        return total;
    }

    public float getSpeedUp(Player player){
        return speedUp.getSpeedUpAmount(player);
    }
}
