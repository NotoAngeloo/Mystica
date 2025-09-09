package me.angeloo.mystica.Components.Quests.Inventories;

import io.lumine.mythic.api.mobs.MythicMob;
import me.angeloo.mystica.Components.PlayerProfile;
import me.angeloo.mystica.Components.Quests.Quest;
import me.angeloo.mystica.Components.Quests.QuestManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.InventoryItemGetter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PickQuestInventory implements Listener {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final QuestManager questManager;
    private final DisplayWeapons displayWeapons;
    private final QuestAcceptInventory questAcceptInventory;
    private QuestInventoryTextGenerator textGenerator;
    private InventoryItemGetter itemGetter;

    public PickQuestInventory(Mystica main, QuestAcceptInventory questAcceptInventory){
        this.main = main;
        profileManager = main.getProfileManager();
        questManager = main.getQuestManager();
        displayWeapons = main.getDisplayWeapons();
        this.questAcceptInventory = questAcceptInventory;
        textGenerator = questAcceptInventory.getTextGenerator();
        itemGetter = main.getItemGetter();
    }

    //which npc does it
    public void open(Player player, MythicMob questGiver){

        String giverId = questGiver.getInternalName();
        List<Quest> quests = questManager.getQuestsForNpc(giverId);
        PlayerProfile profile = (PlayerProfile) profileManager.getAnyProfile(player);

        StringBuilder title = new StringBuilder();

        //negative space before
        //-110
        title.append("\uF80B\uF80A\uF808\uF806");

        //parchment png
        title.append("\uE87A");

        //-256
        title.append("\uF80D");
        //-126
        title.append("\uF80B\uF80A\uF809\uF808\uF806");
        //name bar
        title.append("\uE87E");
        //-256
        title.append("\uF80D");
        //-100
        title.append("\uF80B\uF80A\uF804");

        title.append(getQuestText(questGiver));

        Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + String.valueOf(title));

        int invSlot = 9;
        for(Quest quest : quests){

            if(invSlot>=53){
                break;
            }

            if(quest.canStart(profile)){
                inv.setItem(invSlot, itemGetter.getItem(Material.EMERALD, 0, ChatColor.RESET + quest.getName()));
                invSlot++;
            }

        }


        player.openInventory(inv);
        player.getInventory().clear();
        displayWeapons.displayArmor(player);

        //player.getInventory().setItem(9, new ItemStack(Material.EMERALD));
        //player.getInventory().setItem(17, new ItemStack(Material.EMERALD));
        //player.getInventory().setItem(27, new ItemStack(Material.EMERALD));
        //player.getInventory().setItem(35, new ItemStack(Material.EMERALD));
    }



    private String getQuestText(MythicMob questGiver){
        List<String> invText = new ArrayList<>();

        String mobName = questGiver.getDisplayName().get();
        invText.add(mobName);

        return textGenerator.getInventoryText(invText);
    }

}
