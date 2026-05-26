package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin;

import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
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

    public void addComboPoint(LivingEntity caster){

        int current = getComboPoints(caster);

        int max = 4;

        if(profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.DUELIST)){
            max = 5;
        }

        if(current>=max){
            return;
        }

        current++;

        comboPoints.put(caster.getUniqueId(), current);


        //cooldownDisplayer.displayCooldown(caster, 3);
        //cooldownDisplayer.displayCooldown(caster, 4);

        if(caster instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Resource));
        }
    }

    public int removeAnAmountOfPoints(LivingEntity caster, int amount){

        int current = getComboPoints(caster);

        int newAmount = current - amount;

        comboPoints.put(caster.getUniqueId(), newAmount);


        //cooldownDisplayer.displayCooldown(caster, 3);
        //cooldownDisplayer.displayCooldown(caster, 4);

        if(caster instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Resource));
        }

        return current;
    }

    public int getComboPoints(LivingEntity caster){
        return comboPoints.getOrDefault(caster.getUniqueId(), 0);
    }



}
