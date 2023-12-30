package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SpeedUp {

    private final Map<Player, Boolean> hasSpeedUp = new HashMap<>();
    private final Map<Player, Float> speedUpAmount = new HashMap<>();

    public SpeedUp(){

    }

    public void applySpeedUp(Player player, float amount){
        hasSpeedUp.put(player, true);

        double current = getSpeedUpAmount(player);

        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player, false));

        if(amount > current){
            speedUpAmount.put(player, amount);
            player.setWalkSpeed(amount);
        }

    }

    public boolean getIfSpeedUp(Player player){
        return hasSpeedUp.getOrDefault(player, false);
    }

    public void removeSpeedUp(Player player){
        hasSpeedUp.remove(player);
        speedUpAmount.remove(player);
        player.setWalkSpeed(.2f);

        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player, false));

    }

    public float getSpeedUpAmount(Player player){
        return speedUpAmount.getOrDefault(player, .2f);
    }

}
