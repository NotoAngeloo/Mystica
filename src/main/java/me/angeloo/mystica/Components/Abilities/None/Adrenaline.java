package me.angeloo.mystica.Components.Abilities.None;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Adrenaline {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> buffActiveMap = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Adrenaline(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(getCooldown(player) > 0){
            return;
        }

        if(getIfBuffTime(player) > 0){
            return;
        }

        double cost = 10;

        if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
            return;
        }

        double healthPercent =  Math.round((profileManager.getAnyProfile(player).getCurrentHealth() / (double) profileManager.getAnyProfile(player).getTotalHealth()) * 100);

        if(healthPercent > 50){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 25);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 8);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private void execute(Player player){

        buffActiveMap.put(player.getUniqueId(), 11);
        new BukkitRunnable(){
            @Override
            public void run(){

                Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));

                if(buffActiveMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int left = buffActiveMap.get(player.getUniqueId()) - 1;

                buffActiveMap.put(player.getUniqueId(), left);

            }
        }.runTaskTimer(main, 0,20);



    }

    public int getIfBuffTime(Player player){
        return buffActiveMap.getOrDefault(player.getUniqueId(), 0);
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;

    }

    public int returnWhichItem(Player player) {
        double healthPercent =  Math.round((profileManager.getAnyProfile(player).getCurrentHealth() / (double) profileManager.getAnyProfile(player).getTotalHealth()) * 100);

        if(healthPercent > 50){
            return 1;
        }

        return 0;
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
