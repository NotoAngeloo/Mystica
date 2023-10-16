package me.angeloo.mystica.Components.Inventories;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClassSelectInventory {

    public ClassSelectInventory(){

    }

    public Inventory openClassSelect(String clazz){

        Inventory inv = Bukkit.createInventory(null, 9 * 3,"Select a Class");

        inv.setItem(15, getItem(Material.ARROW, 0,ChatColor.of(new Color(0, 153, 51)) + "Select"));
        inv.setItem(11, getItem(Material.ARROW, 0,ChatColor.of(new Color(255, 153, 51)) + "Trial"));

        inv.setItem(4, getClassItem(clazz));


        if(!clazz.equalsIgnoreCase("elementalist")){
            inv.setItem(22, getElementalistItem());
        }

        if(!clazz.equalsIgnoreCase("ranger")){
            inv.setItem(23, getRangerItem());
        }

        return inv;
    }



    private ItemStack getClassItem(String clazz){

        switch(clazz.toLowerCase()){
            case "elementalist":{
                return getElementalistItem();
            }
            case "ranger":{
                return getRangerItem();
            }
            default:{
                return new ItemStack(Material.AIR);
            }
        }

    }

    private ItemStack getElementalistItem(){
        return getItem(Material.STICK, 1,ChatColor.of(new Color(153, 204, 255)) + "Elementalist",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Role " + ChatColor.of(new Color(217, 217, 217)) + "Ranged Magical Dps"
        );
    }

    private ItemStack getRangerItem(){
        return getItem(Material.BOW, 1, ChatColor.of(new Color(34, 111, 80)) + "Ranger",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Role " + ChatColor.of(new Color(217, 217, 217)) + "Ranged Physical Dps"
        );
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
