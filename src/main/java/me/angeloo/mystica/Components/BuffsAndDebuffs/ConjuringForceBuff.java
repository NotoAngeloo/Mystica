package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ConjuringForceBuff {

    private final Map<Player, Boolean> hasConjForceBuffMap = new HashMap<>();
    private final Map<Player, Double> extraDamageMap = new HashMap<>();

    public ConjuringForceBuff(){
    }

    public void applyConjuringForceBuff(Player player, double extraDamageAmount){
        hasConjForceBuffMap.put(player, true);

        double currentExtraDamage = getExtraDamageAmount(player);

        if(extraDamageAmount > currentExtraDamage){
            extraDamageMap.put(player, extraDamageAmount);
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        }

    }

    public void removeConjuringForceBuff(Player player){
        hasConjForceBuffMap.remove(player);
        extraDamageMap.remove(player);
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
    }

    public boolean getIfConjForceBuff(Player player){
        return hasConjForceBuffMap.getOrDefault(player, false);
    }

    public double getExtraDamageAmount(Player player){
        return extraDamageMap.getOrDefault(player, 0.0);
    }

    public double getRangeModifier(Player player){

        if(getIfConjForceBuff(player)){
            return 10;
        }

        return 0;
    }

}
