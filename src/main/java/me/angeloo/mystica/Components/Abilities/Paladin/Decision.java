package me.angeloo.mystica.Components.Abilities.Paladin;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Decision {

    private final Map<UUID, Boolean> decisionMap = new HashMap<>();

    public Decision(){

    }

    public void applyDecision(Player player){
        decisionMap.put(player.getUniqueId(), true);
    }

    public boolean getDecision(Player player){
        return decisionMap.getOrDefault(player.getUniqueId(), false);
    }

    public void removeDecision(Player player){
        decisionMap.remove(player.getUniqueId());
    }


}
