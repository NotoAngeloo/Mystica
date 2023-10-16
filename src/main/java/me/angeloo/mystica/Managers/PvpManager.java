package me.angeloo.mystica.Managers;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PvpManager {

    private final ProfileManager profileManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final Map<Player, Boolean> inPvp;

    private boolean globalPvp;

    public PvpManager(Mystica main){
        profileManager = main.getProfileManager();
        changeResourceHandler = main.getChangeResourceHandler();
        inPvp = new HashMap<>();
        globalPvp = false;
    }

    private boolean inPvp(Player player){

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

    public boolean pvpLogic(Player player, Player otherPlayer){

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
