package me.angeloo.mystica.Components.Guis.Misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GearSwapInventory {

    public GearSwapInventory(){

    }

    public Inventory openGearSwapInventory(){

        Inventory inv = Bukkit.createInventory(null, 9*3, "Convert Equipment");

        for(int i=18;i<27;i++){
            inv.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        inv.setItem(22, getItem(Material.LIME_DYE, "Swap"));

        return inv;
    }

    private ItemStack getItem(Material material, String name, String ... lore){
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
