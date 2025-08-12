package me.angeloo.mystica.Managers.Parties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerParty {

    private UUID leaderId;
    private final Set<UUID> partyIds;

    public PlayerParty(UUID leaderId){
        this.leaderId = leaderId;
        this.partyIds = new HashSet<>();
        partyIds.add(leaderId);
    }

    public Player getLeader(){
        return Bukkit.getOfflinePlayer(leaderId).getPlayer();
    }

    public Set<Player> getPlayers(){
        Set<Player> players = new HashSet<>();
        for(UUID memberId : partyIds){
            Player player = Bukkit.getOfflinePlayer(memberId).getPlayer();
            players.add(player);
        }
        return players;
    }

    public void addPlayer(Player player){
        partyIds.add(player.getUniqueId());
    }

    public void removePlayer(Player toRemove){

        UUID removeId = toRemove.getUniqueId();
        partyIds.remove(removeId);
        if(leaderId.equals(removeId)){
            List<UUID> memberList = new ArrayList<>(partyIds);
            leaderId = memberList.get(0);
        }
    }




}
