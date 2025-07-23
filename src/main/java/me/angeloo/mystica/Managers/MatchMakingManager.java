package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.Role;
import me.angeloo.mystica.Utility.Enums.SubClass;
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


    public MatchMakingManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        customInventoryManager = main.getInventoryManager();
        mysticaPartyManager = main.getMysticaPartyManager();
    }


    public void fillWithBots(Player player){

        //make the players enter, when all entered, fill with bots

        Player leaderPlayer = mysticaPartyManager.getLeaderPlayer(player);
        Set<Player> partyPlayers = mysticaPartyManager.getPartyPlayers(player);
        List<LivingEntity> mParty = mysticaPartyManager.getMysticaParty(leaderPlayer);

        boolean dungeonRequiresInterrupt = false;

        //does sending the leader send all others?
        switch (customInventoryManager.getDungeonIndex(player)) {
            case 0 -> {
                Mystica.dungeonsApi().initiateDungeonForPlayer(player, "Heart_of_Corruption");
                dungeonRequiresInterrupt = true;
            }
            case 1 -> {
                Mystica.dungeonsApi().initiateDungeonForPlayer(player, "Acolyte_of_Chaos");
            }
            case 2 -> {
                Mystica.dungeonsApi().initiateDungeonForPlayer(player, "Cave_of_Lindwyrm");
            }
            case 3 -> {
                Mystica.dungeonsApi().initiateDungeonForPlayer(player, "Curse_of_Shadow");
            }
        }

        //get what roles are filled
        boolean tank = false;
        boolean healer = false;

        for (Player member : partyPlayers){
            if(getRole(member).equals(Role.Tank)){
                tank = true;
            }

            if(getRole(member).equals(Role.Healer)){
                healer = true;
            }
        }

        List<String> dpsCompanionNames = new ArrayList<>();
        dpsCompanionNames.add("Darwin");
        dpsCompanionNames.add("Luna");
        dpsCompanionNames.add("Slippy");
        Collections.shuffle(dpsCompanionNames);

        boolean finalTank = tank;
        boolean finalHeal = healer;
        boolean finalInterrupt = dungeonRequiresInterrupt;
        new BukkitRunnable(){
            @Override
            public void run(){

                if(!Mystica.dungeonsApi().isPlayerInDungeon(leaderPlayer)){
                    return;
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

                Bukkit.getLogger().info(String.valueOf(mysticaPartyManager.getMysticaParty(leaderPlayer)));
            }
        }.runTaskTimer(main, 0, 20);


    }

    private Role getRole(LivingEntity partyMember){

        SubClass subClass = profileManager.getAnyProfile(partyMember).getPlayerSubclass();

        switch (subClass){
            case Shepard, Divine -> {
                return Role.Healer;
            }
            case Gladiator, Blood, Templar ->{
                return Role.Tank;
            }
            default -> {
                return Role.Damage;
            }
        }

    }

}
