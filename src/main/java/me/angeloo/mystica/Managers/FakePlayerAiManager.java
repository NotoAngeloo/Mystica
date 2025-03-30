package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Abilities.*;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.nio.file.LinkOption;
import java.util.*;


public class FakePlayerAiManager {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final AbilityManager abilityManager;

    private final Map<UUID, BukkitTask> aiTaskMap = new HashMap<>();

    private final Map<UUID, Boolean> cautionMap = new HashMap<>();
    private final Map<UUID, Boolean> needToInterrupt = new HashMap<>();

    public FakePlayerAiManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = main.getAbilityManager();
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

            switch (profileManager.getAnyProfile(companion).getPlayerClass().toLowerCase()){
                case "paladin":{
                    startTemplarRotation(companion);
                    break;
                }
                case "ranger":{
                    startTamerRotation(companion);
                    break;
                }
                case "mystic":{
                    startShepardRotation(companion);
                    break;
                }
                case "warrior":{
                    startExecutionerRotation(companion);
                    break;
                }
                case "elementalist":{
                    startConjurerRotation(companion);
                    break;
                }

            }
        }

    }

    private void startTamerRotation(LivingEntity companion){
        RangerAbilities rangerAbilities = abilityManager.getRangerAbilities();

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                //Bukkit.getLogger().info("ranger");

                if(companion.isDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(target.isDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }


                if(rangerAbilities.getWildRoar().usable(companion)){
                    rangerAbilities.getWildRoar().use(companion);
                    return;
                }

                if(rangerAbilities.getShadowCrows().usable(companion, target)){
                    rangerAbilities.getShadowCrows().use(companion);
                    return;
                }

                if(rangerAbilities.getWildSpirit().usable(companion)){
                    rangerAbilities.getWildSpirit().sendSignal(companion);
                    return;
                }

                if(rangerAbilities.getRallyingCry().getIfBuffTime(companion)>0){
                    if(rangerAbilities.getBlessedArrow().usable(companion, target)){
                        rangerAbilities.getBlessedArrow().use(companion);
                        return;
                    }

                    rangerAbilities.getRangerBasic().useBasic(companion);
                    return;
                }

                if(rangerAbilities.getRelentless().usable(companion, target)){
                    rangerAbilities.getRelentless().use(companion);
                    return;
                }

                if(rangerAbilities.getRazorWind().usable(companion, target)){
                    rangerAbilities.getRazorWind().use(companion);
                    return;
                }


                if(rangerAbilities.getRallyingCry().usable(companion)){
                    rangerAbilities.getRallyingCry().use(companion);
                    return;
                }

                if(rangerAbilities.getBlessedArrow().usable(companion, target)){
                    rangerAbilities.getBlessedArrow().use(companion);
                    return;
                }


                if(rangerAbilities.getBitingRain().usable(companion, target)){
                    rangerAbilities.getBitingRain().use(companion);
                }

                rangerAbilities.getRangerBasic().useBasic(companion);


            }
        }.runTaskTimer(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());
    }

    private void startTemplarRotation(LivingEntity companion){

        PaladinAbilities paladinAbilities = abilityManager.getPaladinAbilities();

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                //Bukkit.getLogger().info("paladin");

                if(companion.isDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(target.isDead()){
                    stopAiTask(companion.getUniqueId());
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

                if(healthPercent<=50){
                    if(paladinAbilities.getDivineGuidance().usable(companion)){
                        paladinAbilities.getDivineGuidance().use(companion);
                        return;
                    }
                }
                else{
                    if(distance<=5){
                        if(paladinAbilities.getDivineGuidance().usable(companion)){
                            paladinAbilities.getDivineGuidance().use(companion);
                            return;
                        }
                    }
                }

                if(!getIfCautious(companion)){
                    if(distance<=8){
                        if(paladinAbilities.getDuranceOfTruth().usable(companion)){
                            paladinAbilities.getDuranceOfTruth().use(companion);
                            return;
                        }
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
        profileManager.setCompanionCombat(companion.getUniqueId());

    }

    private void startShepardRotation(LivingEntity companion){
        MysticAbilities mysticAbilities = abilityManager.getMysticAbilities();

        Player companionPlayer = profileManager.getCompanionsPlayer(companion);
        List<LivingEntity> companions = profileManager.getCompanions(companionPlayer);
        List<LivingEntity> fakeParty = new ArrayList<>(companions);
        fakeParty.add(companionPlayer);

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                //Bukkit.getLogger().info("mystic");

                if(companion.isDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                List<LivingEntity> liveParty = new ArrayList<>();

                for(LivingEntity member : fakeParty){
                    if(profileManager.getAnyProfile(member).getIfDead()){
                        continue;
                    }
                    liveParty.add(member);
                }

                liveParty.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth()/(double)profileManager.getAnyProfile(p).getTotalHealth()));
                LivingEntity lowest = liveParty.get(0);


                double base = 0;
                for(LivingEntity member : fakeParty){
                    base += ((profileManager.getAnyProfile(member).getCurrentHealth()/(double)profileManager.getAnyProfile(member).getTotalHealth()) * 100);
                }
                double averagePhp = base / fakeParty.size();

                //Bukkit.getLogger().info(String.valueOf(averagePhp));

                double lowestHealthPercent = profileManager.getAnyProfile(lowest).getCurrentHealth()/(double)profileManager.getAnyProfile(lowest).getTotalHealth();


                if(averagePhp <= 75){
                    if(mysticAbilities.getEnlightenment().usable(companion)){
                        mysticAbilities.getEnlightenment().use(companion);
                        return;
                    }

                    if(mysticAbilities.getPurifyingBlast().getInstantCast(companion)){
                        if(mysticAbilities.getPurifyingBlast().usable(companion)){
                            mysticAbilities.getPurifyingBlast().use(companion);
                            return;
                        }
                    }
                }

                if(averagePhp <= 50){
                    if(mysticAbilities.getAurora().usable(companion, lowest)){
                        fakePlayerTargetManager.setFakePlayerTarget(companion, lowest);

                        if(fakePlayerTargetManager.getTarget(companion) == companion){
                            fakePlayerTargetManager.setFakePlayerTarget(companion, null);
                        }
                        mysticAbilities.getAurora().use(companion);
                        return;
                    }

                }

                if(lowestHealthPercent<=50){
                    fakePlayerTargetManager.setFakePlayerTarget(companion, lowest);

                    if(fakePlayerTargetManager.getTarget(companion) == companion){
                        fakePlayerTargetManager.setFakePlayerTarget(companion, null);
                    }
                    if(mysticAbilities.getArcaneShield().usable(companion, lowest)){
                        mysticAbilities.getArcaneShield().use(companion);
                        return;
                    }
                }

                if(averagePhp <= 75){
                    if(mysticAbilities.getLightSigil().usable(companion)){
                        mysticAbilities.getLightSigil().use(companion);
                        return;
                    }
                }


                fakePlayerTargetManager.setFakePlayerTarget(companion, lowest);

                if(fakePlayerTargetManager.getTarget(companion) == companion){
                    fakePlayerTargetManager.setFakePlayerTarget(companion, null);
                }

                mysticAbilities.getMysticBasic().useBasic(companion);

                fakePlayerTargetManager.setFakePlayerTarget(companion, lowest);

            }
        }.runTaskTimer(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());
    }

    private void startExecutionerRotation(LivingEntity companion){

        WarriorAbilities warriorAbilities = abilityManager.getWarriorAbilities();

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                //Bukkit.getLogger().info("warrior");

                if(companion.isDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(target.isDead()){
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
                            warriorAbilities.getMeteorCrater().use(companion);
                            removeInterrupt(companion);
                            return;
                        }

                    }
                }



                if(warriorAbilities.getFlamingSigil().usable(companion)){
                    warriorAbilities.getFlamingSigil().use(companion);
                    return;
                }

                if(distance<8){
                    if(warriorAbilities.getTempestRage().usable(companion)){
                        warriorAbilities.getTempestRage().use(companion);
                        return;
                    }

                    if(warriorAbilities.getLavaQuake().usable(companion)){
                        warriorAbilities.getLavaQuake().use(companion);
                        return;
                    }
                }

                if(!getIfCautious(companion)){
                    if(distance<5){
                        if(warriorAbilities.getMagmaSpikes().usable(companion)){
                            warriorAbilities.getMagmaSpikes().use(companion);
                            return;
                        }
                    }
                }


                if(!getIfCautious(companion)){
                    if(warriorAbilities.getDeathGaze().usable(companion, target)){
                        warriorAbilities.getDeathGaze().use(companion);
                    }
                }


                if(!getIfCautious(companion)){
                    if(distance>=8 && distance < 15){
                        if(warriorAbilities.getAnvilDrop().usable(companion)){
                            warriorAbilities.getAnvilDrop().use(companion);
                            return;
                        }
                    }
                }

                if(!getIfCautious(companion)){
                    if(distance<5){
                        warriorAbilities.getWarriorBasic().useBasic(companion);
                    }
                }

            }
        }.runTaskTimer(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());
    }

    private void startConjurerRotation(LivingEntity companion){

        ElementalistAbilities elementalistAbilities = abilityManager.getElementalistAbilities();

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                //Bukkit.getLogger().info("elementalist");

                if(companion.isDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }

                //check goal before casting skills, they may want to run

                LivingEntity target = fakePlayerTargetManager.getTarget(companion);

                if(target.isDead()){
                    stopAiTask(companion.getUniqueId());
                    return;
                }


                int heat = elementalistAbilities.getHeat().getHeat(companion);

                int healthPercent = (int) Math.round((profileManager.getAnyProfile(companion).getCurrentHealth() / (double) profileManager.getAnyProfile(companion).getTotalHealth()) * 100);

                double distance = target.getLocation().distance(companion.getLocation());

                if(distance <=10){
                    if(elementalistAbilities.getConjuringForce().usable(companion)){
                        elementalistAbilities.getConjuringForce().use(companion);
                        return;
                    }
                }


                if(elementalistAbilities.getElementalBreath().usable(companion)){
                    elementalistAbilities.getElementalBreath().use(companion);
                    return;
                }

                if(elementalistAbilities.getElemental_matrix().usable(companion, target)){
                    elementalistAbilities.getElemental_matrix().use(companion);
                    return;
                }

                if(elementalistAbilities.getIceBolt().usable(companion, target)){
                    elementalistAbilities.getIceBolt().use(companion);
                    return;
                }

                if(elementalistAbilities.getElementalBreath().getIfBuffTime(companion) > 0){
                    if(elementalistAbilities.getDescendingInferno().usable(companion, target)){
                        elementalistAbilities.getDescendingInferno().use(companion);
                        return;
                    }
                }

                if(heat < 95){
                    if(elementalistAbilities.getDescendingInferno().usable(companion, target)){
                        elementalistAbilities.getDescendingInferno().use(companion);
                        return;
                    }

                    if(elementalistAbilities.getFieryMagma().usable(companion, target)){
                        elementalistAbilities.getFieryMagma().use(companion);
                        return;
                    }
                }

                if(heat < 85){
                    if(elementalistAbilities.getDragonBreathing().usable(companion, target)){
                        elementalistAbilities.getDragonBreathing().use(companion);
                        return;
                    }
                }

                if(elementalistAbilities.getWindWall().usable(companion)){
                    if(healthPercent<=50){
                        elementalistAbilities.getWindWall().use(companion);
                        return;
                    }
                }

                if(elementalistAbilities.getConjuringForce().usable(companion)){
                    elementalistAbilities.getConjuringForce().use(companion);
                    return;
                }

                elementalistAbilities.getElementalistBasic().use(companion);

            }
        }.runTaskTimer(main, 0, 10);

        aiTaskMap.put(companion.getUniqueId(), task);
        profileManager.setCompanionCombat(companion.getUniqueId());
    }

    public void stopAiTask(UUID uuid){

        if(aiTaskMap.containsKey(uuid)){
            aiTaskMap.get(uuid).cancel();
            profileManager.removeCompanionCombat(uuid);
        }

        Entity entity = Bukkit.getEntity(uuid);
        if(entity instanceof LivingEntity){
            LivingEntity companion = (LivingEntity) entity;
            abilityManager.interruptBasic(companion);
        }
        //Bukkit.getLogger().info("rotation stopped");

        aiTaskMap.remove(uuid);
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

    public boolean getIfRotationRunning(LivingEntity companion){

        return aiTaskMap.containsKey(companion.getUniqueId());
    }


}
