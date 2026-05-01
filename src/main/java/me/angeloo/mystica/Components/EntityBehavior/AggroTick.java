package me.angeloo.mystica.Components.EntityBehavior;

import me.angeloo.mystica.Components.CombatSystem.CombatManager;
import me.angeloo.mystica.Components.CombatSystem.DpsManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.BossManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class AggroTick {

    private final Mystica main;
    private final BossManager bossManager;
    private final AggroManager aggroManager;
    private final DpsManager dpsManager;
    private final CombatManager playerCombatManager;
    private final ProfileManager profileManager;

    private final Map<LivingEntity, BukkitTask> aggroTasks = new HashMap<>();

    public AggroTick(Mystica main){
        this.main = main;
        bossManager = main.getBossManager();
        aggroManager = main.getAggroManager();
        dpsManager = main.getDpsManager();
        playerCombatManager = main.getCombatManager();
        profileManager = main.getProfileManager();
    }

    public void startAggroTaskFor(LivingEntity entity){

        if(aggroTasks.containsKey(entity)){
            return;
        }

        BukkitTask aggroTask = new BukkitRunnable() {

            @Override
            public void run(){

                if(entity.isDead()){
                    this.cancel();
                    return;
                }

                List<LivingEntity> originalAttackerList = aggroManager.getAliveAttackers(entity);

                List<LivingEntity> attackers = new ArrayList<>();

                for(LivingEntity attacker : originalAttackerList){

                    Bukkit.getScheduler().runTask(main, () ->{
                        playerCombatManager.startCombatTimer(attacker);
                    });

                    boolean blacklist = aggroManager.getIfOnBlackList(attacker);
                    boolean deathStatus = profileManager.getAnyProfile(attacker).getIfDead();

                    if(!blacklist && !deathStatus){
                        attackers.add(attacker);
                    }
                }

                if(aggroManager.getHighPriorityTarget(entity) == null){

                    LivingEntity highestDpsPlayer = null;
                    double highestDps = 0;

                    if(!attackers.isEmpty()){

                        for(LivingEntity attacker : attackers){

                            double dps = dpsManager.getRawDps(attacker);
                            if(dps > highestDps){
                                highestDps = dps;
                                highestDpsPlayer = attacker;

                            }
                        }

                        if(highestDpsPlayer != null){
                            ((Creature) entity).setTarget(highestDpsPlayer);
                            aggroManager.setCurrentTarget(entity, highestDpsPlayer);
                        }
                    }
                }

                long currentTime = System.currentTimeMillis() / 1000;
                long lastSet = aggroManager.getLastSetAsPriority(entity);
                if(currentTime - lastSet > 30){
                    aggroManager.removeHighPriorityTarget(entity.getUniqueId());
                }

                LivingEntity highPriorityTarget = aggroManager.getHighPriorityTarget(entity);

                if(highPriorityTarget != null){
                    ((Creature) entity).setTarget(highPriorityTarget);
                    aggroManager.setCurrentTarget(entity, highPriorityTarget);
                }

                if(((Creature) entity).getTarget() != null){
                    LivingEntity targetedPlayer = ((Creature)entity).getTarget();


                    boolean deathStatus = profileManager.getAnyProfile(targetedPlayer).getIfDead();

                    boolean blackList = aggroManager.getIfOnBlackList(targetedPlayer);

                    if(deathStatus || blackList){
                        ((Creature) entity).setTarget(null);
                        aggroManager.setCurrentTarget(entity, null);
                    }else{

                        Bukkit.getScheduler().runTask(main, ()->{
                            playerCombatManager.startCombatTimer(targetedPlayer);
                        });


                    }

                }

                if(((Creature) entity).getTarget() != null){
                    LivingEntity targetedPlayer = ((Creature)entity).getTarget();

                    if(targetedPlayer.isDead()){
                        ((Creature) entity).setTarget(null);
                        aggroManager.setCurrentTarget(entity, null);
                    }

                }

                //ok set the new target if its null, meaning no highest dps nor priority
                if(((Creature) entity).getTarget() == null){


                    if(!attackers.isEmpty()){
                        Random random = new Random();
                        int index = random.nextInt(attackers.size());
                        ((Creature) entity).setTarget(attackers.get(index));
                        aggroManager.setCurrentTarget(entity, attackers.get(index));
                    }
                    else{

                        this.cancel();
                        aggroManager.clearAttackerList(entity);
                        aggroManager.clearLastPlayer(entity);
                        aggroManager.removeHighPriorityTarget(entity.getUniqueId());
                        aggroTasks.remove(entity);
                        aggroManager.setCurrentTarget(entity, null);

                        Bukkit.getScheduler().runTask(main, () -> {
                            bossManager.resetBoss(entity.getUniqueId());
                        });



                    }

                }

            }

        }.runTaskTimerAsynchronously(main, 0, 20);

        //.runTaskTimer(main, 0, 20);

        aggroTasks.put(entity, aggroTask);

    }

}
