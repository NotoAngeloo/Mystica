package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WindWallBuff {

    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;

    private final Map<UUID, Double> windWallMap = new HashMap<>();
    private final Map<UUID, Double> overflowDamage = new HashMap<>();

    public WindWallBuff(Mystica main, BuffAndDebuffManager manager){
        profileManager = main.getProfileManager();
        buffAndDebuffManager = manager;
    }


    public void createAWindWall(LivingEntity entity){

        double hp = profileManager.getAnyProfile(entity).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(entity);

        hp = hp/4;

        windWallMap.put(entity.getUniqueId(), hp);
    }

    public boolean getIfWindWallActive(LivingEntity entity){
        return windWallMap.containsKey(entity.getUniqueId());
    }

    public double calculateHowMuchDamageIsReflected(LivingEntity owner, double damage){

        double windwallCurrentHp = getWallHealth(owner);
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

    public double getWallHealth(LivingEntity entity){
        return windWallMap.getOrDefault(entity.getUniqueId(), 0.0);
    }

}
