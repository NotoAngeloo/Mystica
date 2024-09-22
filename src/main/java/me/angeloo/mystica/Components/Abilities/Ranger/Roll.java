package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
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
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Roll(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }


        combatManager.startCombatTimer(caster);

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
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 8);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        Location start = caster.getLocation();


        Vector direction = start.getDirection().normalize();

        double shieldAmount = (profileManager.getAnyProfile(caster).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(caster)) / 4;

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


        buffAndDebuffManager.getGenericShield().applyOrAddShield(caster, shieldAmount);

        double forwardPower = 3;
        double jumpPower = .2;
        Vector dashVector = direction.multiply(forwardPower).setY(jumpPower);
        caster.setVelocity(dashVector);

        //also give a shield and increase move speed
        if(caster instanceof Player){
            buffAndDebuffManager.getSpeedUp().applySpeedUp((Player) caster, .3f);
        }



        double finalShieldAmount = shieldAmount;
        new BukkitRunnable(){
            @Override
            public void run(){
                if(caster instanceof Player){
                    buffAndDebuffManager.getSpeedUp().removeSpeedUp((Player) caster);
                }

                buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(caster, finalShieldAmount);
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
