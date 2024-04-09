package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Combo {

    private final ProfileManager profileManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> comboPoints = new HashMap<>();

    public Combo(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void addComboPoint(Player player){

        int current = getComboPoints(player);

        int max = 5;

        if(profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("duelist")){
            max = 6;
        }

        if(current>=max){
            return;
        }

        current++;

        comboPoints.put(player.getUniqueId(), current);
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        cooldownDisplayer.displayCooldown(player, 3);
        cooldownDisplayer.displayCooldown(player, 4);
    }

    public int removeAnAmountOfPoints(Player player, int amount){

        int current = getComboPoints(player);

        int newAmount = current - amount;

        comboPoints.put(player.getUniqueId(), newAmount);



        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        cooldownDisplayer.displayCooldown(player, 3);
        cooldownDisplayer.displayCooldown(player, 4);

        return current;
    }

    public int getComboPoints(Player player){
        return comboPoints.getOrDefault(player.getUniqueId(), 0);
    }

}
