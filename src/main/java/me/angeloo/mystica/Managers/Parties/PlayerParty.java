package me.angeloo.mystica.Managers.Parties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerParty {

    private UUID leaderId;
    private final List<UUID> partyIds;

    public PlayerParty(UUID leaderId){
        this.leaderId = leaderId;
        this.partyIds = new ArrayList<>();
    }

    public Player getLeader(){
        return Bukkit.getOfflinePlayer(leaderId).getPlayer();
    }

    public List<Player> getPlayers(){
        List<Player> players = new ArrayList<>();
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
            leaderId = partyIds.get(0);
        }
    }




}
