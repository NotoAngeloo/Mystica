package me.angeloo.mystica.Components.Inventories;


import me.angeloo.mystica.Components.Items.FastTravelItems;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FastTravelInv {

    private final ProfileManager profileManager;
    private final FastTravelItems fastTravelItems;

    public FastTravelInv(Mystica main){
        profileManager = main.getProfileManager();
        fastTravelItems = new FastTravelItems();
    }

    public Inventory openFastTravelInv(Player player, ItemStack selectedItem){

        Inventory inv = Bukkit.createInventory(null, 9 * 3,"Fast Travel");

        for(int i=0;i<27;i++){
            inv.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, 0, " "));
        }

        inv.setItem(1, fastTravelItems.teleportStonemont());


        if(profileManager.getAnyProfile(player).getMilestones().getMilestone("visitedloc1")){
            inv.setItem(2, fastTravelItems.teleportLindwyrm());
        }

        if(profileManager.getAnyProfile(player).getMilestones().getMilestone("visitedloc2")){
            inv.setItem(3, fastTravelItems.teleportWindbluff());
        }

        if(profileManager.getAnyProfile(player).getMilestones().getMilestone("visitedloc3")){
            inv.setItem(4,fastTravelItems.teleportOutpost());
        }


        inv.setItem(7, getItem(Material.BOOK, 0, "Set Respawn"));

        inv.setItem(21, selectedItem);

        if(selectedItem!=null){
            if(selectedItem.getType() != Material.AIR){
                inv.setItem(24, getItem(Material.LIME_DYE, 0,"Teleport"));
                inv.setItem(26, getItem(Material.YELLOW_DYE, 0, "Buy"));
            }
        }



        return inv;
    }

    private ItemStack getItem(Material material, int modelData, String name, String ... lore){

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }

}
