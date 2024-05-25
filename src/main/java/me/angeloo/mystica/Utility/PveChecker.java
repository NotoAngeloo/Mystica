package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Managers.PvpManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class PveChecker {

    private final ProfileManager profileManager;
    private final PvpManager pvpManager;

    public PveChecker(Mystica main) {
        this.profileManager = main.getProfileManager();
        pvpManager = main.getPvpManager();
    }

    public boolean pveLogic(LivingEntity entity){

        if(entity instanceof Player){
            return false;
        }

        if(profileManager.getAnyProfile(entity).fakePlayer()){
            if(pvpManager.globalPvp()){
                return true;
            }
        }

        boolean passive = profileManager.getAnyProfile(entity).getIsPassive();

        return !passive;

    }



}
