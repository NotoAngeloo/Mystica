package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class AggroManager {

    private final ProfileManager profileManager;
    private final Map<UUID, List<LivingEntity>> creatureListOfAttackers = new HashMap<>();
    private final Map<UUID, LivingEntity> lastPlayerWhoHit = new HashMap<>();
    private final Map<UUID, LivingEntity> creatureHighPriorityTarget = new HashMap<>();
    private final Map<UUID, Long> lastSetAsPriority = new HashMap<>();
    private final Map<LivingEntity, Boolean> blacklist = new HashMap<>();

    public AggroManager(Mystica main){
        profileManager = main.getProfileManager();
    }

    public void addAttacker(LivingEntity entity, LivingEntity attacker){

        if(!creatureListOfAttackers.containsKey(entity.getUniqueId())){
            creatureListOfAttackers.put(entity.getUniqueId(), new ArrayList<>());
        }

        lastPlayerWhoHit.put(entity.getUniqueId(), attacker);

        List<LivingEntity> attackers = creatureListOfAttackers.get(entity.getUniqueId());

        if(attackers.contains(attacker)){
            return;
        }


        attackers.add(attacker);
        creatureListOfAttackers.put(entity.getUniqueId(), attackers);

    }

    public void removeFromAllAttackerLists(LivingEntity caster){
        for(Map.Entry<UUID, List<LivingEntity>> entry : creatureListOfAttackers.entrySet()){
            UUID entityUUID = entry.getKey();
            List<LivingEntity> entityListOfAttackers = entry.getValue();
            entityListOfAttackers.remove(caster);
            creatureListOfAttackers.put(entityUUID, entityListOfAttackers);
        }

        for(Map.Entry<UUID, LivingEntity> entry : creatureHighPriorityTarget.entrySet()){
            UUID entityUUID = entry.getKey();
            LivingEntity highPriorityPlayer = entry.getValue();

            if(caster == highPriorityPlayer){
                removeHighPriorityTarget(entityUUID);
            }
        }
    }

    public List<LivingEntity> getAttackerList(LivingEntity entity){

        if(!creatureListOfAttackers.containsKey(entity.getUniqueId())){
            creatureListOfAttackers.put(entity.getUniqueId(), new ArrayList<>());
        }
        return creatureListOfAttackers.get(entity.getUniqueId());
    }

    public List<LivingEntity> getAliveAttackers(LivingEntity entity){

        List<LivingEntity> liveAttackers = new ArrayList<>();

        for(LivingEntity attacker : getAttackerList(entity)){
            if(!profileManager.getAnyProfile(attacker).getIfDead()){
                liveAttackers.add(attacker);
            }
        }

        return liveAttackers;
    }

    public void clearAttackerList(LivingEntity entity){
        creatureListOfAttackers.put(entity.getUniqueId(), new ArrayList<>());
    }

    public void clearLastPlayer(LivingEntity entity){
        lastPlayerWhoHit.remove(entity.getUniqueId());
    }

    public LivingEntity getLastPlayer(LivingEntity entity){
        return lastPlayerWhoHit.getOrDefault(entity.getUniqueId(), null);
    }

    public void setAsHighPriorityTarget(LivingEntity entity, LivingEntity attacker){
        creatureHighPriorityTarget.put(entity.getUniqueId(), attacker);
        Long currentTime = System.currentTimeMillis() / 1000;
        setLastSetAsPriority(entity, currentTime);
    }

    public LivingEntity getHighPriorityTarget(LivingEntity entity){
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

    public void addToBlackList(LivingEntity attacker){
        blacklist.put(attacker, true);
    }

    public void removeFromBlackList(LivingEntity attacker){
        blacklist.remove(attacker);
    }

    public boolean getIfOnBlackList(LivingEntity attacker){
        if(blacklist.containsKey(attacker)){
            return blacklist.get(attacker);
        }

        return false;
    }
}
