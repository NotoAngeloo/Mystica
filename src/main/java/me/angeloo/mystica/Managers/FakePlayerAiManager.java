package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
import me.angeloo.mystica.Components.Abilities.RangerAbilities;
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
                case "ranger":{
                    startTamerRotation(companion);
                    break;
                }
                case "mystic":{
                    startShepardRotation(companion);
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

                if(!getIfCautious(companion)){
                    if(distance>=8){
                        if(paladinAbilities.getDuranceOfTruth().usable(companion)){
                            paladinAbilities.getDuranceOfTruth().use(companion);
                            return;
                        }
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

    private void startShepardRotation(LivingEntity companion){
        MysticAbilities mysticAbilities = abilityManager.getMysticAbilities();

        Player companionPlayer = profileManager.getCompanionsPlayer(companion);
        List<LivingEntity> companions = profileManager.getCompanions(companionPlayer);
        List<LivingEntity> fakeParty = new ArrayList<>(companions);
        fakeParty.add(companionPlayer);

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

                List<LivingEntity> liveParty = new ArrayList<>();
                List<LivingEntity> deadParty = new ArrayList<>();

                for(LivingEntity member : fakeParty){
                    if(profileManager.getAnyProfile(member).getIfDead()){
                        deadParty.add(member);
                        continue;
                    }
                    liveParty.add(member);
                }

                liveParty.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth()/(double)profileManager.getAnyProfile(p).getTotalHealth()));
                LivingEntity lowest = liveParty.get(0);


                LivingEntity boss = (LivingEntity) Bukkit.getEntity(targetManager.getBossTarget(companionPlayer));
                assert boss != null;

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
                        targetManager.setPlayerTarget(companion, lowest);
                        if(targetManager.getPlayerTarget(companion) == companion){
                            targetManager.setPlayerTarget(companion, null);
                        }
                        mysticAbilities.getAurora().use(companion);
                        return;
                    }

                }

                if(lowestHealthPercent<=50){
                    targetManager.setPlayerTarget(companion, lowest);
                    if(targetManager.getPlayerTarget(companion) == companion){
                        targetManager.setPlayerTarget(companion, null);
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


                targetManager.setPlayerTarget(companion, lowest);

                if(targetManager.getPlayerTarget(companion) == companion){
                    targetManager.setPlayerTarget(companion, null);
                }

                mysticAbilities.getMysticBasic().useBasic(companion);

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
