package me.angeloo.mystica.Components.CombatSystem.Abilities.Paladin;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Decision {

    private final Map<UUID, Boolean> decisionMap = new HashMap<>();

    public Decision(){

    }

    public void applyDecision(LivingEntity entity){
        if(entity instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
        }

        decisionMap.put(entity.getUniqueId(), true);
    }

    public boolean getDecision(LivingEntity entity){
        return decisionMap.getOrDefault(entity.getUniqueId(), false);
    }

    public void removeDecision(LivingEntity entity){
        if(entity instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
        }

        decisionMap.remove(entity.getUniqueId());
    }


}
