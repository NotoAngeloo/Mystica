package me.angeloo.mystica.Managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DpsManager {

    private final Map<UUID, Double> playerDps;
    private final Map<UUID, Integer> playerSecondsInCombat;
    private final Map<UUID, Double> playerDamageDealt;
    private final Map<UUID, Long> lastDamagedEnemy;

    public DpsManager(){
        playerDps = new HashMap<>();
        playerSecondsInCombat = new HashMap<>();
        playerDamageDealt = new HashMap<>();
        lastDamagedEnemy = new HashMap<>();
    }

    public void setPlayerDps(Player player){

        long currentTime = System.currentTimeMillis()/1000;

        if(!lastDamagedEnemy.containsKey(player.getUniqueId())){
            lastDamagedEnemy.put(player.getUniqueId(), currentTime);
        }

        //have a 10 second check to see when the last time damage was updated
        if(currentTime - lastDamagedEnemy.get(player.getUniqueId()) > 7){

            //Bukkit.getLogger().info("last damaged too long");

            removeDps(player);

            if(!playerDps.containsKey(player.getUniqueId())){
                playerDps.put(player.getUniqueId(), 0.0);
            }
            return;
        }

        if(!playerSecondsInCombat.containsKey(player.getUniqueId())){
            playerSecondsInCombat.put(player.getUniqueId(), 1);
        }

        if(!playerDamageDealt.containsKey(player.getUniqueId())){
            playerDamageDealt.put(player.getUniqueId(), 0.0);
        }

        double dps = playerDamageDealt.get(player.getUniqueId()) / playerSecondsInCombat.get(player.getUniqueId());
        playerDps.put(player.getUniqueId(), dps);
    }

    //make this add each second
    public void addPlayerSecondsInCombat(Player player){

        if(!playerSecondsInCombat.containsKey(player.getUniqueId())){
            playerSecondsInCombat.put(player.getUniqueId(), 0);
        }
        int oldSeconds = playerSecondsInCombat.get(player.getUniqueId());
        int newSeconds = oldSeconds + 1;

        playerSecondsInCombat.put(player.getUniqueId(), newSeconds);
        setPlayerDps(player);
    }

    public void addDamageToDamageDealt(Player player, Double damage){
        if(!playerDamageDealt.containsKey(player.getUniqueId())){
            playerDamageDealt.put(player.getUniqueId(), 0.0);
        }

        long currentTime = System.currentTimeMillis() / 1000;
        lastDamagedEnemy.put(player.getUniqueId(), currentTime);

        double oldDamage = playerDamageDealt.get(player.getUniqueId());
        double newDamage = oldDamage + damage;
        playerDamageDealt.put(player.getUniqueId(), newDamage);
    }

    public void removeDps(Player player){
        if(!playerDps.containsKey(player.getUniqueId())){
            return;
        }
        playerDps.remove(player.getUniqueId());
        playerSecondsInCombat.remove(player.getUniqueId());
        playerDamageDealt.remove(player.getUniqueId());
        lastDamagedEnemy.remove(player.getUniqueId());
    }

    public double getRawDps(Player player){

        if(!playerDps.containsKey(player.getUniqueId())){
            playerDps.put(player.getUniqueId(), 0.0);
        }

        return playerDps.get(player.getUniqueId());
    }

    public int getRoundedDps(Player player){

        if(!playerDps.containsKey(player.getUniqueId())){
            playerDps.put(player.getUniqueId(), 0.0);
        }

        return (int) Math.round(playerDps.get(player.getUniqueId()));
    }
}
