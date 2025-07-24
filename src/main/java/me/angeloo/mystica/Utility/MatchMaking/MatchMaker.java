package me.angeloo.mystica.Utility.MatchMaking;

import me.angeloo.mystica.Utility.Enums.Dungeon;
import org.bukkit.Bukkit;

import java.util.*;

public class MatchMaker {


    private final List<MatchMakingParty> queue = Collections.synchronizedList(new ArrayList<>());

    public MatchMaker(){

    }

    public void addToQueue(MatchMakingParty newParty) {
        queue.add(newParty);
        tryToMatch(newParty);
    }

    private void tryToMatch(MatchMakingParty incoming){

        List<MatchMakingParty> candidates = new ArrayList<>();
        candidates.add(incoming);

        synchronized (queue){
            for (MatchMakingParty other : new ArrayList<>(queue)){

                if(other == incoming){
                    continue;
                }

                if(candidates.stream().allMatch(p -> p.compatible(other))){
                    candidates.add(other);

                    MatchMakingParty merged = mergeParties(candidates);
                    if(merged.size() == 5 && merged.full()){
                        for(MatchMakingParty p : candidates){
                            queue.remove(p);
                        }
                    }

                    handleMatchedTeams(merged);

                }

            }
        }


    }

    private MatchMakingParty mergeParties(List<MatchMakingParty> parties){
        List<MatchMakingPlayer> all = new ArrayList<>();
        for(MatchMakingParty p : parties){
            all.addAll(p.getPlayers());
        }

        return new MatchMakingParty(all);
    }

    private void handleMatchedTeams(MatchMakingParty team){

        Bukkit.getLogger().info("team successfully formed");

        for (MatchMakingPlayer player : team.getPlayers()){
            Bukkit.getLogger().info(String.valueOf(player.getRole()));
        }

    }

    public void removePlayerFromQueue(UUID playerId) {
        synchronized (queue) {
            Iterator<MatchMakingParty> iterator = queue.iterator();
            while (iterator.hasNext()) {
                MatchMakingParty party = iterator.next();
                if (party.containsPlayer(playerId)) {
                    iterator.remove();
                }
            }
        }
    }



}
