package me.angeloo.mystica.Managers;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.MysticaParty;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class MysticaPartyManager {

    private final ProfileManager profileManager;

    private final Map<UUID, List<LivingEntity>> mysticaPartyMap = new HashMap<>();

    private final Map<Player, MysticaParty> mPartyMap = new HashMap<>();
    private final Map<Player, Player> leaderPlayer = new HashMap<>();

    public MysticaPartyManager(Mystica main){
        profileManager = main.getProfileManager();
    }

    public void removeFromMysticaPartyMap(LivingEntity entity){
        mysticaPartyMap.remove(entity.getUniqueId());
    }


    public List<LivingEntity> getMysticaParty(LivingEntity caster){

        List<LivingEntity> mParty;

        if(!mysticaPartyMap.containsKey(caster.getUniqueId())){
             mParty = new ArrayList<>();
            //mParty.add(caster);

            Player player;

            if(caster instanceof Player){
                player = (Player) caster;
            }
            else{
                player = profileManager.getCompanionsPlayer(caster);

            }

            if(player == null){
                return mParty;
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
            else{
                mParty.add(caster);
            }

            if(!profileManager.getCompanions(partyLeader).isEmpty()){
                List<UUID> companions = profileManager.getCompanions(player);

                for(UUID companionId : companions){
                    LivingEntity companion = (LivingEntity) Bukkit.getEntity(companionId);

                    if(companion == null){
                        continue;
                    }

                    mParty.add(companion);

                }

                //Bukkit.getLogger().info("companions: " + companions);
            }

            for(LivingEntity member : mParty){
                mysticaPartyMap.put(member.getUniqueId(), mParty);
            }

        }
        else{
            mParty = mysticaPartyMap.get(caster.getUniqueId());
        }



        return mParty;
    }

    public Player getMPartyLeader(LivingEntity entity){

        Player player;

        if(entity instanceof Player){
            player = (Player) entity;
        }
        else{
            player = profileManager.getCompanionsPlayer(entity);
        }

        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        assert partyPlayer != null;
        if(partyPlayer.isInParty()){
            Party party = api.getParty(partyPlayer.getPartyId());
            assert party != null;
            return Bukkit.getPlayer(party.getLeader());
        }
        return player;

    }

    public Set<Player> getPartyPlayers(Player player){
        Set<Player> playerSet = new HashSet<>();
        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        assert partyPlayer != null;
        if(partyPlayer.isInParty()){
            Party party = api.getParty(partyPlayer.getPartyId());
            assert party != null;
            for(UUID memberId : party.getMembers()){
                playerSet.add(Bukkit.getPlayer(memberId));
            }
            return playerSet;
        }
        playerSet.add(player);
        return playerSet;
    }

    public void createMParty(Player leader){
        MysticaParty mysticaParty = new MysticaParty(leader);
        mPartyMap.put(leader, mysticaParty);
    }

    public void removeMParty(Player player){
        mPartyMap.remove(player);
    }

    public void transferMParty(Player oldPlayer, Player newPlayer){

        if(mPartyMap.containsKey(oldPlayer)){
            mPartyMap.put(newPlayer, mPartyMap.get(oldPlayer));
            mPartyMap.remove(oldPlayer);
        }

    }

    public MysticaParty getPlayerMParty(Player player){

        if(mPartyMap.containsKey(getLeaderPlayer(player))){
            return mPartyMap.get(getLeaderPlayer(player));
        }

        return null;
    }


    public void setLeaderPlayer(Player player, Player leaderPlayer){
        this.leaderPlayer.put(player, leaderPlayer);
    }

    public Player getLeaderPlayer(Player player){
        return leaderPlayer.getOrDefault(player, player);
    }

    public void createPartyFromMatchmaking(Player tank, Player heal, Player damage1, Player damage2, Player damage3){
        PartiesAPI api = Parties.getApi();

        PartyPlayer tankPlayer = api.getPartyPlayer(tank.getUniqueId());
        PartyPlayer healPlayer = api.getPartyPlayer(heal.getUniqueId());
        PartyPlayer damage1Player = api.getPartyPlayer(damage1.getUniqueId());
        PartyPlayer damage2Player = api.getPartyPlayer(damage2.getUniqueId());
        PartyPlayer damage3Player = api.getPartyPlayer(damage3.getUniqueId());

        api.createParty("", tankPlayer);
        Party party = api.getPartyOfPlayer(tank.getUniqueId());
        assert party != null;
        assert healPlayer != null;
        party.addMember(healPlayer);
        assert damage1Player != null;
        party.addMember(damage1Player);
        assert damage2Player != null;
        party.addMember(damage2Player);
        assert damage3Player != null;
        party.addMember(damage3Player);
    }


    //team merger???

}
