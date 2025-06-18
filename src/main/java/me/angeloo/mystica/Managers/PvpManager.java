package me.angeloo.mystica.Managers;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PvpManager {

    private final Map<LivingEntity, Boolean> inPvp = new HashMap<>();

    private boolean globalPvp = false;

    private final ProfileManager profileManager;

    public PvpManager(Mystica main){
        profileManager = main.getProfileManager();
    }

    private boolean inPvp(LivingEntity player){

        if(globalPvp){
            inPvp.put(player, true);
        }
        else {
            inPvp.put(player, false);
        }

        if(!inPvp.containsKey(player)){
            inPvp.put(player, false);
        }

        return inPvp.get(player);
    }

    public boolean pvpLogic(LivingEntity player, Player otherPlayer){

        if(profileManager.getAnyProfile(player).fakePlayer()){
            player = profileManager.getCompanionsPlayer(player);
        }

        if(player == otherPlayer){
            return false;
        }

        PartiesAPI api = Parties.getApi();

        if(api.areInTheSameParty(player.getUniqueId(), otherPlayer.getUniqueId())){
            return false;
        }

        return inPvp(player) && inPvp(otherPlayer);
    }

    public void setPvpFlag(Player player){
        inPvp.put(player, true);
    }

    public void removePvpFlag(Player player){

        if(globalPvp){
            return;
        }
        inPvp.put(player, false);
    }

    public boolean globalPvp(){
        return globalPvp;
    }

    public void toggleGlobalPvp(){


        if(globalPvp){
            globalPvp = false;
            Bukkit.broadcastMessage("Global Pvp is now disabled");
            return;
        }

        globalPvp = true;
        Bukkit.broadcastMessage("Global Pvp is now enabled");
    }


}
