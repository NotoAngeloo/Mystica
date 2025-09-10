package me.angeloo.mystica.Components.Guis.Misc;

import io.lumine.mythic.api.mobs.MythicMob;
import me.angeloo.mystica.Components.PlayerProfile;
import me.angeloo.mystica.Components.Quests.Inventories.PickQuestInventory;
import me.angeloo.mystica.Components.Quests.Inventories.QuestAcceptInventory;
import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;
import me.angeloo.mystica.Components.Quests.Quest;
import me.angeloo.mystica.Components.Quests.QuestManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.network.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class ShopOrQuest implements Listener {

    private final ProfileManager profileManager;
    private final QuestAcceptInventory questAcceptInventory;
    private final QuestManager questManager;
    private final PickQuestInventory pickQuestInventory;
    private final DisplayWeapons displayWeapons;

    private final Map<UUID, MythicMob> currentNpc = new HashMap<>();

    public ShopOrQuest(Mystica main){
        profileManager = main.getProfileManager();
        questAcceptInventory = main.getQuestAcceptInventory();
        questManager = main.getQuestManager();
        pickQuestInventory = main.getPickQuestInventory();
        displayWeapons = main.getDisplayWeapons();
    }

    //put mm in map to open the quest gui later
    public void open(Player player, MythicMob npc){


        //if can turn in a quest, do that instead
        String giverId = npc.getInternalName();

        List<Quest> quests = questManager.getQuestsForNpc(giverId);
        PlayerProfile profile = (PlayerProfile) profileManager.getAnyProfile(player);
        for(Quest quest : quests){

            QuestProgress progress = profile.getQuestProgressMap().get(quest.getId());

            if(progress == null){
                continue;
            }

            if(progress.isComplete()){
                questAcceptInventory.openQuestAccept(player, quest);
                return;
            }
        }


        String title = ChatColor.WHITE + "\uF807" + "\uE87F" + "\uF828";

        Inventory inv = Bukkit.createInventory(null, 9*6, title);

        player.openInventory(inv);
        player.getInventory().clear();
        displayWeapons.displayArmor(player);

        currentNpc.put(player.getUniqueId(), npc);
    }

    @EventHandler
    public void shopOrQuestClick(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE87F")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        int slot = event.getSlot();

        if(event.getClickedInventory() == bottomInv){

            Bukkit.getLogger().info(String.valueOf(slot));

            List<Integer> questSlots = new ArrayList<>();
            questSlots.add(32);
            questSlots.add(33);
            questSlots.add(34);
            questSlots.add(35);

            if(questSlots.contains(slot)){

                if(getNpc(player) == null){
                    return;
                }

                pickQuestInventory.open(player, getNpc(player));

            }

        }

    }


    private MythicMob getNpc(Player player){
        return currentNpc.get(player.getUniqueId());
    }

}
