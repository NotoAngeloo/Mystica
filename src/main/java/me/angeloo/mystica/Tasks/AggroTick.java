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

        /*boolean alwaysPassive = profileManager.getAnyProfile(entity).getPassivity().getAlwaysPassive();

        if(alwaysPassive){
            return;
        }*/

        BukkitTask aggroTask = new BukkitRunnable() {

            @Override
            public void run(){

                if(entity.isDead()){
                    this.cancel();
                    return;
                }

                //LivingEntity targetedEntity = ((Creature) entity).getTarget();

                List<Player> originalAttackerList = aggroManager.getAttackerList(entity);

                List<Player> attackers = new ArrayList<>();

                for(Player player : originalAttackerList){
                    boolean blacklist = aggroManager.getIfOnBlackList(player);

                    if(!blacklist){
                        attackers.add(player);
                    }
                }

                if(aggroManager.getHighPriorityTarget(entity) == null){

                    Player highestDpsPlayer = null;
                    double highestDps = 0;

                    if(!attackers.isEmpty()){

                        //Bukkit.getLogger().info(String.valueOf(attackers));

                        for(Player player : attackers){


                            double dps = dpsManager.getRawDps(player);
                            //Bukkit.getLogger().info(String.valueOf(dps));
                            if(dps > highestDps){
                                highestDps = dps;
                                highestDpsPlayer = player;

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
                if(currentTime - lastSet > 20){
                    aggroManager.removeHighPriorityTarget(entity.getUniqueId());
                }

                Player highPriorityTarget = aggroManager.getHighPriorityTarget(entity);

                if(highPriorityTarget != null){
                    ((Creature) entity).setTarget(highPriorityTarget);
                    //Bukkit.getLogger().info("setting target to priority player " + highPriorityTarget.getName());
                }

                if(((Creature) entity).getTarget() instanceof Player){
                    Player targetedPlayer = (Player) ((Creature) entity).getTarget();

                    boolean deathStatus = profileManager.getAnyProfile(targetedPlayer).getIfDead();

                    boolean blackList = aggroManager.getIfOnBlackList(targetedPlayer);

                    if(deathStatus || blackList){
                        ((Creature) entity).setTarget(null);
                    }else{
                        playerCombatManager.startCombatTimer(targetedPlayer);
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

                        if(profileManager.resetBoss(entity.getUniqueId())){

                            this.cancel();

                            aggroManager.clearAttackerList(entity);
                            aggroManager.clearLastPlayer(entity);
                            aggroManager.removeHighPriorityTarget(entity.getUniqueId());

                            aggroTasks.remove(entity);
                        }

                    }

                }

            }

        }.runTaskTimer(main, 0, 20);

        aggroTasks.put(entity, aggroTask);

    }

}
