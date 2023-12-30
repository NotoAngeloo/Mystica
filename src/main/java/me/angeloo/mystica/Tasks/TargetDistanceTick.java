package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.Managers.TargetManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TargetDistanceTick extends BukkitRunnable {

    private final TargetManager targetManager;

    public TargetDistanceTick(Mystica main){
        targetManager = main.getTargetManager();
    }

    @Override
    public void run(){

        for(Player player : Bukkit.getOnlinePlayers()){

            LivingEntity playerTarget = targetManager.getPlayerTarget(player);

            if(playerTarget != null){

                if(player.isDead()){
                    targetManager.setPlayerTarget(player, null);
                    continue;
                }

                World playerWorld = player.getWorld();
                World targetWorld = playerTarget.getWorld();

                if(playerWorld != targetWorld){
                    targetManager.removeAllBars(player);
                    return;
                }

                double distance = playerTarget.getLocation().distance(player.getLocation());
                if(distance > 35){
                    targetManager.removeAllBars(player);
                    targetManager.setPlayerTarget(player, null);
                    return;
                }

                if(playerTarget.isDead()){
                    targetManager.removeAllBars(player);
                    targetManager.setPlayerTarget(player, null);
                    return;
                }
            }




        }
    }
}