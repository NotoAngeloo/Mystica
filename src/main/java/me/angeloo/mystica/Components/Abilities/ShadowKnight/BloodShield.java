package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BloodShield {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> shieldTime = new HashMap<>();

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public BloodShield(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(shieldTimeActive(player)){
            return;
        }

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 30);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayUltimateCooldown(player);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        double maxHealth = profileManager.getAnyProfile(player).getTotalHealth();
        double currentHealth = profileManager.getAnyProfile(player).getCurrentHealth();
        double missing = maxHealth-currentHealth;

        changeResourceHandler.addHealthToEntity(player, missing * .5, player);

        double shield = profileManager.getAnyProfile(player).getCurrentHealth();
        buffAndDebuffManager.getGenericShield().applyOrAddShield(player, shield);

        shieldTime.put(player.getUniqueId(), 10);

        new BukkitRunnable(){
            @Override
            public void run(){

                if(buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(player)==0){
                    removeShieldTime(player);
                    this.cancel();
                    return;
                }

                if(!shieldTimeActive(player)){
                    removeShieldTime(player);
                    this.cancel();
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, shield);
                    return;
                }

                int duration = shieldTime.get(player.getUniqueId());

                duration--;

                shieldTime.put(player.getUniqueId(), duration);

            }
        }.runTaskTimer(main, 0, 20);
    }

    public void removeShieldTime(Player player){
        shieldTime.remove(player.getUniqueId());
    }

    public boolean shieldTimeActive(Player player){
        return shieldTime.getOrDefault(player.getUniqueId(), 0) > 0;
    }

    public void increaseDuration(Player player){

        if(!shieldTime.containsKey(player.getUniqueId())){
            return;
        }

        int duration = shieldTime.get(player.getUniqueId());

        duration += 3;

        shieldTime.put(player.getUniqueId(), duration);
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
