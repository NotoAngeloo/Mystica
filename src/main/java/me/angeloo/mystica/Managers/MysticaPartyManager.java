package me.angeloo.mystica.Managers;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MysticaPartyManager {

    private final ProfileManager profileManager;

    public MysticaPartyManager(Mystica main){
        profileManager = main.getProfileManager();
    }

    public List<LivingEntity> getMParty(LivingEntity caster){
        List<LivingEntity> mParty = new ArrayList<>();
        mParty.add(caster);


        Player player;

        if(caster instanceof Player){
            player = (Player) caster;
        }
        else{
            player = profileManager.getCompanionsPlayer(caster);

        }

        Player partyLeader = player;

        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        assert partyPlayer != null;
        if(partyPlayer.isInParty()){
            Party party = api.getParty(partyPlayer.getPartyId());
            assert party != null;
            partyLeader = Bukkit.getPlayer(party.getLeader());
            for(UUID partyMemberId : party.getMembers()){
                mParty.add(Bukkit.getPlayer(partyMemberId));
            }
        }

        if(!profileManager.getCompanions(partyLeader).isEmpty()){
            List<LivingEntity> companions = profileManager.getCompanions(player);
            mParty.addAll(companions);
        }


        return mParty;
    }


}
