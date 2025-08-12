package me.angeloo.mystica.Managers.Parties;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import net.playavalon.mythicdungeons.player.MythicPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class MysticaPartyManager {

    private final Mystica main;
    private final ProfileManager profileManager;

    private final Map<UUID, PlayerParty> pPartyMap = new HashMap<>();


    public MysticaPartyManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
    }


    public List<LivingEntity> getMysticaParty(LivingEntity caster){

        List<LivingEntity> mParty = new ArrayList<>();

        if(caster == null){
            return mParty;
        }

        if(caster instanceof Player player){

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

            return mParty;
        }

        Player leaderPlayer = profileManager.getCompanionsPlayer(caster);
        PlayerParty playerParty = pPartyMap.get(leaderPlayer.getUniqueId());
        mParty.addAll(playerParty.getPlayers());

        if(!profileManager.getCompanions(leaderPlayer).isEmpty()){
            List<UUID> companions = profileManager.getCompanions(leaderPlayer);
            for(UUID companionId : companions){
                Entity cEntity = Bukkit.getEntity(companionId);
                if(cEntity instanceof LivingEntity companion){
                    mParty.add(companion);
                }
            }
        }

        return mParty;

        /*if(caster == null){
            return new ArrayList<>();
        }

        List<LivingEntity> mParty;

        if(!mysticaPartyMap.containsKey(caster)){

            Player player;

            if(caster instanceof Player){
                player = (Player) caster;
            }
            else{
                player = profileManager.getCompanionsPlayer(caster);

            }

            if(player == null){
                return new ArrayList<>();
            }

            updateMysticaParty(player);

        }

        mParty = mysticaPartyMap.get(caster);


        return mParty;*/
    }


    /*public void updateMysticaParty(LivingEntity entity){

        List<LivingEntity> mParty = new ArrayList<>();

        Player player;
        if(entity instanceof Player){
            player = (Player) entity;
        }
        else{
            player = profileManager.getCompanionsPlayer(entity);


            if(player == null){
                return;
            }

        }

        Player partyLeader = player;
        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        assert partyPlayer != null;

        if(partyPlayer.isInParty()){
            UUID partyId = partyPlayer.getPartyId();
            assert partyId != null;
            Party party = api.getParty(partyId);
            assert party != null;
            UUID pPartyLeader = party.getLeader();
            assert pPartyLeader != null;
            partyLeader = Bukkit.getPlayer(pPartyLeader);

            for(UUID memberId : party.getMembers()){
                Player member = Bukkit.getPlayer(memberId);
                if(member != null){
                    mParty.add(member);
                    setLeaderPlayer(member, partyLeader);
                }
            }

        }
        else{
            mParty.add(player);
        }

        if(!profileManager.getCompanions(partyLeader).isEmpty()){
            List<UUID> companions = profileManager.getCompanions(player);

            for(UUID companionId : companions){
                Entity cEntity = Bukkit.getEntity(companionId);
                if(cEntity instanceof LivingEntity companion){
                    mParty.add(companion);
                    setLeaderPlayer(companion, partyLeader);
                }
            }

        }

        for(LivingEntity member : mParty){
            mysticaPartyMap.put(member, mParty);

            if(member instanceof Player pMember){
                Bukkit.getServer().getScheduler().runTask(main, () ->  Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(pMember, BarType.Team)));
            }


        }



    }*/

    /*public Player getMPartyLeader(LivingEntity entity){

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
            UUID partyId = partyPlayer.getPartyId();
            assert partyId != null;
            Party party = api.getParty(partyId);
            assert party != null;
            UUID leaderId = party.getLeader();
            assert leaderId != null;
            return Bukkit.getPlayer(leaderId);
        }

        return player;
    }*/

    /*public Set<Player> getPartyPlayers(Player player){
        Set<Player> playerSet = new HashSet<>();
        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        assert partyPlayer != null;
        if(partyPlayer.isInParty()){
            UUID partyId = partyPlayer.getPartyId();
            assert partyId != null;
            Party party = api.getParty(partyId);
            assert party != null;
            for(UUID memberId : party.getMembers()){
                playerSet.add(Bukkit.getPlayer(memberId));
            }
            return playerSet;
        }
        playerSet.add(player);
        return playerSet;
    }*/



    /*public void setLeaderPlayer(LivingEntity player, Player leaderPlayer){
        this.leaderPlayer.put(player, leaderPlayer);
    }

    public Player getLeaderPlayer(Player player){

        if(leaderPlayer.containsKey(player)){
            Player leader = leaderPlayer.get(player);

            if(leader != null){
                return leader;
            }

        }

        return player;
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
    }*/


    public void joinParty(Player player, Player inviter){

        if(!inPParty(inviter)){
            PlayerParty pParty = new PlayerParty(inviter.getUniqueId());
            pParty.addPlayer(inviter);
            pPartyMap.put(inviter.getUniqueId(), pParty);
        }

        PlayerParty party = pPartyMap.get(inviter.getUniqueId());

        party.addPlayer(player);
        pPartyMap.put(player.getUniqueId(), party);

        /*PartiesAPI api = Parties.getApi();

        if(!inPParty(inviter)){
            //create a new party
            PartyPlayer partyInviter = api.getPartyPlayer(inviter.getUniqueId());
            api.createParty("", partyInviter);
        }

        Party party = api.getPartyOfPlayer(inviter.getUniqueId());

        if(party == null){
            return;
        }

        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());

        if(partyPlayer == null){
            return;
        }

        party.addMember(partyPlayer);

        for(LivingEntity currentMember : mysticaPartyMap.get(inviter)){
            updateMysticaParty(currentMember);
        }

        updateMysticaParty(player);

        Bukkit.getServer().getPluginManager().callEvent(new PartyUpdateWhenObservingEvent(mysticaPartyMap.get(inviter)));*/
    }

    public void removeFromParty(Player player){

        if(!profileManager.getCompanions(player).isEmpty()){
            profileManager.removeCompanions(player);
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

        //update mparty???

        /*if(inPParty(player)) {
            Bukkit.getLogger().info(player.getName() + " in pparty");
            Party party = Parties.getApi().getPartyOfPlayer(player.getUniqueId());
            Bukkit.getLogger().info("party: " + party);
            PartyPlayer partyPlayer = Parties.getApi().getPartyPlayer(player.getUniqueId());
            Bukkit.getLogger().info("party player: " + partyPlayer);
            assert party != null;
            assert partyPlayer != null;
            party.removeMember(partyPlayer);
            Bukkit.getLogger().info("removing from pparty");
        }

        if(Mystica.dungeonsApi().isPlayerInDungeon(player)){
            List<MythicPlayer> mythicPlayers = Mystica.dungeonsApi().getDungeonInstance(player).getPlayers();
            for(MythicPlayer mythicPlayer : mythicPlayers){
                if(player == mythicPlayer.getPlayer()){
                    Mystica.dungeonsApi().getDungeonInstance(player).removePlayer(mythicPlayer);
                    Bukkit.getLogger().info("removing " + player.getName() + " from dungeon");
                    break;
                }

            }
        }

        List<LivingEntity> mParty = new ArrayList<>(getMysticaParty(player));


        for(LivingEntity member : mParty){
            updateMysticaParty(member);
        }

        removeFromMysticaPartyMap(player);*/
    }

    public PlayerParty getPlayerParty(Player player){
        return pPartyMap.get(player.getUniqueId());
    }

    public Player getLeaderPlayer(Player player){

        if(inPParty(player)){
            return pPartyMap.get(player.getUniqueId()).getLeader();
        }

        return player;
    }

    public boolean inPParty(Player player){

        return pPartyMap.containsKey(player.getUniqueId());

        /*return Parties.getApi().isPlayerInParty(player.getUniqueId());*/
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
