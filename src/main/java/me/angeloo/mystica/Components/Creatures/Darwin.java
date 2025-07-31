package me.angeloo.mystica.Components.Creatures;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.angeloo.mystica.Components.FakePlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.Stats;
import me.angeloo.mystica.CustomEvents.CompanionSpawnEvent;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Darwin {

    private final ProfileManager profileManager;

    public Darwin(Mystica main, ProfileManager profileManager){
        this.profileManager = profileManager;
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

        PartiesAPI api = Parties.getApi();

        if (closestPlayer != null) {
            PartyPlayer partyPlayer = api.getPartyPlayer(closestPlayer.getUniqueId());

            assert partyPlayer != null;
            if (partyPlayer.isInParty()) {
                UUID partyId = partyPlayer.getPartyId();
                assert partyId != null;
                Party party = api.getParty(partyId);
                assert party != null;
                UUID partyLeaderId = party.getLeader();
                assert partyLeaderId != null;
                theClosestPlayersLeader = Bukkit.getPlayer(partyLeaderId);

            } else {
                theClosestPlayersLeader = closestPlayer;
            }
            assert theClosestPlayersLeader != null;

        }

        int level = 1;

        if(theClosestPlayersLeader != null){
            level = profileManager.getAnyProfile(theClosestPlayersLeader).getStats().getLevel();
        }

        int attack = 50 + (9 * level);
        int health = 100 + (117 * level);
        int defence = 50 + (8 * level);
        int magic_defence = 50 + (8 * level);
        int crit = 1;

        attack+=(level*2);
        health+=(level*15);
        defence+=(level);
        magic_defence+=(level);

        attack+=level;

        Stats stats = new Stats(level, attack, health, defence, magic_defence, crit);

        FakePlayerProfile fakePlayerProfile = new FakePlayerProfile(false, false, stats, PlayerClass.Warrior, SubClass.Executioner);

        profileManager.addToFakePlayerProfileMap(uuid, fakePlayerProfile);
        profileManager.setCompanionFaces(uuid, "darwin");

        if(theClosestPlayersLeader != null){
            profileManager.addCompanion(closestPlayer, entity.getUniqueId());
        }

        Bukkit.getServer().getPluginManager().callEvent(new CompanionSpawnEvent((LivingEntity) entity));

    }

}
