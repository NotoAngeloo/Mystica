package me.angeloo.mystica.Managers.Parties;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.playavalon.mythicdungeons.player.MythicPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class MysticaPartyManager {

    private final Mystica main;
    private final ProfileManager profileManager;

    private final Map<UUID, List<LivingEntity>> mPartyMap = new HashMap<>();
    private final Map<UUID, PlayerParty> pPartyMap = new HashMap<>();


    public MysticaPartyManager(Mystica main, ProfileManager profileManager){
        this.main = main;
        this.profileManager = profileManager;
    }


    //do an update like before
    public List<LivingEntity> getMysticaParty(LivingEntity entity){

        List<LivingEntity> mParty = new ArrayList<>();

        if(entity == null){
            return mParty;
        }

        if(entity instanceof Player player){
            if(mPartyMap.containsKey(player.getUniqueId())){
                return mPartyMap.get(player.getUniqueId());
            }

            mParty.add(player);
            return mParty;
        }

        Player leaderPlayer = profileManager.getCompanionsPlayer(entity);


        return getMysticaParty(leaderPlayer);
    }

    public void updateMysticaParty(Player player){

        if(player == null){
            return;
        }

        List<LivingEntity> mParty = new ArrayList<>();

        if(inPParty(player)){
            PlayerParty pParty = pPartyMap.get(player.getUniqueId());

            mParty.addAll(pParty.getPlayers());

            Player leaderPlayer = pParty.getLeader();

            if(!profileManager.getCompanions(leaderPlayer).isEmpty()){
                List<UUID> companions = profileManager.getCompanions(leaderPlayer);
                for(UUID companionId : companions){
                    Entity cEntity = Bukkit.getEntity(companionId);
                    if(cEntity instanceof LivingEntity companion){
                        mParty.add(companion);
                    }
                }
            }

            mPartyMap.put(player.getUniqueId(), mParty);
            return;
        }

        mParty.add(player);

        if(!profileManager.getCompanions(player).isEmpty()){
            List<UUID> companions = profileManager.getCompanions(player);
            for(UUID companionId : companions){
                Entity cEntity = Bukkit.getEntity(companionId);
                if(cEntity instanceof LivingEntity companion){
                    mParty.add(companion);
                }
            }
        }

        mPartyMap.put(player.getUniqueId(), mParty);
    }

    public void joinParty(Player player, Player inviter){

        if(!inPParty(inviter)){
            PlayerParty pParty = new PlayerParty(inviter.getUniqueId());
            pPartyMap.put(inviter.getUniqueId(), pParty);
        }

        PlayerParty party = pPartyMap.get(inviter.getUniqueId());

        party.addPlayer(player);
        pPartyMap.put(player.getUniqueId(), party);


        /*updateMysticaParty(player);

        Bukkit.getServer().getPluginManager().callEvent(new PartyUpdateWhenObservingEvent(mysticaPartyMap.get(inviter)));*/
    }

    public void removeFromParty(Player player){

        if(!profileManager.getCompanions(player).isEmpty()){
            profileManager.removeAllCompanions(player);
        }

        if(inPParty(player)){
            PlayerParty party = pPartyMap.get(player.getUniqueId());
            party.removePlayer(player);
            pPartyMap.remove(player.getUniqueId());
        }

        if(Mystica.dungeonsApi().isPlayerInDungeon(player)){
            List<MythicPlayer> mythicPlayers = Mystica.dungeonsApi().getDungeonInstance(player).getPlayers();
            for(MythicPlayer mythicPlayer : mythicPlayers){
                if(player == mythicPlayer.getPlayer()){
                    Mystica.dungeonsApi().getDungeonInstance(player).removePlayer(mythicPlayer);
                    //Bukkit.getLogger().info("removing " + player.getName() + " from dungeon");
                    break;
                }

            }
        }
    }

    public Set<Player> getPlayerParty(Player player){

        if(pPartyMap.containsKey(player.getUniqueId())){
            return pPartyMap.get(player.getUniqueId()).getPlayers();
        }

        Set<Player> defaultSet = new HashSet<>();
        defaultSet.add(player);

        return defaultSet;
    }

    public Player getLeaderPlayer(Player player){

        if(inPParty(player)){
            return pPartyMap.get(player.getUniqueId()).getLeader();
        }

        return player;
    }

    public boolean inPParty(Player player){
        return pPartyMap.containsKey(player.getUniqueId());
    }

    public boolean inSameParty(UUID player, UUID otherPlayer){

        if(pPartyMap.containsKey(player) && pPartyMap.containsKey(otherPlayer)){

            PlayerParty party1 = pPartyMap.get(player);
            PlayerParty party2 = pPartyMap.get(otherPlayer);

            return party1.equals(party2);
        }

        return false;
    }

}
