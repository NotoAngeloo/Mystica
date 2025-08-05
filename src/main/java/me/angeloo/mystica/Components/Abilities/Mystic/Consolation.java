package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Consolation {

    private final Mystica main;

    private final ProfileManager profileManager;

    private final Map<UUID, BukkitTask> taskMap = new HashMap<>();

    private final Map<LivingEntity, List<LivingEntity>> affected = new HashMap<>();

    public Consolation(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
    }

    public void apply(LivingEntity caster, LivingEntity target){

        List<LivingEntity> targets = getTargets(caster);

        targets.add(target);

        affected.put(caster, targets);
        startTask(caster);
    }

    public List<LivingEntity> getTargets(LivingEntity caster){
        List<LivingEntity> targets = new ArrayList<>(affected.getOrDefault(caster, new ArrayList<>()));
        List<LivingEntity> toRemove = new ArrayList<>();

        for(LivingEntity target : targets){
            if(profileManager.getAnyProfile(target).getIfDead()){
                toRemove.add(target);
            }
        }
        targets.removeAll(toRemove);
        affected.put(caster, targets);
        return targets;
    }

    public void removeTargets(LivingEntity caster){
        affected.remove(caster);

        if(taskMap.containsKey(caster.getUniqueId())){
            taskMap.get(caster.getUniqueId()).cancel();
            taskMap.remove(caster.getUniqueId());
        }


    }

    private void startTask(LivingEntity caster){

        if(!(caster instanceof Player)){
            return;
        }

        if(taskMap.containsKey(caster.getUniqueId())){
            taskMap.get(caster.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                List<LivingEntity> targets = getTargets(caster);

                if(targets.isEmpty()){
                    this.cancel();
                    return;
                }

                for(LivingEntity target : targets){

                    if(caster.getWorld() == target.getWorld()){

                        for(double i=2; i<5;i+=.3){

                            ((Player) caster).spawnParticle(Particle.WAX_OFF, target.getLocation().add(0, i, 0), 1, 0, 0, 0, 0);


                        }

                    }

                }



            }
        }.runTaskTimer(main, 0, 20);

        taskMap.put(caster.getUniqueId(), task);
    }

}
