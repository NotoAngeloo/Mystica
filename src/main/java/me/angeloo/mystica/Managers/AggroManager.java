package me.angeloo.mystica.Managers;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class AggroManager {

    private final Map<UUID, List<Player>> creatureListOfAttackers = new HashMap<>();
    private final Map<UUID, Player> lastPlayerWhoHit = new HashMap<>();
    private final Map<UUID, Player> creatureHighPriorityTarget = new HashMap<>();
    private final Map<UUID, Long> lastSetAsPriority = new HashMap<>();
    private final Map<Player, Boolean> blacklist = new HashMap<>();
    //private final TargetManager targetManager;

    public AggroManager(){
    }

    public void addAttacker(LivingEntity entity, Player player){

        if(!creatureListOfAttackers.containsKey(entity.getUniqueId())){
            creatureListOfAttackers.put(entity.getUniqueId(), new ArrayList<>());
        }

        lastPlayerWhoHit.put(entity.getUniqueId(), player);

        List<Player> attackers = creatureListOfAttackers.get(entity.getUniqueId());

        if(attackers.contains(player)){
            return;
        }

        attackers.add(player);
        creatureListOfAttackers.put(entity.getUniqueId(), attackers);

    }

    public void removeFromAllAttackerLists(Player player){
        for(Map.Entry<UUID, List<Player>> entry : creatureListOfAttackers.entrySet()){
            UUID entityUUID = entry.getKey();
            List<Player> entityListOfAttackers = entry.getValue();
            entityListOfAttackers.remove(player);
            creatureListOfAttackers.put(entityUUID, entityListOfAttackers);
        }

        for(Map.Entry<UUID, Player> entry : creatureHighPriorityTarget.entrySet()){
            UUID entityUUID = entry.getKey();
            Player highPriorityPlayer = entry.getValue();

            if(player == highPriorityPlayer){
                removeHighPriorityTarget(entityUUID);
            }
        }
    }

    public List<Player> getAttackerList(LivingEntity entity){

        if(!creatureListOfAttackers.containsKey(entity.getUniqueId())){
            creatureListOfAttackers.put(entity.getUniqueId(), new ArrayList<>());
        }
        return creatureListOfAttackers.get(entity.getUniqueId());
    }

    public void clearAttackerList(LivingEntity entity){
        creatureListOfAttackers.put(entity.getUniqueId(), new ArrayList<>());
    }

    public void clearLastPlayer(LivingEntity entity){
        lastPlayerWhoHit.remove(entity.getUniqueId());
    }

    public Player getLastPlayer(LivingEntity entity){
        return lastPlayerWhoHit.getOrDefault(entity.getUniqueId(), null);
    }

    public void setAsHighPriorityTarget(LivingEntity entity, Player player){
        creatureHighPriorityTarget.put(entity.getUniqueId(), player);
        Long currentTime = System.currentTimeMillis() / 1000;
        setLastSetAsPriority(entity, currentTime);
    }

    public Player getHighPriorityTarget(LivingEntity entity){
        if(!creatureHighPriorityTarget.containsKey(entity.getUniqueId())){
            creatureHighPriorityTarget.put(entity.getUniqueId(), null);
        }

        return creatureHighPriorityTarget.get(entity.getUniqueId());
    }

    public void removeHighPriorityTarget(UUID uuid){
        creatureHighPriorityTarget.put(uuid, null);
    }

    private void setLastSetAsPriority(LivingEntity entity, Long time){
        lastSetAsPriority.put(entity.getUniqueId(), time);
    }

    public long getLastSetAsPriority(LivingEntity entity){
        Long currentTime = System.currentTimeMillis() / 1000;
        if(!lastSetAsPriority.containsKey(entity.getUniqueId())){
            lastSetAsPriority.put(entity.getUniqueId(), currentTime);
        }
        return lastSetAsPriority.get(entity.getUniqueId());
    }

    public void addToBlackList(Player player){
        blacklist.put(player, true);
    }

    public void removeFromBlackList(Player player){
        blacklist.remove(player);
    }

    public boolean getIfOnBlackList(Player player){
        if(blacklist.containsKey(player)){
            return blacklist.get(player);
        }

        return false;
    }
}
