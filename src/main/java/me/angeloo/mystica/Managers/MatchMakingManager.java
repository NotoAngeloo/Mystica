package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Inventories.MatchmakingInventory;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DungeonQueue;
import me.angeloo.mystica.Utility.MysticaParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class MatchMakingManager {

    private final Mystica main;

    private final MysticaPartyManager mysticaPartyManager;
    private final MatchmakingInventory matchmakingInventory;

    private final Map<String, DungeonQueue> dungeonQueueMap = new HashMap<>();
    private final Map<String, List<MysticaParty>> teamQueueList = new HashMap<>();
    //private final List<MysticaParty> teameQueueList = new ArrayList<>();

    public MatchMakingManager(Mystica main){
        this.main = main;
        mysticaPartyManager = main.getMysticaPartyManager();
        matchmakingInventory = new MatchmakingInventory(main, this);
    }

    public void matchMakeReadyCheck(Player player, String dungeon, boolean bots){

        List<Player> joiners = new ArrayList<>(mysticaPartyManager.getPartyPlayers(player));
        MysticaParty mParty = mysticaPartyManager.getPlayerMParty(player);

        if(mParty.numberRoleSelected() < joiners.size()){
            return;
        }

        //open the new inventory
        for(Player partyPlayer : joiners){
            partyPlayer.openInventory(matchmakingInventory.openMatchFound(dungeon, joiners, bots));
        }

    }

    /*public void matchMakeReadyCheck(Player player, String dungeon, boolean bots){

        Set<Player> partyPlayers = mysticaPartyManager.getPartyPlayers(player);
        MysticaParty mParty = mysticaPartyManager.getPlayerMParty(player);

        //Bukkit.getLogger().info("Party has " + partyPlayers.size());
        //Bukkit.getLogger().info("MParty has " + mParty.numberRoleSelected());

        if(mParty.numberRoleSelected() < partyPlayers.size()){
            return;
        }

        if(bots){
            //need to check if this does for all the players
            //Mystica.dungeonsApi().initiateDungeonForPlayer(player, dungeon);
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
        addToTeamQueue(mParty, dungeon);
        //a runnable to show time in queue

    }*/

    private void addToTeamQueue(MysticaParty mParty, String dungeon){
        List<MysticaParty> mParties = new ArrayList<>(teamQueueList.getOrDefault(dungeon, new ArrayList<>()));
        mParties.add(mParty);
        teamQueueList.put(dungeon, mParties);
    }

    public void cancelTeamMatchmaking(MysticaParty mParty){
        for(Map.Entry<String, List<MysticaParty>> mysticaPartyEntry : teamQueueList.entrySet()){
            mysticaPartyEntry.getValue().remove(mParty);
        }
    }

    public void cancelSoloMatchmaking(Player player){
        for(Map.Entry<String, DungeonQueue> queue : dungeonQueueMap.entrySet()){
            queue.getValue().removeTankQueue(player);
            queue.getValue().removeHealQueue(player);
            queue.getValue().removeDamageQueue(player);
        }
    }

    private MysticaParty getFirstValidMParty(String role, String dungeon){

        List<MysticaParty> partyList = new ArrayList<>(teamQueueList.getOrDefault(dungeon, new ArrayList<>()));

        for(MysticaParty mParty : partyList){

            if(role.equalsIgnoreCase("tank") && !mParty.hasTank()){
                return mParty;
            }

            if(role.equalsIgnoreCase("heal") &&  !mParty.hasHeal()){
                return mParty;
            }

            if(role.equalsIgnoreCase("damage") && mParty.needsDamage()){
                return mParty;
            }
        }

        return null;
    }

    public void queueTank(Player player, String dungeon){
        mysticaPartyManager.removeMParty(player);
        cancelSoloMatchmaking(player);
        DungeonQueue queue = getDungeonQueue(dungeon);
        queue.joinTankQueue(player);
        checkIfMatchFound(dungeon);
    }

    public void queueDamage(Player player, String dungeon){
        mysticaPartyManager.removeMParty(player);
        cancelSoloMatchmaking(player);
        DungeonQueue queue = getDungeonQueue(dungeon);
        queue.joinDamageQueue(player);
        checkIfMatchFound(dungeon);
    }

    public void queueHeal(Player player, String dungeon){
        mysticaPartyManager.removeMParty(player);
        cancelSoloMatchmaking(player);
        DungeonQueue queue = getDungeonQueue(dungeon);
        queue.joinHealQueue(player);
        checkIfMatchFound(dungeon);
    }


    private void checkIfMatchFound(String dungeon){

        DungeonQueue queue = getDungeonQueue(dungeon);

        Bukkit.getLogger().info("tanks queued " + queue.getTankPlayers());
        Bukkit.getLogger().info("heal queued " + queue.getHealPlayers());
        Bukkit.getLogger().info("damage queued " + queue.getDamagePlayers());
        //perhaps have a confirm screen

        //check solo players
        if(queue.hasEnoughTanks() && queue.hasEnoughHeal() && queue.hasEnoughDamage()){

            Player tank = queue.getFirstTank();
            Player heal = queue.getFirstHeal();
            List<Player> damagePlayers = new ArrayList<>(queue.getDamagePlayers());
            Player damage1 = damagePlayers.get(0);
            Player damage2 = damagePlayers.get(1);
            Player damage3 = damagePlayers.get(2);



            mysticaPartyManager.createPartyFromMatchmaking(tank, heal, damage1, damage2, damage3);

            Mystica.dungeonsApi().initiateDungeonForPlayer(tank, dungeon);

        }

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

    public MatchmakingInventory getMatchmakingInventory(){
        return matchmakingInventory;
    }

}
