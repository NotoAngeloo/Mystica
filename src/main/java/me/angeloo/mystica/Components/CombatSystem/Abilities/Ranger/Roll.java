package me.angeloo.mystica.Components.CombatSystem.Abilities.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc.SpeedUp;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Roll {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Roll(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
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

        abilityReadyInMap.put(caster.getUniqueId(), 13);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - statusEffectManager.getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 8);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        Location start = caster.getLocation();

        Vector direction = start.getDirection().normalize();

        double shieldAmount = (profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster)) / 4;

        if(caster instanceof Player){
            if(((Player)caster).isSneaking()){
                direction.multiply(-1);
                shieldAmount*=2;
            }
        }

        if(profileManager.getAnyProfile(caster).fakePlayer()){
            direction.multiply(-1);
            shieldAmount*=2;
        }

        statusEffectManager.applyEffect(caster, new GenericShield(), null, shieldAmount);

        double forwardPower = 3;
        double jumpPower = .2;
        Vector dashVector = direction.multiply(forwardPower).setY(jumpPower);
        caster.setVelocity(dashVector);

        //also give a shield and increase move speed
        if(caster instanceof Player){
            statusEffectManager.applyEffect(caster, new SpeedUp(), null, 0.5);
        }



        double finalShieldAmount = shieldAmount;
        new BukkitRunnable(){
            @Override
            public void run(){
                if(caster instanceof Player){
                    statusEffectManager.removeEffect(caster, "speed_up");
                }

                statusEffectManager.reduceShield(caster, finalShieldAmount);
            }
        }.runTaskLater(main, 100);

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
        return getCooldown(caster) <= 0;
    }

}
