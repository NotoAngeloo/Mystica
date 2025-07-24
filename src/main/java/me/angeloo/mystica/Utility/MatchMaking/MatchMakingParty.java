package me.angeloo.mystica.Utility.MatchMaking;

import me.angeloo.mystica.Utility.Enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MatchMakingParty {

    private final List<MatchMakingPlayer> players;

    public MatchMakingParty(List<MatchMakingPlayer> players){
        this.players = players;
    }

    public List<MatchMakingPlayer> getPlayers(){
        return players;
    }

    public Map<Role, Long> getRoleCounts(){
        return players.stream().collect(Collectors.groupingBy(MatchMakingPlayer::getRole, Collectors.counting()));
    }

    public int size(){
        return players.size();
    }

    public boolean full(){
        return size() == 5 &&
                getRoleCounts().getOrDefault(Role.Tank, 0L) == 1 &&
                getRoleCounts().getOrDefault(Role.Healer, 0L) == 1;
    }

    public boolean compatible(MatchMakingParty other){
        int combinedSize = this.size() + other.size();
        if(combinedSize > 5){
            return false;
        }
        long tanks = this.getRoleCounts().getOrDefault(Role.Tank, 0L) + other.getRoleCounts().getOrDefault(Role.Tank, 0L);
        long healers = this.getRoleCounts().getOrDefault(Role.Healer, 0L) + other.getRoleCounts().getOrDefault(Role.Healer, 0L);

        return tanks <= 1 && healers <=1;
    }

    public MatchMakingParty merge(MatchMakingParty other){
        List<MatchMakingPlayer> combined = new ArrayList<>(this.players);
        combined.addAll(other.players);
        return new MatchMakingParty(combined);
    }

    public boolean containsPlayer(UUID playerId) {
        return players.stream().anyMatch(p -> p.getUuid().equals(playerId));
    }


}
