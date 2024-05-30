package me.angeloo.mystica.Components.Abilities.None;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Block {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Block(Mystica main, AbilityManager manager){
        this.main = main;
        this.abilityManager = manager;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    private final double cost = 20;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        if(getCooldown(caster) > 0){
            return;
        }

        if(profileManager.getAnyProfile(caster).getCurrentMana()<cost){
            return;
        }

        changeResourceHandler.subTractManaFromEntity(caster, cost);

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 16);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 7);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 7);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        abilityManager.setCasting(caster, true);
        int castTime = 40;

        buffAndDebuffManager.getBlocking().applyBlocking(caster, castTime);
        if(caster instanceof Player){
            ((Player)caster).setWalkSpeed(.03f);
        }

        new BukkitRunnable(){

            int ran = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.setCasting(caster, false);
                        ((Player) caster).setWalkSpeed(.2f);
                        buffAndDebuffManager.getBlocking().removeBlocking(caster);
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.2f);
                    }
                    buffAndDebuffManager.getBlocking().removeBlocking(caster);
                    return;
                }


                double percent = ((double) ran / castTime) * 100;
                abilityManager.setCastBar(caster, percent);

                if(ran >= castTime){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.2f);
                    }
                    buffAndDebuffManager.getBlocking().removeBlocking(caster);
                }


                ran++;
            }


        }.runTaskTimer(main, 0, 1);

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

}
