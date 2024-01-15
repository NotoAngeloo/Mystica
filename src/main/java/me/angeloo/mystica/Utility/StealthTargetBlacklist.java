package me.angeloo.mystica.Utility;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StealthTargetBlacklist {

    private final Map<Player, Boolean> blacklist = new HashMap<>();

    public StealthTargetBlacklist(){

    }

    public void add(Player player){
        blacklist.put(player, true);
    }

    public void remove(Player player){
        blacklist.remove(player);
    }

    public boolean get(Player player){
        return blacklist.getOrDefault(player, false);
    }
}
