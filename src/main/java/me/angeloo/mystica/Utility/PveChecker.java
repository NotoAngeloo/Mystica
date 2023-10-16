package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class PveChecker {

    private final ProfileManager profileManager;

    public PveChecker(Mystica main) {
        this.profileManager = main.getProfileManager();
    }

    public boolean pveLogic(LivingEntity entity){

        if(entity instanceof Player){
            return false;
        }

        boolean passive = profileManager.getAnyProfile(entity).getIsPassive();

        return !passive;

    }



}
