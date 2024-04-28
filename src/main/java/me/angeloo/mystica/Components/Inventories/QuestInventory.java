package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.ProfileComponents.Milestones;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class QuestInventory {

    private final ProfileManager profileManager;

    public QuestInventory(Mystica main){
        profileManager = main.getProfileManager();
    }

    public Inventory openQuestInventory(Player player, Integer index){

        Inventory inv = Bukkit.createInventory(player, 9 * 6, "Quests");

        //scroller
        inv.setItem(8, getItem(Material.ARROW, "Scroll Up"));
        inv.setItem(53, getItem(Material.ARROW, "Scroll Down"));

        Milestones milestones = profileManager.getAnyProfile(player).getMilestones();

        //put a counter for max items

        switch (index){
            case 0:{

                if(milestones.getMilestone("helping_hand_accept")){
                    inv.addItem(getItem(Material.BOOK, "A Helping Hand"));
                }

                if(milestones.getMilestone("sewer_accept")){
                    inv.addItem(getItem(Material.BOOK, "The Archbishop's Request"));
                }

                if(milestones.getMilestone("sewer2_accept")){
                    inv.addItem(getItem(Material.BOOK, "Heart of Corruption"));
                }

                if(milestones.getMilestone("lindwyrm_accept")){
                    inv.addItem(getItem(Material.BOOK, "Cave of the Lindwyrm"));
                }
                if(milestones.getMilestone("ho_lee_accept")){
                    inv.addItem(getItem(Material.BOOK, "The General's Arrival"));
                }



                break;
            }

        }

        return inv;
    }

    private ItemStack getItem(Material material, String name, String... lore){

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));

        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

}
