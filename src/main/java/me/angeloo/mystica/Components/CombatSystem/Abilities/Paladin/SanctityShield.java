package me.angeloo.mystica.Components.CombatSystem.Abilities.Paladin;

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

public class SanctityShield {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SanctityShield(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(LivingEntity caster){


        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), getSkillCooldown());
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getPlayerCooldown(caster) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getPlayerCooldown(caster) - 1;
                cooldown = cooldown - statusEffectManager.getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(caster instanceof Player){
                    Bukkit.getScheduler().runTask(main,()->{
                        Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent((Player) caster));
                    });
                }


            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){


        double healAmount = getHealAmount(caster);
        double shield = getShieldAmount(caster);

        statusEffectManager.applyEffect(caster, new GenericShield(), null, shield);


        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(!statusEffectManager.hasShield(caster)){
                    this.cancel();
                    return;
                }

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        statusEffectManager.reduceShield(caster, shield);
                        return;
                    }
                }

                if(profileManager.getAnyProfile(caster).getIfDead()){
                    this.cancel();
                    statusEffectManager.reduceShield(caster, shield);
                    return;
                }

                changeResourceHandler.addHealthToEntity(caster, healAmount, caster);

                if(count>=5){
                    this.cancel();
                    statusEffectManager.reduceShield(caster, shield);
                }
                count++;
            }
        }.runTaskTimer(main, 0, 20);

    }

    public double getShieldAmount(LivingEntity caster){
        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster);
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return  (level + 10 / maxHealth) * 100;
    }

    public double getHealAmount(LivingEntity caster){
        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster);
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return maxHealth * ((level + 5) /100);
    }


    public int getPlayerCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int getSkillCooldown(){
        return 12;
    }


    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        return getPlayerCooldown(caster) <= 0;
    }


}
