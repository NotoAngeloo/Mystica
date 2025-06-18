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
import org.bukkit.scheduler.BukkitTask;

import java.util.*;


public class MatchMakingManager {

    private final Mystica main;

    private final MysticaPartyManager mysticaPartyManager;
    private final MatchmakingInventory matchmakingInventory;

    private final Map<String, DungeonQueue> dungeonQueueMap = new HashMap<>();
    private final Map<String, List<MysticaParty>> teamQueueList = new HashMap<>();
    //private final List<MysticaParty> teameQueueList = new ArrayList<>();

    private final Map<MysticaParty, Integer> amountConfirmEnter = new HashMap<>();

    private final Map<UUID, BukkitTask> taskMap = new HashMap<>();

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

    public void matchMakeConfirmEnter(Player player, String dungeon, boolean bots){

        Set<Player> partyPlayers = mysticaPartyManager.getPartyPlayers(player);
        int needToConfirm = partyPlayers.size();
        MysticaParty mParty = mysticaPartyManager.getPlayerMParty(player);

        if(getAmountConfirmed(mParty) == 0){
            amountConfirmEnter.put(mParty, 1);
            Bukkit.getLogger().info("nobody confirm yet, creating counter");
        }else{
            addConfirmEnter(mParty);
            Bukkit.getLogger().info("adding to counter");
        }

        if(needToConfirm!=getAmountConfirmed(mParty)){
            return;
        }

        Bukkit.getLogger().info("all required players confirmed");
        amountConfirmEnter.remove(mParty);

        if(bots){
            //need to check if this does for all the players
            Mystica.dungeonsApi().initiateDungeonForPlayer(player, dungeon);

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
        //addToTeamQueue(mParty, dungeon);
        //a runnable to show time in queue
    }

    private void addConfirmEnter(MysticaParty mParty){
        int amount = getAmountConfirmed(mParty);
        amount++;
        amountConfirmEnter.put(mParty, amount);
    }

    private int getAmountConfirmed(MysticaParty mParty){

        if(amountConfirmEnter.containsKey(mParty)){
            return amountConfirmEnter.get(mParty);
        }

        return 0;
    }

    public void cancelEnterDungeon(Player player){
        MysticaParty mParty = mysticaPartyManager.getPlayerMParty(player);
        Set<Player> partyPlayers = mysticaPartyManager.getPartyPlayers(player);

        for(Player partyPlayer : partyPlayers){
            partyPlayer.sendMessage(player.getName() + " canceled entering dungeon");
        }

        amountConfirmEnter.remove(mParty);
    }



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

    public void cancelMatchmaking(Player player){
        for(Map.Entry<String, DungeonQueue> queue : dungeonQueueMap.entrySet()){
            queue.getValue().removeTankQueue(player);
            queue.getValue().removeHealQueue(player);
            queue.getValue().removeDamageQueue(player);
        }

        if(taskMap.containsKey(player.getUniqueId())){
            taskMap.get(player.getUniqueId()).cancel();
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
        cancelMatchmaking(player);
        DungeonQueue queue = getDungeonQueue(dungeon);
        queue.joinTankQueue(player);
        player.sendMessage("Successfully joined queue");
        startQueueTimer(player);

        if(checkIfSoloMatchFound(dungeon)){
            return;
        }

    }

    public void queueDamage(Player player, String dungeon){
        mysticaPartyManager.removeMParty(player);
        cancelMatchmaking(player);
        DungeonQueue queue = getDungeonQueue(dungeon);
        queue.joinDamageQueue(player);
        player.sendMessage("Successfully joined queue");
        startQueueTimer(player);

        if(checkIfSoloMatchFound(dungeon)){
            return;
        }
    }

    public void queueHeal(Player player, String dungeon){
        mysticaPartyManager.removeMParty(player);
        cancelMatchmaking(player);
        DungeonQueue queue = getDungeonQueue(dungeon);
        queue.joinHealQueue(player);
        player.sendMessage("Successfully joined queue");
        startQueueTimer(player);

        if(checkIfSoloMatchFound(dungeon)){
            return;
        }

    }

    private boolean checkIfSoloMatchFound(String dungeon){

        DungeonQueue queue = getDungeonQueue(dungeon);

        /*Bukkit.getLogger().info("tanks queued " + queue.getTankPlayers());
        Bukkit.getLogger().info("heal queued " + queue.getHealPlayers());
        Bukkit.getLogger().info("damage queued " + queue.getDamagePlayers());*/

        //check solo players
        if(queue.hasEnoughTanks() && queue.hasEnoughHeal() && queue.hasEnoughDamage()){

            Player tank = queue.getFirstTank();
            Player heal = queue.getFirstHeal();
            List<Player> damagePlayers = new ArrayList<>(queue.getDamagePlayers());
            Player damage1 = damagePlayers.get(0);
            Player damage2 = damagePlayers.get(1);
            Player damage3 = damagePlayers.get(2);

            List<Player> joiners = new ArrayList<>();
            joiners.add(tank);
            joiners.add(heal);
            joiners.addAll(damagePlayers);

            for(Player joiner : joiners){
                cancelMatchmaking(joiner);
            }

            mysticaPartyManager.createPartyFromMatchmaking(tank, heal, damage1, damage2, damage3);

            //do the enter confirm thing
            matchmakingInventory.openMatchFound(dungeon, joiners, false);

            //Mystica.dungeonsApi().initiateDungeonForPlayer(tank, dungeon);

            return true;
        }

        return false;
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

    private void startQueueTimer(Player player){

        /*Scoreboard queueTimer = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = queueTimer.registerNewObjective("", "dummy", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = objective.getScore("Time in queue:");
        score.setScore(0);
        player.setScoreboard(queueTimer);

        BukkitTask queueBoard = new BukkitRunnable(){

            @Override
            public void run(){
                score.setScore(score.getScore() + 1);
            }


        }.runTaskTimer(main, 0, 20);

        taskMap.put(player.getUniqueId(), queueBoard);*/
    }

}
