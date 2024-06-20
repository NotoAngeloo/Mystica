package me.angeloo.mystica.Utility;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Managers.TargetManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class MysticaEntityGrabber {

    private final ProfileManager profileManager;
    private final TargetManager targetManager;

    public MysticaEntityGrabber(Mystica main){
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
    }

    public UUID getLowest(Player player){

        List<LivingEntity> liveParty = new ArrayList<>();

        if(!profileManager.getCompanions(player).isEmpty()){
            List<LivingEntity> fakeParty = getFakeParty(player);


            for(LivingEntity member : fakeParty){
                if(profileManager.getAnyProfile(member).getIfDead()){
                    continue;
                }
                liveParty.add(member);
            }

            liveParty.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth() / profileManager.getAnyProfile(p).getTotalHealth()));

            if(!liveParty.isEmpty()){
                return liveParty.get(liveParty.size()-1).getUniqueId();
            }

        }

        List<LivingEntity> realParty = getParty(player);

        for(LivingEntity member : realParty){
            if(profileManager.getAnyProfile(member).getIfDead()){
                continue;
            }
            liveParty.add(member);
        }

        liveParty.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth() / profileManager.getAnyProfile(p).getTotalHealth()));

        if(!liveParty.isEmpty()){
            return liveParty.get(liveParty.size()-1).getUniqueId();
        }

        return player.getUniqueId();
    }

    public UUID getRandomEntity(Player player){

        List<LivingEntity> liveParty = new ArrayList<>();

        if(!profileManager.getCompanions(player).isEmpty()){
            List<LivingEntity> fakeParty = getFakeParty(player);


            for(LivingEntity member : fakeParty){
                if(profileManager.getAnyProfile(member).getIfDead()){
                    continue;
                }
                liveParty.add(member);
            }

            Collections.shuffle(liveParty);

            if(!liveParty.isEmpty()){
                return liveParty.get(0).getUniqueId();
            }

        }

        List<LivingEntity> realParty = getParty(player);

        for(LivingEntity member : realParty){
            if(profileManager.getAnyProfile(member).getIfDead()){
                continue;
            }
            liveParty.add(member);
        }

        Collections.shuffle(liveParty);

        if(!liveParty.isEmpty()){
            return liveParty.get(0).getUniqueId();
        }

        return player.getUniqueId();
    }



    private List<LivingEntity> getFakeParty(Player player){
        List<LivingEntity> fakeParty = new ArrayList<>();
        if(!profileManager.getCompanions(player).isEmpty()){
            List<LivingEntity> companions = profileManager.getCompanions(player);
            fakeParty.addAll(companions);
        }
        fakeParty.add(player);
        return fakeParty;
    }

    private List<LivingEntity> getParty(Player player){
        List<LivingEntity> realParty = new ArrayList<>();
        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        assert partyPlayer != null;
        if(partyPlayer.isInParty()){
            Party party = api.getParty(partyPlayer.getPartyId());
            assert party != null;
            for(UUID partyMemberId : party.getMembers()){
                realParty.add(Bukkit.getPlayer(partyMemberId));
            }
        }
        realParty.add(player);
        return realParty;
    }

}
