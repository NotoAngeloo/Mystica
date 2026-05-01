package me.angeloo.mystica.Components.CombatSystem.Targeting;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.CombatSystem.GravestoneManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Logic.StealthTargetBlacklist;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class TargetValidator {

    private final ProfileManager profileManager;
    private final PvpManager pvpManager;
    private final GravestoneManager gravestoneManager;
    private final StealthTargetBlacklist stealthTargetBlacklist;

    public TargetValidator(Mystica main) {
        profileManager = main.getProfileManager();
        pvpManager = main.getPvpManager();
        gravestoneManager = main.getGravestoneManager();
        stealthTargetBlacklist = main.getStealthTargetBlacklist();
    }

    public boolean isValidTarget(Player source, LivingEntity target){

        if(target == null) return false;

        if(target.isDead()) return false;

        var profile = profileManager.getAnyProfile(target);

        if(profile.getIfDead()) return false;

        // players
        if(target instanceof Player playerTarget){
            if(!pvpManager.pvpLogic(source, playerTarget)){
                return false;
            }

            if(stealthTargetBlacklist.get(playerTarget)){
                return false;
            }
        }

        // mythic mobs / special entities
        if(MythicBukkit.inst().getAPIHelper().isMythicMob(target.getUniqueId())){

            if(profile.getIfObject()){
                return gravestoneManager.isGravestone(target);
            }

            return true;
        }

        return true;
    }
}


