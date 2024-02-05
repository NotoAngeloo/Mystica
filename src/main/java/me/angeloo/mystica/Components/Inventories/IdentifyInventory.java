package me.angeloo.mystica.Components.Inventories;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class IdentifyInventory {

    public IdentifyInventory(){

    }

    public Inventory openIdentifyInventory(ItemStack item){

        Inventory inv = Bukkit.createInventory(null, 9 * 3,"Identify");

        for(int i=0;i<27;i++){
            inv.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, 0, " "));
        }

        inv.setItem(13, item);

        inv.setItem(22, getItem(Material.LIME_DYE, 0, "Identify"));

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
