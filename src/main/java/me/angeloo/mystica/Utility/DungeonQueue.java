package me.angeloo.mystica.Utility;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DungeonQueue {

    private final List<Player> tankQueue;
    private final List<Player> damageQueue;
    private final List<Player> healQueue;

    public DungeonQueue(List<Player> tankQueue, List<Player> healQueue, List<Player> damageQueue){
        this.tankQueue = tankQueue;
        this.healQueue = healQueue;
        this.damageQueue = damageQueue;
    }

    public void joinTankQueue(Player player){
        tankQueue.add(player);
    }

    public void removeTankQueue(Player player){
        tankQueue.remove(player);
    }

    public void addHealQueue(Player player){
        healQueue.add(player);
    }

    public void removeHealQueue(Player player){
        healQueue.remove(player);
    }

    public void addDamageQueue(Player player){
        damageQueue.add(player);
    }

    public void removeDamageQueue(Player player){
        damageQueue.remove(player);
    }

}
