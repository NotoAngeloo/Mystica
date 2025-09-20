package me.angeloo.mystica.Utility.MatchMaking;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.Parties.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.Dungeon;
import me.angeloo.mystica.Utility.Enums.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class MatchMakingManager {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final CustomInventoryManager customInventoryManager;
    private final MysticaPartyManager mysticaPartyManager;

    private final Map<Dungeon, MatchMaker> dungeonQueues = new EnumMap<>(Dungeon.class);
    private final Map<UUID, Dungeon> playerQueueMap = new HashMap<>();

    public MatchMakingManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        customInventoryManager = main.getInventoryManager();
        mysticaPartyManager = main.getMysticaPartyManager();

        for(Dungeon dungeon : Dungeon.values()){
            dungeonQueues.put(dungeon, new MatchMaker());
        }
    }

    public void matchMake(Player player){

        Player leaderPlayer = mysticaPartyManager.getLeaderPlayer(player);

        if(player != leaderPlayer){
            return;
        }

        Dungeon dungeon;

        switch (customInventoryManager.getDungeonIndex(player)) {
            case 0 -> {
                dungeon = Dungeon.Heart_of_Corruption;
            }
            case 1 -> {
                dungeon = Dungeon.Acolyte_of_Chaos;
            }
            case 2 -> {
                dungeon = Dungeon.Cave_of_Lindwyrm;
            }
            case 3 -> {
                dungeon = Dungeon.Curse_of_Shadow;
            }
            default -> {
                return;
            }
        }

        MatchMaker matchMaker = dungeonQueues.get(dungeon);

        if(matchMaker == null){
            Bukkit.getLogger().info("queue for " + dungeon.name() + " doesn't exist");
            return;
        }


        List<MatchMakingPlayer> mPlayers = new ArrayList<>();
        for(LivingEntity entity : mysticaPartyManager.getMysticaParty(leaderPlayer)){

            if(playerQueueMap.containsKey(entity.getUniqueId())){
                Dungeon previousDungeon = playerQueueMap.get(entity.getUniqueId());
                MatchMaker previousMatchMaker =  dungeonQueues.get(previousDungeon);

                if(previousMatchMaker != null){
                    previousMatchMaker.removePlayerFromQueue(entity.getUniqueId());
                }

            }

            playerQueueMap.put(entity.getUniqueId(), dungeon);
            Role role = customInventoryManager.getRole(entity);
            mPlayers.add(new MatchMakingPlayer(entity.getUniqueId(), role));
        }

        MatchMakingParty mParty = new MatchMakingParty(mPlayers);

        dungeonQueues.get(dungeon).addToQueue(mParty);


    }

    //find instances in which to use this. ie: player leaves party.
    public void removePlayerFromAllQueues(UUID playerId) {
        Dungeon dungeon = playerQueueMap.remove(playerId);
        if (dungeon != null) {
            MatchMaker matchmaker = dungeonQueues.get(dungeon);
            if (matchmaker != null) {
                matchmaker.removePlayerFromQueue(playerId);
            }
        }
    }


    public void fillWithBots(Player player){

        //make the players enter, when all entered, fill with bots

        Player leaderPlayer = mysticaPartyManager.getLeaderPlayer(player);
        Set<Player> partyPlayers = mysticaPartyManager.getPlayerParty(player);
        List<LivingEntity> mParty = mysticaPartyManager.getMysticaParty(leaderPlayer);

        boolean dungeonRequiresInterrupt = false;

        //does sending the leader send all others?
        switch (customInventoryManager.getDungeonIndex(player)) {
            case 0 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + Dungeon.Heart_of_Corruption.name() + " " + player.getName());
                dungeonRequiresInterrupt = true;
                player.closeInventory();
            }
            case 1 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + Dungeon.Acolyte_of_Chaos.name() + " " + player.getName());
                player.closeInventory();
            }
            case 2 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + Dungeon.Cave_of_Lindwyrm.name() + " " + player.getName());
                player.closeInventory();
            }
            case 3 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + Dungeon.Curse_of_Shadow.name() + " " + player.getName());
                player.closeInventory();
            }
        }

        //get what roles are filled
        boolean tank = false;
        boolean healer = false;

        for (Player member : partyPlayers){
            if(customInventoryManager.getRole(member).equals(Role.Tank)){
                tank = true;
            }

            if(customInventoryManager.getRole(member).equals(Role.Healer)){
                healer = true;
            }
        }

        List<String> dpsCompanionNames = new ArrayList<>();
        dpsCompanionNames.add("Darwin");
        dpsCompanionNames.add("Luna");
        dpsCompanionNames.add("Slippy");

        if(!dungeonRequiresInterrupt){
            Collections.shuffle(dpsCompanionNames);
        }

        boolean finalTank = tank;
        boolean finalHeal = healer;
        boolean finalInterrupt = dungeonRequiresInterrupt;
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(!Mystica.dungeonsApi().isPlayerInDungeon(leaderPlayer)){
                    return;
                }

                if(count<=1){
                    count++;
                    return;
                }

                for(Player pMember: partyPlayers){
                    pMember.sendMessage("Filled team with bots");
                }

                boolean interruptSpawned = false;
                boolean healSpawned = false;
                boolean tankSpawned = false;
                Set<Entity> dpsPlayerSpawned = new HashSet<>();


                while (mParty.size()<5){

                    if(!finalHeal && !healSpawned){
                        Entity companion;
                        try {
                            companion = MythicBukkit.inst().getAPIHelper().spawnMythicMob("Wings", leaderPlayer.getLocation());
                        } catch (InvalidMobTypeException e) {
                            break;
                        }
                        mParty.add((LivingEntity) companion);
                        healSpawned = true;
                        continue;
                    }

                    if(!finalTank && !tankSpawned){
                        Entity companion;
                        try {
                            companion = MythicBukkit.inst().getAPIHelper().spawnMythicMob("SammingSins", leaderPlayer.getLocation());
                        } catch (InvalidMobTypeException e) {
                            break;

                        }
                        mParty.add((LivingEntity) companion);
                        tankSpawned = true;
                        continue;
                    }

                    if(finalInterrupt && !interruptSpawned){

                        Entity companion;

                        try {
                            companion = MythicBukkit.inst().getAPIHelper().spawnMythicMob("Darwin", leaderPlayer.getLocation());
                        } catch (InvalidMobTypeException e) {
                            break;

                        }
                        dpsPlayerSpawned.add(companion);
                        interruptSpawned = true;
                        mParty.add((LivingEntity) companion);
                        continue;

                    }
                    else{

                        Entity companion;

                        try {
                            companion = MythicBukkit.inst().getAPIHelper().spawnMythicMob(dpsCompanionNames.get(dpsPlayerSpawned.size()), leaderPlayer.getLocation());
                        } catch (InvalidMobTypeException e) {
                            break;

                        }
                        dpsPlayerSpawned.add(companion);
                        mParty.add((LivingEntity) companion);

                    }


                }

                this.cancel();

                //Bukkit.getLogger().info(String.valueOf(mysticaPartyManager.getMysticaParty(leaderPlayer)));
            }
        }.runTaskTimer(main, 0, 20);


    }




}
