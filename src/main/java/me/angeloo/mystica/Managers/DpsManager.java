package me.angeloo.mystica.Managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DpsManager {

    private final Map<UUID, Double> playerDamageDealt = new HashMap<>();
    private final Map<UUID, Long> damageStarted = new HashMap<>();

    public DpsManager(){
    }

    public void addDamageToDamageDealt(Player player, Double damage){
        if(!playerDamageDealt.containsKey(player.getUniqueId())){
            playerDamageDealt.put(player.getUniqueId(), 0.0);
        }

        double oldDamage = playerDamageDealt.get(player.getUniqueId());
        double newDamage = oldDamage + damage;
        playerDamageDealt.put(player.getUniqueId(), newDamage);

        if(!damageStarted.containsKey(player.getUniqueId())){
            startDpsTimer(player);
        }

    }

    private void startDpsTimer(Player player){
        long currentTime = System.currentTimeMillis() / 1000;
        damageStarted.put(player.getUniqueId(), currentTime - 1);
    }

    public void removeDps(Player player){
        playerDamageDealt.remove(player.getUniqueId());
        damageStarted.remove(player.getUniqueId());
    }

    public double getRawDps(Player player){

        double damage = playerDamageDealt.getOrDefault(player.getUniqueId(), 0.0);

        long currentTime = System.currentTimeMillis()/1000;

        if(!damageStarted.containsKey(player.getUniqueId())){
            startDpsTimer(player);
        }

        double seconds = currentTime - damageStarted.get(player.getUniqueId());

        //Bukkit.getLogger().info(damage + " " + seconds);

        //Bukkit.getLogger().info(String.valueOf(damage/seconds));

        return damage/seconds;
    }

    public int getRoundedDps(Player player){

        double damage = playerDamageDealt.getOrDefault(player.getUniqueId(), 0.0);

        long currentTime = System.currentTimeMillis()/1000;

        if(!damageStarted.containsKey(player.getUniqueId())){
            startDpsTimer(player);
        }

        double seconds = currentTime - damageStarted.get(player.getUniqueId());

        return (int) Math.round(damage/seconds);
    }
}
