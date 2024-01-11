package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Combo {

    private final ProfileManager profileManager;

    private final Map<UUID, Integer> comboPoints = new HashMap<>();

    public Combo(Mystica main){
        profileManager = main.getProfileManager();
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
    }

    public int removeAnAmountOfPoints(Player player, int amount){

        int current = getComboPoints(player);

        int newAmount = current - amount;

        comboPoints.put(player.getUniqueId(), newAmount);

        return current;
    }

    public int getComboPoints(Player player){
        return comboPoints.getOrDefault(player.getUniqueId(), 0);
    }

}
