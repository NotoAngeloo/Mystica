package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Logic.StealthTargetBlacklist;
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

        if (target == null) {
            return;
        }

        //maybe make this subclass in the future, easy enough to change
        switch (profileManager.getAnyProfile(entity).getPlayerClass()) {
            case Elementalist, Warrior, Ranger -> {


                if (pveChecker.pveLogic(target) && !profileManager.getAnyProfile(target).getIfObject()) {
                    targetMap.put(entity.getUniqueId(), target);
                }

            }
            case Paladin -> {

                if (targetMap.containsKey(entity.getUniqueId())) {

                    LivingEntity currentTarget = targetMap.get(entity.getUniqueId());

                    //ignore if already targeting a boss
                    if (profileManager.getIfEntityIsBoss(currentTarget.getUniqueId())) {
                        return;
                    }

                }

                if (pveChecker.pveLogic(target) && !profileManager.getAnyProfile(target).getIfObject()) {
                    targetMap.put(entity.getUniqueId(), target);
                    //Bukkit.getLogger().info("target of companion set to " + target);
                }

            }
        }

    }

    public void setFakePlayerTarget(LivingEntity caster, LivingEntity entity){

        if(entity instanceof ArmorStand){
            return;
        }

        targetMap.put(caster.getUniqueId(), entity);

    }

    //do something different for mystics

    public LivingEntity getTarget(LivingEntity entity){
        return targetMap.getOrDefault(entity.getUniqueId(), entity);
    }



}
