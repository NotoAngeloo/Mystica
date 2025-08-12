package me.angeloo.mystica.Managers.Parties;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Abilities.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.DeathManager;
import me.angeloo.mystica.Managers.FakePlayerTargetManager;
import me.angeloo.mystica.Managers.Parties.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;


public class FakePlayerAiManager {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final DeathManager deathManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final AbilityManager abilityManager;

    private final Map<UUID, BukkitTask> aiTaskMap = new HashMap<>();

    private final Map<UUID, Boolean> cautionMap = new HashMap<>();
    private final Map<UUID, Boolean> needToInterrupt = new HashMap<>();

    public FakePlayerAiManager(Mystica main){
        this.main = main;
        mysticaPartyManager = main.getMysticaPartyManager();
        profileManager = main.getProfileManager();
        abilityManager = main.getAbilityManager();
        deathManager = main.getDeathManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
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

            switch (profileManager.getAnyProfile(companion).getPlayerClass()) {
                case Paladin -> {
                    startTemplarRotation(companion);
                }
                case Ranger -> {
                    startTamerRotation(companion);
                }
                case Mystic -> {
                    startShepardRotation(companion);
                }
                case Warrior -> {
                    startExecutionerRotation(companion);
                }
                case Elementalist -> {
                    startConjurerRotation(companion);
                }
            }
        }

    }

    private void startTamerRotation(LivingEntity companion){
        RangerAbilities rangerAbilities = abilityManager.getRangerAbilities();

        profileManager.getAnyProfile(companion).setIfInCombat(true);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(this.isCancelled()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(profileManager.getAnyProfile(target).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }


                if(rangerAbilities.getWildRoar().usable(companion)){

                    Bukkit.getScheduler().runTask(main,()->{
                        rangerAbilities.getWildRoar().use(companion);
                    });


                    return;
                }

                if(rangerAbilities.getShadowCrows().usable(companion, target)){

                    Bukkit.getScheduler().runTask(main,()->{
                        rangerAbilities.getShadowCrows().use(companion);
                    });


                    return;
                }

                if(rangerAbilities.getWildSpirit().usable(companion)){

                    Bukkit.getScheduler().runTask(main,()->{
                        rangerAbilities.getWildSpirit().sendSignal(companion);
                    });

                    return;
                }

                if(rangerAbilities.getRallyingCry().getIfBuffTime(companion)>0){
                    if(rangerAbilities.getBlessedArrow().usable(companion, target)){

                        Bukkit.getScheduler().runTask(main,()->{
                            rangerAbilities.getBlessedArrow().use(companion);
                        });


                        return;
                    }

                    Bukkit.getScheduler().runTask(main,()->{
                        rangerAbilities.getRangerBasic().useBasic(companion);
                    });


                    return;
                }

                if(rangerAbilities.getRelentless().usable(companion, target)){

                    Bukkit.getScheduler().runTask(main,()->{
                        rangerAbilities.getRelentless().use(companion);
                    });


                    return;
                }

                if(rangerAbilities.getRazorWind().usable(companion, target)){

                    Bukkit.getScheduler().runTask(main,()->{
                        rangerAbilities.getRazorWind().use(companion);
                    });


                    return;
                }


                if(rangerAbilities.getRallyingCry().usable(companion)){

                    Bukkit.getScheduler().runTask(main,()->{
                        rangerAbilities.getRallyingCry().use(companion);
                    });


                    return;
                }

                if(rangerAbilities.getBlessedArrow().usable(companion, target)){

                    Bukkit.getScheduler().runTask(main,()->{
                        rangerAbilities.getBlessedArrow().use(companion);
                    });


                    return;
                }


                if(rangerAbilities.getBitingRain().usable(companion, target)){

                    Bukkit.getScheduler().runTask(main,()->{
                        rangerAbilities.getBitingRain().use(companion);
                    });


                }

                Bukkit.getScheduler().runTask(main,()->{
                    rangerAbilities.getRangerBasic().useBasic(companion);
                });




            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());
    }

    private void startTemplarRotation(LivingEntity companion){

        PaladinAbilities paladinAbilities = abilityManager.getPaladinAbilities();

        profileManager.getAnyProfile(companion).setIfInCombat(true);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(this.isCancelled()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(profileManager.getAnyProfile(target).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }



                double distance = target.getLocation().distance(companion.getLocation());

                int healthPercent = (int) Math.round((profileManager.getAnyProfile(companion).getCurrentHealth() / (double) profileManager.getAnyProfile(companion).getTotalHealth()) * 100);

                if(healthPercent <=50){
                    if(paladinAbilities.getSanctityShield().usable(companion)){

                        Bukkit.getScheduler().runTask(main,()->{
                            paladinAbilities.getSanctityShield().use(companion);
                        });


                        return;
                    }

                }

                if(paladinAbilities.getJudgement().usable(companion)){

                    //maybe later check hp to see if needed to heal
                    Bukkit.getScheduler().runTask(main,()->{
                        paladinAbilities.getJudgement().use(companion);
                    });


                    return;
                }



                if (distance >= 10) {
                    if (paladinAbilities.getOrderShield().usable(companion, target)) {

                        Bukkit.getScheduler().runTask(main,()->{
                            paladinAbilities.getOrderShield().use(companion);
                        });


                        return;
                    }
                }


                if(!getIfCautious(companion)){
                    if(paladinAbilities.getGloryOfPaladins().usable(companion)){

                        Bukkit.getScheduler().runTask(main,()->{
                            paladinAbilities.getGloryOfPaladins().use(companion);
                        });


                    }
                }



                if (paladinAbilities.getTorahSword().usable(companion, target)) {

                    Bukkit.getScheduler().runTask(main,()->{
                        paladinAbilities.getTorahSword().use(companion);
                    });


                    return;
                }


                if(!getIfCautious(companion)){
                    if(paladinAbilities.getReigningSword().usable(companion)){

                        Bukkit.getScheduler().runTask(main,()->{
                            paladinAbilities.getReigningSword().use(companion);
                        });

                        return;
                    }
                }
                else{
                    if(paladinAbilities.getReigningSword().usable(companion)){
                        if(healthPercent<=50){
                            Bukkit.getScheduler().runTask(main,()->{
                                paladinAbilities.getReigningSword().use(companion);
                            });

                            return;
                        }
                    }
                }

                if(healthPercent<=50){
                    if(paladinAbilities.getDivineGuidance().usable(companion)){
                        Bukkit.getScheduler().runTask(main,()->{
                            paladinAbilities.getDivineGuidance().use(companion);
                        });

                        return;
                    }
                }
                else{
                    if(distance<=5){
                        if(paladinAbilities.getDivineGuidance().usable(companion)){
                            Bukkit.getScheduler().runTask(main,()->{
                                paladinAbilities.getDivineGuidance().use(companion);
                            });

                            return;
                        }
                    }
                }

                if(!getIfCautious(companion)){
                    if(distance<=8){
                        if(paladinAbilities.getDuranceOfTruth().usable(companion)){
                            Bukkit.getScheduler().runTask(main,()->{
                                paladinAbilities.getDuranceOfTruth().use(companion);
                            });

                            return;
                        }
                    }
                }



                if(!getIfCautious(companion)){
                    if(distance<5){
                        Bukkit.getScheduler().runTask(main,()->{
                            paladinAbilities.getPaladinBasic().useBasic(companion);
                        });

                    }
                }


            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());

    }

    private void startShepardRotation(LivingEntity companion){
        MysticAbilities mysticAbilities = abilityManager.getMysticAbilities();

        profileManager.getAnyProfile(companion).setIfInCombat(true);

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(companion));

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                //Bukkit.getLogger().info("mystic");
                if(this.isCancelled()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                List<LivingEntity> liveParty = new ArrayList<>();

                for(LivingEntity member : mParty){
                    if(profileManager.getAnyProfile(member).getIfDead()){
                        continue;
                    }
                    liveParty.add(member);
                }

                liveParty.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth()/(double)profileManager.getAnyProfile(p).getTotalHealth()));
                LivingEntity lowest = liveParty.get(0);


                double base = 0;
                for(LivingEntity member : mParty){
                    base += ((profileManager.getAnyProfile(member).getCurrentHealth()/(double)profileManager.getAnyProfile(member).getTotalHealth()) * 100);
                }
                double averagePhp = base / mParty.size();

                //Bukkit.getLogger().info(String.valueOf(averagePhp));

                double lowestHealthPercent = profileManager.getAnyProfile(lowest).getCurrentHealth()/(double)profileManager.getAnyProfile(lowest).getTotalHealth();


                if(averagePhp <= 75){
                    if(mysticAbilities.getEnlightenment().usable(companion)){

                        Bukkit.getScheduler().runTask(main,()->{
                            mysticAbilities.getEnlightenment().use(companion);
                        });


                        return;
                    }

                    if(mysticAbilities.getPurifyingBlast().getInstantCast(companion)){
                        if(mysticAbilities.getPurifyingBlast().usable(companion)){

                            Bukkit.getScheduler().runTask(main,()->{
                                mysticAbilities.getPurifyingBlast().use(companion);
                            });


                            return;
                        }
                    }
                }

                if(averagePhp <= 50){
                    if(mysticAbilities.getAurora().usable(companion, lowest)){

                        Bukkit.getScheduler().runTask(main,()->{
                            fakePlayerTargetManager.setFakePlayerTarget(companion, lowest);

                            if(fakePlayerTargetManager.getTarget(companion) == companion){
                                fakePlayerTargetManager.setFakePlayerTarget(companion, null);
                            }
                            mysticAbilities.getAurora().use(companion);
                        });


                        return;
                    }

                }

                if(lowestHealthPercent<=50){

                    Bukkit.getScheduler().runTask(main,()->{

                        fakePlayerTargetManager.setFakePlayerTarget(companion, lowest);

                        if(fakePlayerTargetManager.getTarget(companion) == companion){
                            fakePlayerTargetManager.setFakePlayerTarget(companion, null);
                        }

                        if(mysticAbilities.getArcaneShield().usable(companion, lowest)){
                            mysticAbilities.getArcaneShield().use(companion);
                        }

                    });


                }

                if(averagePhp <= 75){
                    if(mysticAbilities.getLightSigil().usable(companion)){

                        Bukkit.getScheduler().runTask(main,()->{
                            mysticAbilities.getLightSigil().use(companion);
                        });


                        return;
                    }
                }


                fakePlayerTargetManager.setFakePlayerTarget(companion, lowest);

                if(fakePlayerTargetManager.getTarget(companion) == companion){
                    fakePlayerTargetManager.setFakePlayerTarget(companion, null);
                }

                Bukkit.getScheduler().runTask(main,()->{
                    mysticAbilities.getMysticBasic().useBasic(companion);
                });


                fakePlayerTargetManager.setFakePlayerTarget(companion, lowest);

            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());
    }

    private void startExecutionerRotation(LivingEntity companion){

        WarriorAbilities warriorAbilities = abilityManager.getWarriorAbilities();

        profileManager.getAnyProfile(companion).setIfInCombat(true);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                //Bukkit.getLogger().info("warrior");
                if(this.isCancelled()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }


                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(profileManager.getAnyProfile(target).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }



                double distance = target.getLocation().distance(companion.getLocation());

                //Bukkit.getLogger().info(String.valueOf(warriorAbilities.getRage().getCurrentRage(companion)));

                //if need to interupt, do it
                if(getIfNeedToInterrupt(companion)){

                    //Bukkit.getLogger().info("need to interrupt");

                    if(warriorAbilities.getMeteorCrater().usable(companion)){

                        //Bukkit.getLogger().info("usable");

                        if(distance<5){
                            //Bukkit.getLogger().info("Skill cast");

                            Bukkit.getScheduler().runTask(main,()->{
                                warriorAbilities.getMeteorCrater().use(companion);
                                removeInterrupt(companion);
                            });

                            return;
                        }

                    }
                }



                if(warriorAbilities.getFlamingSigil().usable(companion)){

                    Bukkit.getScheduler().runTask(main,()->{
                        warriorAbilities.getFlamingSigil().use(companion);
                    });

                    return;
                }

                if(distance<8){
                    if(warriorAbilities.getTempestRage().usable(companion)){

                        Bukkit.getScheduler().runTask(main,()->{
                            warriorAbilities.getTempestRage().use(companion);
                        });


                        return;
                    }

                    if(warriorAbilities.getLavaQuake().usable(companion)){

                        Bukkit.getScheduler().runTask(main,()->{
                            warriorAbilities.getLavaQuake().use(companion);
                        });


                        return;
                    }
                }

                if(!getIfCautious(companion)){
                    if(distance<5){
                        if(warriorAbilities.getMagmaSpikes().usable(companion)){

                            Bukkit.getScheduler().runTask(main,()->{
                                warriorAbilities.getMagmaSpikes().use(companion);
                            });


                            return;
                        }
                    }
                }


                if(!getIfCautious(companion)){
                    if(warriorAbilities.getDeathGaze().usable(companion, target)){

                        Bukkit.getScheduler().runTask(main,()->{
                            warriorAbilities.getDeathGaze().use(companion);
                        });


                    }
                }


                if(!getIfCautious(companion)){
                    if(distance>=8 && distance < 15){
                        if(warriorAbilities.getAnvilDrop().usable(companion)){

                            Bukkit.getScheduler().runTask(main,()->{
                                warriorAbilities.getAnvilDrop().use(companion);
                            });


                            return;
                        }
                    }
                }

                if(!getIfCautious(companion)){
                    if(distance<5){

                        Bukkit.getScheduler().runTask(main,()->{
                            warriorAbilities.getWarriorBasic().useBasic(companion);
                        });


                    }
                }

            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());
    }

    private void startConjurerRotation(LivingEntity companion){

        ElementalistAbilities elementalistAbilities = abilityManager.getElementalistAbilities();

        profileManager.getAnyProfile(companion).setIfInCombat(true);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(this.isCancelled()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //Bukkit.getLogger().info("elementalist");

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(profileManager.getAnyProfile(target).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }


                int heat = elementalistAbilities.getHeat().getHeat(companion);

                int healthPercent = (int) Math.round((profileManager.getAnyProfile(companion).getCurrentHealth() / (double) profileManager.getAnyProfile(companion).getTotalHealth()) * 100);

                if(elementalistAbilities.getConjuringForce().usable(companion)){

                    Bukkit.getScheduler().runTask(main,()->{
                        elementalistAbilities.getConjuringForce().use(companion);
                    });


                    return;
                }

                if(elementalistAbilities.getElementalBreath().usable(companion)){

                    Bukkit.getScheduler().runTask(main,()->{
                        elementalistAbilities.getElementalBreath().use(companion);
                    });


                    return;
                }

                if(elementalistAbilities.getElemental_matrix().usable(companion, target)){

                    Bukkit.getScheduler().runTask(main,()->{
                        elementalistAbilities.getElemental_matrix().use(companion);
                    });


                    return;
                }

                if(elementalistAbilities.getIceBolt().usable(companion, target)){

                    Bukkit.getScheduler().runTask(main,()->{
                        elementalistAbilities.getIceBolt().use(companion);
                    });


                    return;
                }

                if(elementalistAbilities.getElementalBreath().getIfBuffTime(companion) > 0){
                    if(elementalistAbilities.getDescendingInferno().usable(companion, target)){

                        Bukkit.getScheduler().runTask(main,()->{
                            elementalistAbilities.getDescendingInferno().use(companion);
                        });


                        return;
                    }
                }

                if(heat < 95){
                    if(elementalistAbilities.getDescendingInferno().usable(companion, target)){

                        Bukkit.getScheduler().runTask(main,()->{
                            elementalistAbilities.getDescendingInferno().use(companion);
                        });


                        return;
                    }

                    if(elementalistAbilities.getFieryMagma().usable(companion, target)){

                        Bukkit.getScheduler().runTask(main,()->{
                            elementalistAbilities.getFieryMagma().use(companion);
                        });


                        return;
                    }
                }

                if(heat < 85){
                    if(elementalistAbilities.getDragonBreathing().usable(companion, target)){

                        Bukkit.getScheduler().runTask(main,()->{
                            elementalistAbilities.getDragonBreathing().use(companion);
                        });


                        return;
                    }
                }

                if(elementalistAbilities.getWindWall().usable(companion)){
                    if(healthPercent<=50){


                        Bukkit.getScheduler().runTask(main,()->{
                            elementalistAbilities.getWindWall().use(companion);
                        });

                        return;
                    }
                }

                if(elementalistAbilities.getConjuringForce().usable(companion)){

                    Bukkit.getScheduler().runTask(main,()->{
                        elementalistAbilities.getConjuringForce().use(companion);
                    });


                    return;
                }

                Bukkit.getScheduler().runTask(main,()->{
                    elementalistAbilities.getElementalistBasic().use(companion);
                });



            }
        }.runTaskTimerAsynchronously(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());
    }

    public void stopAiTask(UUID uuid){

        //Bukkit.getLogger().info("stopping companion task for " + uuid + " due to despawn");

        Bukkit.getScheduler().runTask(main, () ->{
            if(aiTaskMap.containsKey(uuid)){
                aiTaskMap.get(uuid).cancel();
                profileManager.removeCompanionCombat(uuid);
            }

            Entity entity = Bukkit.getEntity(uuid);
            if(entity instanceof LivingEntity companion){
                abilityManager.interruptBasic(companion);
                profileManager.getAnyProfile(companion).setIfInCombat(false);


            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(main, ()->{
                aiTaskMap.remove(uuid);
            },20);


        });


        //Bukkit.getLogger().info("rotation stopped");


    }

    public void setCaution(LivingEntity entity, boolean caution){
        cautionMap.put(entity.getUniqueId(), caution);
    }
    public void setNeedToInterrupt(LivingEntity entity, boolean needs){needToInterrupt.put(entity.getUniqueId(),needs);}
    private void removeInterrupt(LivingEntity entity){needToInterrupt.remove(entity.getUniqueId());}
    private boolean getIfNeedToInterrupt(LivingEntity entity){return needToInterrupt.getOrDefault(entity.getUniqueId(), false);}

    private boolean getIfCautious(LivingEntity entity){
        return cautionMap.getOrDefault(entity.getUniqueId(), false);
    }



}
