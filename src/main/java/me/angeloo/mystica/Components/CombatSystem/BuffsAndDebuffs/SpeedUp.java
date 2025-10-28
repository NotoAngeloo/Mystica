package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Utility.Enums.BarType;
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

        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));

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
        player.setWalkSpeed(.3f);

        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));

    }

    public float getSpeedUpAmount(Player player){
        return speedUpAmount.getOrDefault(player, .3f);
    }

}
