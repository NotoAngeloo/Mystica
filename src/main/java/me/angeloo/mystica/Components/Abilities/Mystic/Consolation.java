package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Mystica;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Consolation {

    private final Mystica main;

    private final Map<UUID, BukkitTask> taskMap = new HashMap<>();

    private final Map<Player, List<Player>> affected = new HashMap<>();

    public Consolation(Mystica main){
        this.main = main;
    }

    public void apply(Player caster, Player target){

        List<Player> targets = getTargets(caster);

        targets.add(target);

        affected.put(caster, targets);
        startTask(caster);
    }

    public List<Player> getTargets(Player player){
        return affected.getOrDefault(player, new ArrayList<>());
    }

    public void removeTargets(Player player){
        affected.remove(player);
        taskMap.get(player.getUniqueId()).cancel();
        taskMap.remove(player.getUniqueId());
    }

    private void startTask(Player player){

        if(taskMap.containsKey(player.getUniqueId())){
            taskMap.get(player.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                List<Player> targets = getTargets(player);

                for(Player target : targets){

                    if(player.getWorld() == target.getWorld()){

                        for(double i=2; i<5;i+=.3){
                            player.spawnParticle(Particle.WAX_OFF, target.getLocation().add(0,i,0), 1,0, 0, 0, 0);
                        }

                    }

                }



            }
        }.runTaskTimer(main, 0, 20);

        taskMap.put(player.getUniqueId(), task);
    }

}
