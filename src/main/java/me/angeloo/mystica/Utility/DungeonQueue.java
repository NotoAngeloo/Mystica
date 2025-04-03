package me.angeloo.mystica.Utility;

import org.bukkit.entity.Player;

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

    public void joinHealQueue(Player player){
        healQueue.add(player);
    }

    public void removeHealQueue(Player player){
        healQueue.remove(player);
    }

    public void joinDamageQueue(Player player){
        damageQueue.add(player);
    }

    public void removeDamageQueue(Player player){
        damageQueue.remove(player);
    }

    public boolean hasEnoughTanks(){
        return !tankQueue.isEmpty();
    }

    public boolean hasEnoughHeal(){
        return !healQueue.isEmpty();
    }

    public boolean hasEnoughDamage(){
        return damageQueue.size() >= 3;
    }

    public Player getFirstTank(){
        if(!tankQueue.isEmpty()){
            return tankQueue.get(0);
        }
        return null;
    }

    public Player getFirstHeal(){
        if(!healQueue.isEmpty()){
            return healQueue.get(0);
        }
        return null;
    }

    public List<Player> getDamagePlayers(){
        return damageQueue;
    }
    public List<Player> getHealPlayers(){
        return healQueue;
    }

    public List<Player> getTankPlayers(){
        return tankQueue;
    }

}
