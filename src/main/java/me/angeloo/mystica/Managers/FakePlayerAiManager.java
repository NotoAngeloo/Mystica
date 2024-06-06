package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;


public class FakePlayerAiManager {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final AbilityManager abilityManager;

    private final Map<UUID, BukkitTask> aiTaskMap = new HashMap<>();

    private final Map<UUID, Boolean> cautionMap = new HashMap<>();

    public FakePlayerAiManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = main.getAbilityManager();
        targetManager = main.getTargetManager();
    }


    public void signal(LivingEntity companion, String signal){

        if(MythicBukkit.inst().getAPIHelper().isMythicMob(companion.getUniqueId())){
            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(companion).getEntity();
            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(companion).signalMob(abstractEntity, signal);
        }

        if(signal.equalsIgnoreCase("attack")){

            if(aiTaskMap.containsKey(companion.getUniqueId())){
                return;
            }

            switch (profileManager.getAnyProfile(companion).getPlayerClass().toLowerCase()){
                case "paladin":{
                    startTemplarRotation(companion);
                    break;
                }

            }
        }

    }

    private void startTemplarRotation(LivingEntity companion){

        PaladinAbilities paladinAbilities = abilityManager.getPaladinAbilities();

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(companion.isDead()){
                    stopAiTask(companion);
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion);
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = targetManager.getPlayerTarget(companion);

                if(target.isDead()){
                    stopAiTask(companion);
                    return;
                }

                double distance = target.getLocation().distance(companion.getLocation());

                int healthPercent = (int) Math.round((profileManager.getAnyProfile(companion).getCurrentHealth() / (double) profileManager.getAnyProfile(companion).getTotalHealth()) * 100);

                if(healthPercent <=50){

                    if(paladinAbilities.getSanctityShield().usable(companion)){
                        paladinAbilities.getSanctityShield().use(companion);
                        return;
                    }

                }

                if(paladinAbilities.getJudgement().usable(companion)){

                    //maybe later check hp to see if needed to heal

                    paladinAbilities.getJudgement().use(companion);
                    return;
                }

                //only if not running away
                if(distance>=10){
                    if(paladinAbilities.getDuranceOfTruth().usable(companion)){
                        paladinAbilities.getDuranceOfTruth().use(companion);
                        return;
                    }
                }

                if (distance >= 10) {
                    if (paladinAbilities.getOrderShield().usable(companion, target)) {
                        paladinAbilities.getOrderShield().use(companion);
                        return;
                    }
                }


                if(!getIfCautious(companion)){
                    if(paladinAbilities.getGloryOfPaladins().usable(companion)){
                        paladinAbilities.getGloryOfPaladins().use(companion);
                    }
                }



                if (paladinAbilities.getTorahSword().usable(companion, target)) {
                    paladinAbilities.getTorahSword().use(companion);
                    return;
                }


                if(!getIfCautious(companion)){
                    if(paladinAbilities.getReigningSword().usable(companion)){
                        paladinAbilities.getReigningSword().use(companion);
                        return;
                    }
                }
                else{
                    if(paladinAbilities.getReigningSword().usable(companion)){
                        if(healthPercent<=50){
                            paladinAbilities.getReigningSword().use(companion);
                            return;
                        }
                    }
                }


                if(!getIfCautious(companion)){
                    if(paladinAbilities.getDuranceOfTruth().usable(companion)){
                        paladinAbilities.getDuranceOfTruth().use(companion);
                        return;
                    }
                }



                if(!getIfCautious(companion)){
                    if(distance<5){
                        paladinAbilities.getPaladinBasic().useBasic(companion);
                    }
                }


            }
        }.runTaskTimer(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);

    }

    public void stopAiTask(LivingEntity entity){

        if(aiTaskMap.containsKey(entity.getUniqueId())){
            aiTaskMap.get(entity.getUniqueId()).cancel();
        }

        aiTaskMap.remove(entity.getUniqueId());

    }

    public void setCaution(LivingEntity entity, boolean caution){
        cautionMap.put(entity.getUniqueId(), caution);
    }

    private boolean getIfCautious(LivingEntity entity){
        return cautionMap.getOrDefault(entity.getUniqueId(), false);
    }

}
