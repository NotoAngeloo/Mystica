package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
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

    public void addComboPoint(LivingEntity caster){

        int current = getComboPoints(caster);

        int max = 5;

        if(profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("duelist")){
            max = 6;
        }

        if(current>=max){
            return;
        }

        current++;

        comboPoints.put(caster.getUniqueId(), current);

        if(caster instanceof Player){
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) caster));
        }

        cooldownDisplayer.displayCooldown(caster, 3);
        cooldownDisplayer.displayCooldown(caster, 4);
    }

    public int removeAnAmountOfPoints(LivingEntity caster, int amount){

        int current = getComboPoints(caster);

        int newAmount = current - amount;

        comboPoints.put(caster.getUniqueId(), newAmount);


        if(caster instanceof Player){
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) caster));
        }


        cooldownDisplayer.displayCooldown(caster, 3);
        cooldownDisplayer.displayCooldown(caster, 4);

        return current;
    }

    public int getComboPoints(LivingEntity caster){
        return comboPoints.getOrDefault(caster.getUniqueId(), 0);
    }



}
