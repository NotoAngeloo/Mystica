package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pulled {

    private final Map<UUID, Boolean> knockedUp = new HashMap<>();

    public Pulled(){
    }

    public void applyPull(LivingEntity entity){
        knockedUp.put(entity.getUniqueId(), true);

    }

    public void removePull(LivingEntity entity){
        knockedUp.remove(entity.getUniqueId());
    }

    public boolean getIfPulled(LivingEntity entity){
        return knockedUp.getOrDefault(entity.getUniqueId(), false);
    }

}
