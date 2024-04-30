package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.Quests.HoLeeQuest;
import me.angeloo.mystica.Components.Quests.LindwyrmQuest;
import me.angeloo.mystica.Components.Quests.NewPlayerQuest;
import me.angeloo.mystica.Components.Quests.SewerQuest;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.angeloo.mystica.Mystica.levelColor;
import static me.angeloo.mystica.Mystica.questColor;

public class QuestManager {

    private final ProfileManager profileManager;
    private final PathingManager pathingManager;

    private final NewPlayerQuest newPlayerQuest = new NewPlayerQuest();
    private final SewerQuest sewerQuest = new SewerQuest();
    private final LindwyrmQuest lindwyrmQuest = new LindwyrmQuest();
    private final HoLeeQuest hoLeeQuest = new HoLeeQuest();

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
                newPlayerQuest.openNewPlayerQuest(player, false);
                return;
            }
            case "sewer":{
                sewerQuest.openSewerQuest(player, false);
                return;
            }
            case "sewer2":{
                sewerQuest.openSewerQuest2(player, false);
                return;
            }
            case "lindwyrm":{
                lindwyrmQuest.openSewerQuest(player, false);
                return;
            }
            case "ho_lee":{
                hoLeeQuest.openHoLeeQuest(player, false);
                return;
            }
        }

    }

    public void rereadQuest(Player player, String quest){

        queuedQuest.put(player.getUniqueId(), quest);

        switch (quest.toLowerCase()){
            case "helping_hand":{
                newPlayerQuest.openNewPlayerQuest(player, true);
                return;
            }
            case "sewer":{
                sewerQuest.openSewerQuest(player, true);
                return;
            }
            case "sewer2":{
                sewerQuest.openSewerQuest2(player, true);
                return;
            }
            case "lindwyrm":{
                lindwyrmQuest.openSewerQuest(player, true);
                return;
            }
            case "ho_lee":{
                hoLeeQuest.openHoLeeQuest(player, true);
                return;
            }
        }
    }

    public void navigateQuest(Player player){

        switch (queuedQuest.get(player.getUniqueId()).toLowerCase()){
            case "helping_hand":{
                Location destination = new Location(player.getWorld(), -18, 84, -214);
                pathingManager.calculatePath(player, destination);
                player.closeInventory();
                break;
            }
            case "sewer":{
                Location destination = new Location(player.getWorld(), -11, 100, -289);
                pathingManager.calculatePath(player, destination);
                player.closeInventory();
                break;
            }
            case "sewer2":{

                Location destination = new Location(player.getWorld(), 61, 92, -299);
                pathingManager.calculatePath(player, destination);
                player.closeInventory();
                break;
            }
            case "lindwyrm":{
                Location destination = new Location(player.getWorld(), 613, 98, -89);
                pathingManager.calculatePath(player, destination);
                player.closeInventory();
                break;
            }
            case "ho_lee":{
                Location destination = new Location(player.getWorld(), -79, 69, 452);
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

    public void acceptQuest(Player player){

        if(!queuedQuest.containsKey(player.getUniqueId())){
            return;
        }

        switch (queuedQuest.get(player.getUniqueId()).toLowerCase()){
            case "helping_hand":{
                navigateQuest(player);
                profileManager.getAnyProfile(player).getMilestones().setMilestone(queuedQuest.get(player.getUniqueId()) + "_accept", true);
                player.sendMessage(net.md_5.bungee.api.ChatColor.of(questColor) + "Objective: " + ChatColor.RESET + "Speak with Captain Moon.");
                completeQuest(player, "helping_hand");
                break;
            }
            case "sewer":{
                navigateQuest(player);
                profileManager.getAnyProfile(player).getMilestones().setMilestone(queuedQuest.get(player.getUniqueId()) + "_accept", true);
                player.sendMessage(net.md_5.bungee.api.ChatColor.of(questColor) + "Objective: " + ChatColor.RESET + "Speak with Archbishop Hasbrudan.");
                completeQuest(player, "sewer");
                break;
            }
            case "sewer2":{
                navigateQuest(player);
                player.sendMessage(net.md_5.bungee.api.ChatColor.of(questColor) + "Objective: " + ChatColor.RESET + "Defeat the Heart of Corruption");
                break;
            }
            case "lindwyrm":{
                navigateQuest(player);
                break;
            }
            case "ho_lee":{
                navigateQuest(player);
                profileManager.getAnyProfile(player).getMilestones().setMilestone(queuedQuest.get(player.getUniqueId()) + "_accept", true);
                player.sendMessage(net.md_5.bungee.api.ChatColor.of(questColor) + "Objective: " + ChatColor.RESET + "Speak with Ho Lee.");
                completeQuest(player, "ho_lee");
                break;
            }
            default:{
                Bukkit.getLogger().info("unknown quest");
                return;
            }

        }

        profileManager.getAnyProfile(player).getMilestones().setMilestone(queuedQuest.get(player.getUniqueId()) + "_accept", true);

    }

    public void completeQuest(Player player, String quest){

        if(!profileManager.getAnyProfile(player).getMilestones().getMilestone(quest + "_accept")){
            return;
        }

        profileManager.getAnyProfile(player).getMilestones().setMilestone(quest + "_complete", true);

        switch (quest.toLowerCase()){
            case "sewer2":{
                player.sendMessage(net.md_5.bungee.api.ChatColor.of(questColor) + "Objective Complete: " + ChatColor.RESET + "Speak with Archbishop Hasbrudan.");
                break;
            }
        }

    }

    public void rewardQuest(Player player, String quest){

        if(!profileManager.getAnyProfile(player).getMilestones().getMilestone(quest + "_complete")){
            return;
        }

        switch (quest.toLowerCase()){
            case "sewer2":{
                player.sendMessage(net.md_5.bungee.api.ChatColor.of(questColor) + "Quest Complete");
                player.sendMessage(net.md_5.bungee.api.ChatColor.of(levelColor) + "Rewards: " + ChatColor.RESET + "$20");
                int bal = profileManager.getAnyProfile(player).getBal().getBal();
                profileManager.getAnyProfile(player).getBal().setBal(bal + 20);
                break;
            }
        }

        profileManager.getAnyProfile(player).getMilestones().setMilestone(quest + "_reward", true);
    }


}
