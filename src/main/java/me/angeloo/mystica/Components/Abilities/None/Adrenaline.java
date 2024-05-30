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
import org.bukkit.entity.LivingEntity;
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

    private final double cost = 10;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }


        changeResourceHandler.subTractManaFromEntity(caster, cost);

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 25);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 8);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        buffActiveMap.put(caster.getUniqueId(), 11);
        new BukkitRunnable(){
            @Override
            public void run(){

                if (caster instanceof Player) {
                    Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) caster));
                }


                if(buffActiveMap.get(caster.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int left = buffActiveMap.get(caster.getUniqueId()) - 1;

                buffActiveMap.put(caster.getUniqueId(), left);

            }
        }.runTaskTimer(main, 0,20);



    }

    public int getIfBuffTime(LivingEntity caster){
        return buffActiveMap.getOrDefault(caster.getUniqueId(), 0);
    }

    public int getCooldown(LivingEntity caster){

        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

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

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }

        if(getIfBuffTime(caster) > 0){
            return false;
        }



        if(profileManager.getAnyProfile(caster).getCurrentMana()<cost){
            return false;
        }

        double healthPercent =  Math.round((profileManager.getAnyProfile(caster).getCurrentHealth() / (double) profileManager.getAnyProfile(caster).getTotalHealth()) * 100);

        if(healthPercent > 50){
            return false;
        }

        return true;
    }

}
