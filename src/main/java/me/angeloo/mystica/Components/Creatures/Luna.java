package me.angeloo.mystica.Components.Creatures;


import me.angeloo.mystica.Components.FakePlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.Stats;
import me.angeloo.mystica.CustomEvents.CompanionSpawnEvent;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Luna {

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;

    public Luna(Mystica main, ProfileManager profileManager, MysticaPartyManager mysticaPartyManager){
        this.profileManager = profileManager;
        this.mysticaPartyManager = mysticaPartyManager;
    }

    public void makeProfile(UUID uuid){

        Entity entity = Bukkit.getEntity(uuid);

        if(entity == null){
            return;
        }

        Player theClosestPlayersLeader = null;

        double closestDistanceSquared = Double.MAX_VALUE;
        Player closestPlayer = null;


        for (Player player : entity.getWorld().getPlayers()) {
            double distanceSquared = player.getLocation().distanceSquared(entity.getLocation());
            if (distanceSquared < closestDistanceSquared) {
                closestDistanceSquared = distanceSquared;
                closestPlayer = player;
            }
        }

        if(closestPlayer != null){
            theClosestPlayersLeader = mysticaPartyManager.getLeaderPlayer(closestPlayer);
        }

        int level = 1;

        if(theClosestPlayersLeader != null){
            level = profileManager.getAnyProfile(theClosestPlayersLeader).getStats().getLevel();
        }

        //base stats
        int attack = 50;
        int health = 100;
        int defence = 50;
        int magic_defence = 50;
        int crit = 1;

        //from level
        attack+=(level*2);
        health+=(level*15);
        defence+=(level);
        magic_defence+=(level);

        //from subclass
        health+=(level*15);

        //stats from gear, making so simulates starter gear, unleveled
        attack+=9;
        health+=99;
        defence+=8;

        Stats stats = new Stats(level, attack, health, defence, magic_defence, crit);

        FakePlayerProfile fakePlayerProfile = new FakePlayerProfile(false, false, stats, PlayerClass.Ranger, SubClass.Tamer);

        profileManager.addToFakePlayerProfileMap(uuid, fakePlayerProfile);
        profileManager.setCompanionFaces(uuid, "luna");

        if(theClosestPlayersLeader != null){
            profileManager.addCompanion(closestPlayer, entity.getUniqueId());
        }

        Bukkit.getServer().getPluginManager().callEvent(new CompanionSpawnEvent((LivingEntity) entity));

    }

}
