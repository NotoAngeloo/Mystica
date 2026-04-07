package me.angeloo.mystica.NMS.Tasks;

import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncHologramTask extends BukkitRunnable {

    public static AsyncHologramTask createHologramTask(Player player, Entity armorStand, Location inititialLocation){
        return new AsyncHologramTask(player, armorStand, inititialLocation);
    }

    private final int DURATION;
    private final double INITIAL_SPEED, ACCELERATION;
    private int tick = 0;

    private final Player player;
    private final Entity armorStand;
    private final Location initialLocation;

    public AsyncHologramTask(Player player, Entity armorStand, Location initialLocation){
        this.DURATION = 23;
        this.INITIAL_SPEED = 1.15d;
        this.ACCELERATION = 4;

        this.player = player;
        this.armorStand = armorStand;
        this.initialLocation = initialLocation;

        this.runTaskTimerAsynchronously(Mystica.getPlugin(), 0,1);
    }

    @Override
    public void run(){
        if(!player.isOnline()){
            this.cancel();
            return;
        }

        double time = tick/20d;
        double dy = INITIAL_SPEED * time - 0.5d * ACCELERATION * Math.pow(time, 2);

        Mystica.getPlugin().getPacketInterface().relEntityMove(player, armorStand.getEntityId(), initialLocation.getY(), dy, false);

        if(tick >= DURATION){
            Mystica.getPlugin().getPacketInterface().destroyEntity(player, armorStand.getEntityId());
            this.cancel();
        }

        tick++;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException{
        Integer id = this.getTaskId();
        Mystica.TASKS_ID.remove(id);

        super.cancel();
    }

}
