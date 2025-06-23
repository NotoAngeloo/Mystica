package me.angeloo.mystica.Utility.Logic;

import me.angeloo.mystica.Managers.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Managers.TargetManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class MysticaEntityGrabber {

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final TargetManager targetManager;

    public MysticaEntityGrabber(Mystica main){
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        targetManager = main.getTargetManager();
    }

    public UUID getBossTarget(Player player){

        //List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMParty(player));


        return targetManager.getBossTarget(player);
    }

    public UUID getLowestPhp(Player player){

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

        List<LivingEntity> liveParty = new ArrayList<>();

        for(LivingEntity member : mParty){
            if(profileManager.getAnyProfile(member).getIfDead()){
                continue;
            }
            liveParty.add(member);
        }

        liveParty.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth()));

        if(!mParty.isEmpty()){
            return liveParty.get(0).getUniqueId();
        }

        Bukkit.getLogger().info("no lowest php found");

        return null;
    }

    public UUID getRandomEntity(Player player){

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

        //Bukkit.getLogger().info("all " + mParty);

        List<LivingEntity> liveParty = new ArrayList<>();

        for(LivingEntity member : mParty){
            if(profileManager.getAnyProfile(member).getIfDead()){
                continue;
            }
            liveParty.add(member);
        }

        Collections.shuffle(liveParty);

        //Bukkit.getLogger().info("live " + liveParty);

        if(!mParty.isEmpty()){
            return liveParty.get(0).getUniqueId();
        }

        Bukkit.getLogger().info("no random found");

        return null;
    }

    public int getValidAmount(Player player){
        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

        List<LivingEntity> liveParty = new ArrayList<>();

        for(LivingEntity member : mParty){
            if(profileManager.getAnyProfile(member).getIfDead()){
                continue;
            }
            liveParty.add(member);
        }

        return liveParty.size();
    }

    public UUID getMPartyLeader(Player player){
        return mysticaPartyManager.getMPartyLeader(player).getUniqueId();
    }



}
