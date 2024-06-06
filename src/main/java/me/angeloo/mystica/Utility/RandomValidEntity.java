package me.angeloo.mystica.Utility;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class RandomValidEntity {

    private final ProfileManager profileManager;

    public RandomValidEntity(Mystica main){
        profileManager = main.getProfileManager();
    }

    public UUID getRandomEntity(Player player){

        List<UUID> targetedIds = new ArrayList<>();

        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        assert partyPlayer != null;
        if(partyPlayer.isInParty()){
            Party party = api.getParty(partyPlayer.getPartyId());
            assert party != null;
            Set<UUID> partyMemberList = party.getMembers();

            if(partyMemberList.size() > 1){
                for(UUID partyMemberId : partyMemberList){
                    Player partyMember = Bukkit.getPlayer(partyMemberId);

                    if(partyMember==null){
                        continue;
                    }

                    if(profileManager.getAnyProfile(partyMember).getIfDead()){
                        continue;
                    }

                    targetedIds.add(partyMemberId);
                }

                targetedIds.addAll(partyMemberList);
                Collections.shuffle(targetedIds);

                return targetedIds.get(0);
            }
        }

        if(!profileManager.getCompanions(player).isEmpty()){

            //Bukkit.getLogger().info("companion mode");

            if(!profileManager.getAnyProfile(player).getIfDead()){
                targetedIds.add(player.getUniqueId());
            }

            for(LivingEntity companion : profileManager.getCompanions(player)){

                if(profileManager.getAnyProfile(companion).getIfDead()){
                    continue;
                }

                UUID companionId = companion.getUniqueId();
                targetedIds.add(companionId);
                //Bukkit.getLogger().info("companion not dead, adding " + companion.getName());
            }

            Collections.shuffle(targetedIds);

            return targetedIds.get(0);
        }

        if(!profileManager.getAnyProfile(player).getIfDead()){
            return player.getUniqueId();
        }

        return null;
    }

}
