package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WindWallBuff {

    private final ProfileManager profileManager;

    private final Map<UUID, Double> windWallMap = new HashMap<>();
    private final Map<UUID, Double> overflowDamage = new HashMap<>();

    public WindWallBuff(Mystica main){
        profileManager = main.getProfileManager();
    }


    public void createAWindWall(LivingEntity entity){

        double hp = profileManager.getAnyProfile(entity).getTotalHealth();

        hp = hp/4;

        windWallMap.put(entity.getUniqueId(), hp);
    }

    public boolean getIfWindWallActive(LivingEntity entity){
        return windWallMap.containsKey(entity.getUniqueId());
    }

    public double calculateHowMuchDamageIsReflected(LivingEntity owner, double damage){

        double windwallCurrentHp = windWallMap.get(owner.getUniqueId());
        double newHp = windwallCurrentHp - damage;
        windWallMap.put(owner.getUniqueId(), newHp);

        if(newHp <=0){

            double overflow = Math.abs(0 - newHp);
            overflowDamage.put(owner.getUniqueId(), overflow);
            removeWindwall(owner);
            return windwallCurrentHp;
        }

        return damage;
    }

    public boolean getIfOverflow(LivingEntity owner){
        return overflowDamage.containsKey(owner.getUniqueId());
    }

    public double getOverflowAmount(LivingEntity owner){

        double overflow = overflowDamage.get(owner.getUniqueId());

        overflowDamage.remove(owner.getUniqueId());

        return overflow;
    }

    public void removeWindwall(LivingEntity owner){
        windWallMap.remove(owner.getUniqueId());
    }

}
