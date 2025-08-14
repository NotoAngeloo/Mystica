package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.Managers.AggroManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.DpsManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class AggroTick {

    private final Mystica main;
    private final AggroManager aggroManager;
    private final DpsManager dpsManager;
    private final CombatManager playerCombatManager;
    private final ProfileManager profileManager;

    private final Map<LivingEntity, BukkitTask> aggroTasks = new HashMap<>();

    public AggroTick(Mystica main){
        this.main = main;
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

                /*LivingEntity targetedEntity = ((Creature) entity).getTarget();
                assert targetedEntity != null;
                Bukkit.getLogger().info(targetedEntity.getName());*/

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

                //Bukkit.getLogger().info("attackers " + attackers);

                if(aggroManager.getHighPriorityTarget(entity) == null){

                    LivingEntity highestDpsPlayer = null;
                    double highestDps = 0;

                    if(!attackers.isEmpty()){

                        //Bukkit.getLogger().info(String.valueOf(attackers));

                        for(LivingEntity attacker : attackers){


                            double dps = dpsManager.getRawDps(attacker);
                            //Bukkit.getLogger().info(String.valueOf(dps));
                            if(dps > highestDps){
                                highestDps = dps;
                                highestDpsPlayer = attacker;

                            }
                        }

                        if(highestDpsPlayer != null){
                            ((Creature) entity).setTarget(highestDpsPlayer);

                            //Bukkit.getLogger().info("setting target to highest dps player " + highestDpsPlayer.getName());
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
                    //Bukkit.getLogger().info("setting target to priority player " + highPriorityTarget.getName());
                }

                if(((Creature) entity).getTarget() != null){
                    LivingEntity targetedPlayer = ((Creature)entity).getTarget();

                    //Bukkit.getLogger().info(targetedPlayer.getName());

                    boolean deathStatus = profileManager.getAnyProfile(targetedPlayer).getIfDead();

                    boolean blackList = aggroManager.getIfOnBlackList(targetedPlayer);

                    if(deathStatus || blackList){
                        ((Creature) entity).setTarget(null);
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
                    }

                }

                //ok set the new target if its null, meaning no highest dps nor priority
                if(((Creature) entity).getTarget() == null){

                    //Bukkit.getLogger().info("target null");

                    if(!attackers.isEmpty()){
                        Random random = new Random();
                        int index = random.nextInt(attackers.size());
                        ((Creature) entity).setTarget(attackers.get(index));

                        //Bukkit.getLogger().info("Setting random target to " + attackers.get(index));
                    }
                    else{

                        this.cancel();
                        aggroManager.clearAttackerList(entity);
                        aggroManager.clearLastPlayer(entity);
                        aggroManager.removeHighPriorityTarget(entity.getUniqueId());
                        aggroTasks.remove(entity);

                        Bukkit.getScheduler().runTask(main, () -> {
                            profileManager.resetBoss(entity.getUniqueId());
                        });



                    }

                }

            }

        }.runTaskTimerAsynchronously(main, 0, 20);

        //.runTaskTimer(main, 0, 20);

        aggroTasks.put(entity, aggroTask);

    }

}
