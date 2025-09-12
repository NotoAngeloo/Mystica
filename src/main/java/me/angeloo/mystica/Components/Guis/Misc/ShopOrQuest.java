package me.angeloo.mystica.Components.Guis.Misc;

import io.lumine.mythic.api.mobs.MythicMob;
import me.angeloo.mystica.Components.Guis.Abilities.ClassSelectInventory;
import me.angeloo.mystica.Components.PlayerProfile;
import me.angeloo.mystica.Components.Quests.Inventories.PickQuestInventory;
import me.angeloo.mystica.Components.Quests.Inventories.QuestAcceptInventory;
import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;
import me.angeloo.mystica.Components.Quests.Quest;
import me.angeloo.mystica.Components.Quests.QuestManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.Enums.ShopType;
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
    private final ClassSelectInventory classSelectInventory;
    private final QuestAcceptInventory questAcceptInventory;
    private final QuestManager questManager;
    private final PickQuestInventory pickQuestInventory;
    private final DisplayWeapons displayWeapons;

    private final Map<UUID, MythicMob> currentNpc = new HashMap<>();

    private final Map<String, ShopType> shopForNpc = Map.ofEntries(
            Map.entry("ArchbishopNpc", ShopType.ClassSelect)
    );

    public ShopOrQuest(Mystica main){
        profileManager = main.getProfileManager();
        classSelectInventory = main.getClassSelectInventory();
        questAcceptInventory = main.getQuestAcceptInventory();
        questManager = main.getQuestManager();
        pickQuestInventory = main.getPickQuestInventory();
        displayWeapons = main.getDisplayWeapons();


    }

    //put mm in map to open the quest gui later
    public void open(Player player, MythicMob npc){


        String giverId = npc.getInternalName();

        List<Quest> quests = questManager.getQuestsForNpc(giverId);
        int questAmount = 0;
        PlayerProfile profile = (PlayerProfile) profileManager.getAnyProfile(player);
        for(Quest quest : quests){

            QuestProgress progress = profile.getQuestProgressMap().get(quest.getId());

            //has a quest that player has not started
            if(progress == null){
                questAmount++;
                continue;
            }

            if(progress.isRewarded()){
                continue;
            }

            //has a quest player finished (skip because if finish all just open inv)
            if(progress.isComplete()){
                questAcceptInventory.openQuestAccept(player, quest);
                return;
            }

            //has a quest the player HAS started
            questAmount++;
        }


        if(questAmount==0){
            //if has no quests, just open the regular inventory

            ShopType shopType = shopForNpc.get(giverId);

            if(shopType == null){
                return;
            }

            switch (shopType){
                case ClassSelect -> {
                    classSelectInventory.openClassSelect(player);
                    return;
                }
            }

            return;
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

        MythicMob npc = getNpc(player);

        int slot = event.getSlot();

        if(event.getClickedInventory() == bottomInv){

            //Bukkit.getLogger().info(String.valueOf(slot));

            List<Integer> questSlots = new ArrayList<>();
            questSlots.add(32);
            questSlots.add(33);
            questSlots.add(34);
            questSlots.add(35);

            if(questSlots.contains(slot)){

                if(getNpc(player) == null){
                    return;
                }

                pickQuestInventory.open(player, npc);
                return;
            }

            List<Integer> interactSlots = new ArrayList<>();
            interactSlots.add(14);
            interactSlots.add(15);
            interactSlots.add(16);
            interactSlots.add(17);

            if(interactSlots.contains(slot)){


                ShopType shopType = shopForNpc.get(npc.getInternalName());

                if(shopType == null){
                    return;
                }

                switch (shopType){
                    case ClassSelect -> {
                        classSelectInventory.openClassSelect(player);
                        return;
                    }
                }

                return;
            }

        }

    }


    private MythicMob getNpc(Player player){
        return currentNpc.get(player.getUniqueId());
    }

}
