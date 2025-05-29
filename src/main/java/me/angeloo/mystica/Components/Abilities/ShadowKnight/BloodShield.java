package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Components.Abilities.ShadowKnightAbilities;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BloodShield {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Energy energy;

    private final Map<UUID, Integer> shieldTime = new HashMap<>();

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public BloodShield(Mystica main, AbilityManager manager, ShadowKnightAbilities shadowKnightAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        energy = shadowKnightAbilities.getEnergy();
    }

    public void use(LivingEntity caster){

        if(shieldTimeActive(caster)){
            return;
        }

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }


        energy.subTractEnergyFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 50);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(caster instanceof Player){
                    Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent((Player) caster));
                }


            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(caster);
        double currentHealth = profileManager.getAnyProfile(caster).getCurrentHealth();
        double missing = maxHealth-currentHealth;

        changeResourceHandler.addHealthToEntity(caster, missing * .5, caster);

        double shield = profileManager.getAnyProfile(caster).getCurrentHealth();
        buffAndDebuffManager.getGenericShield().applyOrAddShield(caster, shield);

        shieldTime.put(caster.getUniqueId(), 10);

        new BukkitRunnable(){
            @Override
            public void run(){

                if(buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(caster)==0){
                    removeShieldTime(caster);
                    this.cancel();
                    return;
                }

                if(!shieldTimeActive(caster)){
                    removeShieldTime(caster);
                    this.cancel();
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, shield);
                    return;
                }

                int duration = shieldTime.get(caster.getUniqueId());

                duration--;

                shieldTime.put(caster.getUniqueId(), duration);

            }
        }.runTaskTimer(main, 0, 20);
    }

    public void removeShieldTime(LivingEntity caster){
        shieldTime.remove(caster.getUniqueId());
    }

    public boolean shieldTimeActive(LivingEntity caster){
        return shieldTime.getOrDefault(caster.getUniqueId(), 0) > 0;
    }

    public void increaseDuration(LivingEntity caster){

        if(!shieldTime.containsKey(caster.getUniqueId())){
            return;
        }

        int duration = shieldTime.get(caster.getUniqueId());

        duration += 3;

        shieldTime.put(caster.getUniqueId(), duration);
    }

    public int getCost(){
        return 50;
    }

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }


        return energy.getCurrentEnergy(caster) >= getCost();
    }

    public int returnWhichItem(Player player){

        if(energy.getCurrentEnergy(player)<getCost()){
            return 6;
        }

        return 0;
    }

}
