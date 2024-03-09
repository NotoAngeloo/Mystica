package me.angeloo.mystica.Utility;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.angeloo.mystica.Managers.DpsManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class DamageHealthBoard {

    private final ProfileManager profileManager;
    private final DpsManager dpsManager;

    private final Map<UUID, Integer> boardPreferences = new HashMap<>();

    public DamageHealthBoard(Mystica main){
        profileManager = main.getProfileManager();
        dpsManager = main.getDpsManager();
    }

    public void update(Player player){

        if(!profileManager.getAnyProfile(player).getIfInCombat()){
            removeScoreboard(player);
            return;
        }

        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());

        assert partyPlayer != null;
        if(!partyPlayer.isInParty()){
            removeScoreboard(player);
            return;
        }

        Party party = api.getParty(partyPlayer.getPartyId());

        assert party != null;
        Set<UUID> partyMemberList = party.getMembers();


        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("test", Criteria.DUMMY, "");

        switch (getPreference(player)){
            case 0:{
                objective.setDisplayName("Dps");
                //sort it by dps
                for (UUID memberId : partyMemberList){

                    Player partyMember = Bukkit.getPlayer(memberId);

                    if(partyMember == null){
                        continue;
                    }

                    Score partyMemberName = objective.getScore(partyMember.getName());

                    int roundedDps = dpsManager.getRoundedDps(partyMember);

                    partyMemberName.setScore(roundedDps);
                }

                break;
            }
            case 1:{
                objective.setDisplayName("Percent Health");
                //health
                for (UUID memberId : partyMemberList){

                    Player partyMember = Bukkit.getPlayer(memberId);

                    if(partyMember == null){
                        continue;
                    }

                    Score partyMemberName = objective.getScore(partyMember.getName());

                    int healthPercent = (int) (Math.round(profileManager.getAnyProfile(partyMember).getCurrentHealth() / profileManager.getAnyProfile(partyMember).getTotalHealth()) * 100);



                    partyMemberName.setScore(healthPercent);
                }
                break;
            }
            case 2:{
                //none
                removeScoreboard(player);
                return;
            }
        }


        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(board);
    }

    public void removeScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }

    public void toggle(Player player){

        int preference = getPreference(player);

        preference +=1;

        if(preference>2){
            preference=0;
        }

        boardPreferences.put(player.getUniqueId(), preference);
        update(player);
    }

    private int getPreference(Player player){

        if(!boardPreferences.containsKey(player.getUniqueId())){
            boardPreferences.put(player.getUniqueId(), 0);
        }

        return boardPreferences.get(player.getUniqueId());
    }

}
