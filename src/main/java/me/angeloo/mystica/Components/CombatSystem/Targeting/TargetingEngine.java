package me.angeloo.mystica.Components.CombatSystem.Targeting;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.CombatSystem.GravestoneManager;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class TargetingEngine {

    private final ProfileManager profileManager;
    private final MysticaPartyManager partyManager;
    private final GravestoneManager gravestoneManager;
    private final TargetValidator targetValidator;


    private final Map<UUID, List<LivingEntity>> cachedTargets = new HashMap<>();
    private final Map<UUID, Integer> targetIndex = new HashMap<>();
    private final Map<UUID, Integer> partyIndex = new HashMap<>();

    public TargetingEngine(Mystica main){
        profileManager = main.getProfileManager();
        partyManager = main.getMysticaPartyManager();
        gravestoneManager = main.getGravestoneManager();
        targetValidator = new TargetValidator(main);
    }

    //used when abilities require a target and player does not have currently
    public LivingEntity findTarget(Player player, TargetingContext context){

        List<LivingEntity> candidates = getCandidates(player, context);

        if(candidates.isEmpty()){

            return null;
        }

        cachedTargets.put(player.getUniqueId(), candidates);
        targetIndex.put(player.getUniqueId(), 0);

        return candidates.get(0);
    }

    public LivingEntity cycleTarget(Player player, TargetingContext context){

        UUID id = player.getUniqueId();

        List<LivingEntity> fresh = getCandidates(player, context);
        List<LivingEntity> cached = cachedTargets.get(id);

        List<LivingEntity> merged = mergeCandidates(player, cached, fresh,context);

        List<LivingEntity> candidates = filterByPriority(merged, context);

        if(candidates.isEmpty()){
            cachedTargets.remove(id);
            targetIndex.remove(id);
            return null;
        }

        cachedTargets.put(id, merged); // store full list, not filtered

        int index = targetIndex.getOrDefault(id, -1);
        index = (index + 1) % candidates.size();

        targetIndex.put(id, index);


        return candidates.get(index);
    }

    private List<LivingEntity> mergeCandidates(
            Player player,
            List<LivingEntity> oldList,
            List<LivingEntity> newList,
            TargetingContext context
    ){

        Map<UUID, LivingEntity> merged = new LinkedHashMap<>();

        // keep old order first (important for stable cycling)
        if(oldList != null){
            for(LivingEntity e : oldList){
                if(isStillValid(player, e,context)){
                    merged.put(e.getUniqueId(), e);
                }
            }
        }

        // add new ones
        for(LivingEntity e : newList){
            if(isStillValid(player, e,context)){
                merged.putIfAbsent(e.getUniqueId(), e);
            }
        }

        return new ArrayList<>(merged.values());
    }

    private boolean isStillValid(Player player, LivingEntity entity, TargetingContext context){

        if(entity == null) return false;

        if(!entity.isValid()) return false;

        if(entity.isDead()) return false;

        if(!entity.getWorld().equals(player.getWorld())){
            return false;
        }

        if(player.getLocation().distance(entity.getLocation())>context.range){
            return false;
        }

        // your centralized validator
        return targetValidator.isValidTarget(player, entity);
    }

    public List<LivingEntity> getCandidates(Player player, TargetingContext context){

        Location origin = player.getEyeLocation();

        double rangeSquared = context.range * context.range;

        List<LivingEntity> result = new ArrayList<>();

        for(Entity entity : player.getWorld().getNearbyEntities(origin, context.range, context.range, context.range)){

            if(!(entity instanceof LivingEntity target)){
                continue;
            }

            if(entity == player){
                continue;
            }

            if(!targetValidator.isValidTarget(player, target)){
                continue;
            }

            double distSq = target.getLocation().distanceSquared(origin);

            if(distSq > rangeSquared){
                continue;
            }

            result.add(target);
        }

        result.sort(Comparator.comparingDouble(t -> scoreTarget(player, t, context)));

        return result;
    }



    private boolean isWithinFOV(Location origin, Vector forward, Location targetLoc, double fov){

        Vector toTarget = targetLoc.toVector().subtract(origin.toVector()).normalize();

        double dot = forward.dot(toTarget);

        double angle = Math.acos(dot);

        return angle <= (fov / 2);
    }

    private List<LivingEntity> filterByPriority(List<LivingEntity> candidates, TargetingContext context){

        List<LivingEntity> high = new ArrayList<>();
        List<LivingEntity> low = new ArrayList<>();

        for(LivingEntity e : candidates){
            if(isPlayerLike(e)){
                if(context.prioritizePlayers){
                    high.add(e);
                } else {
                    low.add(e);
                }
            } else {
                if(context.prioritizePlayers){
                    low.add(e);
                } else {
                    high.add(e);
                }
            }
        }

        return !high.isEmpty() ? high : low;
    }

    private double scoreTarget(Player player, LivingEntity target, TargetingContext context){

        double distance = player.getLocation().distanceSquared(target.getLocation());

        double score = distance;

        if(isWithinFOV(player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                target.getLocation(),
                context.fov)){
            score -= 100; // strong bias
        }

        return score;
    }

    private boolean isPlayerLike(LivingEntity entity){

        if(entity instanceof Player){
            return true;
        }

        if(MythicBukkit.inst().getAPIHelper().isMythicMob(entity.getUniqueId())){
            return profileManager.getAnyProfile(entity).fakePlayer();
        }

        return false;
    }

    public LivingEntity cycleParty(Player player, int direction){

        List<LivingEntity> mParty = new ArrayList<>(partyManager.getMysticaParty(player));

        //cannot target self
        mParty.remove(player);

        if(mParty.isEmpty()){
            return null;
        }

        //change this to index of whomever player is currently targeting, if they are in the team
        int index = 0;

        if(partyIndex.containsKey(player.getUniqueId())){
            index = partyIndex.get(player.getUniqueId());
        }

        index += direction;

        index = Math.floorMod(index, mParty.size());

        partyIndex.put(player.getUniqueId(), index);

        LivingEntity target = mParty.get(index);

        if(target instanceof Player){

            if(profileManager.getAnyProfile(target).getIfDead()){
                return gravestoneManager.getPlayer(target);
            }

        }

        return target;
    }

    public void clearCache(Player player){
        UUID id = player.getUniqueId();
        cachedTargets.remove(id);
        targetIndex.remove(id);
    }

}
