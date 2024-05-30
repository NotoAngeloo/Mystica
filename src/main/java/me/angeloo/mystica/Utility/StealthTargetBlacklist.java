package me.angeloo.mystica.Utility;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StealthTargetBlacklist {

    private final Map<LivingEntity, Boolean> blacklist = new HashMap<>();

    public StealthTargetBlacklist(){

    }

    public void add(LivingEntity caster){
        blacklist.put(caster, true);
    }

    public void remove(LivingEntity caster){
        blacklist.remove(caster);
    }

    public boolean get(LivingEntity caster){
        return blacklist.getOrDefault(caster, false);
    }
}
