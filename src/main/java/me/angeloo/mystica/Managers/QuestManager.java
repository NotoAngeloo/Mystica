package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.Quests.LindwyrmQuest;
import me.angeloo.mystica.Components.Quests.NewPlayerQuest;
import me.angeloo.mystica.Components.Quests.SewerQuest;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestManager {

    private final ProfileManager profileManager;
    private final PathingManager pathingManager;

    private final NewPlayerQuest newPlayerQuest = new NewPlayerQuest();
    private final SewerQuest sewerQuest = new SewerQuest();
    private final LindwyrmQuest lindwyrmQuest = new LindwyrmQuest();

    private final Map<UUID, String> queuedQuest = new HashMap<>();

    public QuestManager(Mystica main){
        profileManager = main.getProfileManager();
        pathingManager = main.getPathingManager();
    }

    public void setQueuedQuest(Player player, String quest){

        if(!quest.equalsIgnoreCase("accept")){
            queuedQuest.put(player.getUniqueId(), quest);
        }


        switch (quest.toLowerCase()){
            case "helping_hand":{
                newPlayerQuest.openNewPlayerQuest(player);
                return;
            }
            case "sewer":{
                sewerQuest.openSewerQuest(player);
                return;
            }
            case "sewer2":{
                sewerQuest.openSewerQuest2(player);
                return;
            }
            case "lindwyrm":{
                lindwyrmQuest.openSewerQuest(player);
                return;
            }
        }


    }

    public void acceptQuest(Player player){

        if(!queuedQuest.containsKey(player.getUniqueId())){
            return;
        }

        switch (queuedQuest.get(player.getUniqueId()).toLowerCase()){
            case "helping_hand":{
                Location destination = new Location(player.getWorld(), -18, 84, -214);
                pathingManager.calculatePath(player, destination);
                player.closeInventory();

                profileManager.getAnyProfile(player).getMilestones().setMilestone("helping_hand.accept", true);
                break;
            }
            case "sewer":{

                Location destination = new Location(player.getWorld(), -11, 100, -289);
                pathingManager.calculatePath(player, destination);
                player.closeInventory();

                profileManager.getAnyProfile(player).getMilestones().setMilestone("sewer.accept", true);
                break;
            }
            case "sewer2":{

                Location destination = new Location(player.getWorld(), 57, 99, -292);
                pathingManager.calculatePath(player, destination);
                player.closeInventory();

                profileManager.getAnyProfile(player).getMilestones().setMilestone("sewer2.accept", true);
                break;
            }
            case "lindwyrm":{

                Location destination = new Location(player.getWorld(), 613, 98, -89);
                pathingManager.calculatePath(player, destination);
                player.closeInventory();

                break;
            }
            default:{
                Bukkit.getLogger().info("unknown quest");
                return;
            }
        }

    }


}
