package me.angeloo.mystica.Utility.Hud;

import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossWarnings {

    private final Mystica main;

    private final Map<UUID, String> warningMap = new HashMap<>();
    private final Map<UUID, BukkitTask> removalTaskMap = new HashMap<>();

    public BossWarnings(Mystica main){
        this.main = main;
    }

    public String getWarning(Player player){
        return warningMap.getOrDefault(player.getUniqueId(), " ");
    }

    public void setWarning(Player player, String string, int time){

        if(removalTaskMap.containsKey(player.getUniqueId())){
            removalTaskMap.get(player.getUniqueId()).cancel();
            removalTaskMap.remove(player.getUniqueId());
        }


        //make unicodes for each letter to make them small enough to not be annoying
        //for each letter, get from a pre saved map

        //remove after time
        warningMap.put(player.getUniqueId(), string);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                removeWarning(player);
                removalTaskMap.remove(player.getUniqueId());
            }

        }.runTaskLaterAsynchronously(main, time);

        removalTaskMap.put(player.getUniqueId(), task);

    }

    public void removeWarning(Player player){
        warningMap.remove(player.getUniqueId());
    }

}
