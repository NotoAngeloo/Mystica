package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.PveChecker;
import me.angeloo.mystica.Utility.StealthTargetBlacklist;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakePlayerTargetManager {

    private final ProfileManager profileManager;
    private final PvpManager pvpManager;
    private final StealthTargetBlacklist stealthTargetBlacklist;
    private final PveChecker pveChecker;

    private final Map<UUID, LivingEntity> targetMap = new HashMap<>();

    public FakePlayerTargetManager(Mystica main){
        profileManager = main.getProfileManager();
        pvpManager = main.getPvpManager();
        stealthTargetBlacklist = main.getStealthTargetBlacklist();
        pveChecker = main.getPveChecker();
    }

    public void suggestTarget(LivingEntity entity, LivingEntity target){
        //first check the class

        //maybe make this subclass in the future, easy enough to change
        switch (profileManager.getAnyProfile(entity).getPlayerClass().toLowerCase()){
            case "elementalist":
            case "warrior":
            case "ranger":{

                if(pveChecker.pveLogic(target) && !profileManager.getAnyProfile(target).getIfObject()){
                    targetMap.put(entity.getUniqueId(), target);
                }

                break;
            }
            case "paladin":{

                if(targetMap.containsKey(entity.getUniqueId())){

                    LivingEntity currentTarget = targetMap.get(entity.getUniqueId());

                    //ignore if already targeting a boss
                    if(profileManager.getIfEntityIsBoss(currentTarget.getUniqueId())){
                        break;
                    }

                }

                if(pveChecker.pveLogic(target) && !profileManager.getAnyProfile(target).getIfObject()){
                    targetMap.put(entity.getUniqueId(), target);
                    //Bukkit.getLogger().info("target of companion set to " + target);
                }

                break;
            }
        }

    }

    public void setFakePlayerTarget(LivingEntity caster, LivingEntity entity){

        if(entity instanceof ArmorStand){
            return;
        }

        targetMap.put(caster.getUniqueId(), entity);

    }

    public void setTargetToNearestValid(LivingEntity caster, double radius){

        LivingEntity target = getTarget(caster);

        if(target != null){
            return;
        }

        BoundingBox boundingBox = new BoundingBox(
                caster.getLocation().getX() - radius,
                caster.getLocation().getY() - 2,
                caster.getLocation().getZ() - radius,
                caster.getLocation().getX() + radius,
                caster.getLocation().getY() + 3,
                caster.getLocation().getZ() + radius
        );


        double closestDistanceSquared = Double.MAX_VALUE;
        LivingEntity theClosestEntity = null;

        for(Entity entity : caster.getWorld().getNearbyEntities(boundingBox)){

            if(entity == caster){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;

            if(entity.isDead()){
                continue;
            }

            if(entity instanceof Player){
                double distanceSquared = entity.getLocation().distanceSquared(caster.getLocation());

                Player entityPlayer = (Player) entity;

                if(stealthTargetBlacklist.get(entityPlayer)){
                    continue;
                }

                boolean deathStatus = profileManager.getAnyProfile(entityPlayer).getIfDead();

                if(deathStatus){
                    continue;
                }

                if(pvpManager.pvpLogic(caster, entityPlayer)){
                    if(distanceSquared < closestDistanceSquared){
                        theClosestEntity = entityPlayer;
                        closestDistanceSquared = distanceSquared;
                    }
                }
                continue;
            }

            if (pveChecker.pveLogic(livingEntity)) {

                double distanceSquared = entity.getLocation().distanceSquared(caster.getLocation());

                if(distanceSquared < closestDistanceSquared){
                    theClosestEntity = livingEntity;
                    closestDistanceSquared = distanceSquared;
                }
            }

        }

        if(theClosestEntity != null){
            suggestTarget(caster, theClosestEntity);
        }

    }

    //do something different for mystics

    public LivingEntity getTarget(LivingEntity entity){
        return targetMap.get(entity.getUniqueId());
    }



}
