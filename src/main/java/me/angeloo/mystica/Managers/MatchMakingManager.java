package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DungeonQueue;
import me.angeloo.mystica.Utility.MysticaParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MatchMakingManager {

    private final Mystica main;

    private final MysticaPartyManager mysticaPartyManager;

    private final Map<String, DungeonQueue> dungeonQueueMap = new HashMap<>();

    public MatchMakingManager(Mystica main){
        this.main = main;
        mysticaPartyManager = main.getMysticaPartyManager();
    }

    public void matchMakeReadyCheck(Player player, String dungeon, boolean bots){

        Set<Player> partyPlayers = mysticaPartyManager.getPartyPlayers(player);
        MysticaParty mParty = mysticaPartyManager.getPlayerMParty(player);

        //Bukkit.getLogger().info("Party has " + partyPlayers.size());
        //Bukkit.getLogger().info("MParty has " + mParty.numberRoleSelected());

        if(mParty.numberRoleSelected() < partyPlayers.size()){
            return;
        }

        if(bots){
            //need to check if this does for all the players
            Mystica.dungeonsApi().initiateDungeonForPlayer(player, dungeon);
            //then wait for the dungeon to be available

            //which means they clicked bots while having max people
            if(mParty.numberRoleSelected() == 5 ){
                return;
            }

            Player leader = mParty.getLeader();
            new BukkitRunnable(){
                @Override
                public void run(){

                    if(Mystica.dungeonsApi().isPlayerInDungeon(leader)){
                        //then summon needed bots

                        boolean lunaSpawned = false;
                        boolean darwinSpawned = false;
                        boolean slippySpawned = false;

                        while (mParty.numberRoleSelected() < 5){

                            //Bukkit.getLogger().info("Role selected " + mParty.numberRoleSelected());

                            if(!mParty.hasHeal()){

                                Entity companion;

                                try {
                                    companion = MythicBukkit.inst().getAPIHelper().spawnMythicMob("Wings", leader.getLocation());
                                } catch (InvalidMobTypeException e) {
                                    break;
                                }

                                mParty.addOrChangeMemberRole((LivingEntity) companion, "heal");
                                continue;
                            }

                            if(!mParty.hasTank()){

                                Entity companion;

                                try {
                                    companion = MythicBukkit.inst().getAPIHelper().spawnMythicMob("SammingSins", leader.getLocation());
                                } catch (InvalidMobTypeException e) {
                                    break;
                                }

                                mParty.addOrChangeMemberRole((LivingEntity) companion, "tank");
                                continue;
                            }

                            //in future, maybe pick random companions when more than 3 total
                            if(!lunaSpawned){

                                Entity companion;

                                try {
                                    companion = MythicBukkit.inst().getAPIHelper().spawnMythicMob("Luna", leader.getLocation());
                                } catch (InvalidMobTypeException e) {
                                    break;
                                }

                                mParty.addOrChangeMemberRole((LivingEntity) companion, "damage");
                                lunaSpawned = true;
                                continue;
                            }

                            if(!darwinSpawned){

                                Entity companion;

                                try {
                                    companion = MythicBukkit.inst().getAPIHelper().spawnMythicMob("Darwin", leader.getLocation());
                                } catch (InvalidMobTypeException e) {
                                    break;
                                }

                                mParty.addOrChangeMemberRole((LivingEntity) companion, "damage");
                                darwinSpawned = true;
                                continue;
                            }

                            if(!slippySpawned){

                                Entity companion;

                                try {
                                    companion = MythicBukkit.inst().getAPIHelper().spawnMythicMob("Slippy", leader.getLocation());
                                } catch (InvalidMobTypeException e) {
                                    break;
                                }

                                mParty.addOrChangeMemberRole((LivingEntity) companion, "damage");
                                slippySpawned = true;
                                continue;
                            }

                        }

                        this.cancel();
                    }

                }
            }.runTaskTimer(main, 20, 20);


            return;
        }


        //enter matchmake

    }




    public void queueTank(Player player, String dungeon, boolean bots){

        if(bots){
            //get mparty, then fill rest with bots. instant enter

            MysticaParty mParty = mysticaPartyManager.getPlayerMParty(player);

            Bukkit.getLogger().info(String.valueOf(mParty));
            //Mystica.dungeonsApi().initiateDungeonForPlayer(player, dungeon);
            return;
        }

    }

    public void queueDamage(Player player, String dungeon, boolean bots){

        if(bots){
            return;
        }
    }

    public void queueHeal(Player player, String dungeon, boolean bots){

        if(bots){
            return;
        }
    }

    public void unQueuePlayer(Player player){

    }

    private void checkIfMatchFound(Player player){

    }

    private void addNewDungeonQueue(String dungeonName){
        dungeonQueueMap.put(dungeonName, new DungeonQueue(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    private DungeonQueue getDungeonQueue(String dungeonName){

        if(!dungeonQueueMap.containsKey(dungeonName)){
            addNewDungeonQueue(dungeonName);
        }

        return dungeonQueueMap.get(dungeonName);
    }

}
