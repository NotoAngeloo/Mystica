package me.angeloo.mystica.Components.BuffsAndDebuffs;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PassThrough {

    private final Map<Player, Player> passingToThisPlayer = new HashMap<>();

    public PassThrough(){

    }

    public void applyPassThrough(Player player, Player targetPlayer){
        passingToThisPlayer.put(targetPlayer, player);
    }

    public void removePassThrough(Player player){
        passingToThisPlayer.remove(player);
    }

    public Player getPassingToPlayer(Player player){
        return passingToThisPlayer.get(player);
    }

    public boolean getIfPassingToPlayer(Player player){
        return passingToThisPlayer.containsKey(player);
    }

}
