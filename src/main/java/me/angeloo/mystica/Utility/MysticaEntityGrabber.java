package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.MysticaPartyManager;
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
    private final MysticaPartyManager mysticaPartyManager;
    private final TargetManager targetManager;

    public MysticaEntityGrabber(Mystica main){
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        targetManager = main.getTargetManager();
    }

    public UUID getBossTarget(Player player){
        return targetManager.getBossTarget(player);
    }

    public UUID getLowestPhp(Player player){

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMParty(player));

        mParty.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth()));

        if(!mParty.isEmpty()){
            return mParty.get(mParty.size()-1).getUniqueId();
        }

        return player.getUniqueId();
    }

    public UUID getRandomEntity(Player player){

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMParty(player));

        Collections.shuffle(mParty);

        if(!mParty.isEmpty()){
            return mParty.get(0).getUniqueId();
        }

        return player.getUniqueId();
    }

    public int getValidAmount(Player player){
        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMParty(player));

        return mParty.size();
    }



}
