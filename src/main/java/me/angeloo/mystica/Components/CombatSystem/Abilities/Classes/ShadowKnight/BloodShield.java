package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnightAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
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
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    private final Energy energy;

    private final Map<UUID, Integer> shieldTime = new HashMap<>();


    public BloodShield(Mystica main, ShadowKnightAbilities shadowKnightAbilities, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        energy = shadowKnightAbilities.getEnergy();
        cooldownManager = manager.getCooldownManager();
    }

    private final int abilityNumber = -1;
    private final int baseCooldown = 50;
    private final int cost = 50;

    public void use(LivingEntity caster){

        if(shieldTimeActive(caster)){
            return;
        }


        if(!usable(caster)){
            return;
        }


        energy.subTractEnergyFromEntity(caster, cost);

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), abilityNumber, (long) (baseCooldown * 1000));

    }

    private void execute(LivingEntity caster){

        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster);
        double currentHealth = profileManager.getAnyProfile(caster).getCurrentHealth();
        double missing = maxHealth-currentHealth;

        changeResourceHandler.addHealthToEntity(caster, missing * .5, caster);

        double shield = profileManager.getAnyProfile(caster).getCurrentHealth();
        statusEffectManager.applyEffect(caster, new GenericShield(), null, shield);

        shieldTime.put(caster.getUniqueId(), 10);

        new BukkitRunnable(){
            @Override
            public void run(){
                

                if(statusEffectManager.hasShield(caster)){
                    removeShieldTime(caster);
                    this.cancel();
                    return;
                }

                if(!shieldTimeActive(caster)){
                    removeShieldTime(caster);
                    this.cancel();
                    statusEffectManager.reduceShield(caster, shield);
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



    public boolean usable(LivingEntity caster){
        if(energy.getCurrentEnergy(caster)<cost){
            return false;
        }


        return cooldownManager.isReady(caster.getUniqueId(), abilityNumber, statusEffectManager.getHastePercent(caster));
    }

    /*public int returnWhichItem(Player player){

        if(energy.getCurrentEnergy(player)<getCost()){
            return 6;
        }

        return 0;
    }*/

}
