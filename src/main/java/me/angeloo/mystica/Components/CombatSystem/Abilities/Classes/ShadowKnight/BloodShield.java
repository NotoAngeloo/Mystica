package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BloodShield extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    private final Energy energy;

    private final Map<UUID, Integer> shieldTime = new HashMap<>();


    public BloodShield(Mystica main, AbilityManager manager){
        super("blood_shield");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        energy = manager.getEnergy();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 50;
    private final int cost = 50;

    @Override
    public boolean use(LivingEntity caster){

        if(shieldTimeActive(caster)){
            return false;
        }


        if(!usable(caster)){
            return false;
        }


        energy.subTractEnergyFromEntity(caster, cost);

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster);
        double currentHealth = profileManager.getAnyProfile(caster).getCurrentHealth();
        double missing = maxHealth-currentHealth;

        changeResourceHandler.addHealthToEntity(caster, missing * .5, caster, false);

        double shield = profileManager.getAnyProfile(caster).getCurrentHealth();
        statusEffectManager.applyEffect(caster, new GenericShield(), null, shield, caster);

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

    @Override
    public void onExternalTrigger(LivingEntity caster){
        increaseDuration(caster);
    }

    private void increaseDuration(LivingEntity caster){

        if(!shieldTime.containsKey(caster.getUniqueId())){
            return;
        }

        int duration = shieldTime.get(caster.getUniqueId());

        duration += 3;

        shieldTime.put(caster.getUniqueId(), duration);
    }


    @Override
    public boolean usable(LivingEntity caster){
        if(energy.getCurrentEnergy(caster)<cost){
            return false;
        }


        return cooldownManager.isReady(caster.getUniqueId(), -1, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {

        if(energy.getCurrentEnergy(entity)<cost){
            return "\ue402";
        }

        return "\ue401";
    }
}
