package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.GenericDamageReduction;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GladiatorHeart extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    public GladiatorHeart(Mystica main, AbilityManager manager){
        super("gladiator_heart");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = manager.getCooldownManager();
    }

    private final int baseCooldown = 12;
    private final int baseShield = 10;

    @Override
    public void use(LivingEntity caster){


        if(!usable(caster)){
            return;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));

    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        double shield = getShieldAmount(caster);

        statusEffectManager.applyEffect(caster, new GenericShield(), null, shield);
        statusEffectManager.applyEffect(caster, new GenericDamageReduction(), -1, 0.8);
        //.8 is 20% damage reduction

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(!statusEffectManager.hasShield(caster)){
                    statusEffectManager.removeEffect(caster, "damage_reduction");
                    this.cancel();
                    return;
                }

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        statusEffectManager.reduceShield(caster, shield);
                        statusEffectManager.removeEffect(caster, "damage_reduction");
                        return;
                    }
                }

                if(profileManager.getAnyProfile(caster).getIfDead()){
                    this.cancel();
                    statusEffectManager.reduceShield(caster, shield);
                    statusEffectManager.removeEffect(caster, "damage_reduction");
                    return;
                }



                if(count>=5){
                    this.cancel();
                    statusEffectManager.reduceShield(caster, shield);
                    statusEffectManager.removeEffect(caster, "damage_reduction");
                }
                count++;
            }
        }.runTaskTimer(main, 0, 20);

    }

    public double getShieldAmount(LivingEntity caster){
        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth()+ statusEffectManager.getHealthBuffAmount(caster);
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return  (level + baseShield / maxHealth) * 100;
    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), -1, statusEffectManager.getHastePercent(caster));
    }

}
