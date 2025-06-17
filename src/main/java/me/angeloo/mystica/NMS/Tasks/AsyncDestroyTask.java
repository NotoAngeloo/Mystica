package me.angeloo.mystica.NMS.Tasks;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.NMS.Common.PacketInterface;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

public class AsyncDestroyTask extends BukkitRunnable {

    public static AsyncDestroyTask createDestroyingTask(@Nullable Player player, Entity entity){
        return new AsyncDestroyTask(player, entity);
    }

    private final Player player;
    private final Entity entity;

    public AsyncDestroyTask(@Nullable Player player, Entity entity){
        this.player = player;
        this.entity = entity;

        this.runTaskLaterAsynchronously(Mystica.getPlugin(), 23);
    }

    @Override
    public void run(){

        if(player != null && player.isOnline()){
            Mystica.getPlugin().getPacketInterface().destroyEntity(player, entity.getEntityId());
        }

        Integer id = getTaskId();
        Mystica.TASKS_ID.remove(id);
    }
}
