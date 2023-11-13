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

        inv.setItem(4, getClassItem(clazz));

        if(!clazz.equalsIgnoreCase("mystic")){
            inv.setItem(21, getMysticItem());
        }

        if(!clazz.equalsIgnoreCase("elementalist")){
            inv.setItem(22, getElementalistItem());
        }

        if(!clazz.equalsIgnoreCase("ranger")){
            inv.setItem(23, getRangerItem());
        }

        if(!clazz.equalsIgnoreCase("shadow knight")){
            inv.setItem(20, getShadowKnightItem());
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
            case "mystic":{
                return getMysticItem();
            }
            case "shadow knight":{
                return getShadowKnightItem();
            }
            default:{
                return new ItemStack(Material.AIR);
            }
        }

    }

    private ItemStack getElementalistItem(){
        return getItem(Material.STICK, 1,ChatColor.of(new Color(153, 204, 255)) + "Elementalist",
                "",
                ChatColor.of(new Color(217, 217, 217)) + "Magical Damage Type",
                "",
                ChatColor.of(new Color(250, 102, 0)) + "Pyromancer",
                ChatColor.of(new Color(217, 217, 217)) + "Long Range Dps",
                "",
                ChatColor.of(new Color(153, 0, 255)) + "Conjurer",
                ChatColor.of(new Color(217, 217, 217)) + "Long Range Damage Support"
        );
    }

    private ItemStack getRangerItem(){
        return getItem(Material.FEATHER, 1, ChatColor.of(new Color(34, 111, 80)) + "Ranger",
                "",
                ChatColor.of(new Color(217, 217, 217)) + "Physical Damage Type",
                "",
                ChatColor.of(new Color(34, 111, 80)) + "Scout",
                ChatColor.of(new Color(217, 217, 217)) + "Long Range Dps",
                "",
                ChatColor.of(new Color(0, 117, 94)) + "Animal Tamer",
                ChatColor.of(new Color(217, 217, 217)) + "Long Range Damage Support"
        );
    }

    private ItemStack getMysticItem(){
        return getItem(Material.BLAZE_ROD, 1,ChatColor.of(new Color(155, 120, 197)) + "Mystic",
                "",
                ChatColor.of(new Color(217, 217, 217)) + "Magic Damage Type",
                "",
                ChatColor.of(new Color(126, 101, 238)) + "Shepard",
                ChatColor.of(new Color(217, 217, 217)) + "Long Range Healer",
                "",
                ChatColor.of(new Color(59, 14, 114)) + "Chaos",
                ChatColor.of(new Color(217, 217, 217)) + "Long Range Dps",
                "",
                ChatColor.of(new Color(155, 120, 197)) + "Arcane Master",
                ChatColor.of(new Color(217, 217, 217)) + "Long Range Hybrid Dps"
        );
    }

    private ItemStack getShadowKnightItem(){
        return getItem(Material.DIAMOND_SWORD, 1, ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight",
                "",
                ChatColor.of(new Color(217, 217, 217)) + "Physical Damage Type",
                "",
                ChatColor.of(new Color(213, 33, 3)) + "Blood",
                ChatColor.of(new Color(217, 217, 217)) + "Tank",
                "",
                ChatColor.of(new Color(3, 7, 219)) + "Doom",
                ChatColor.of(new Color(217, 217, 217)) + "Medium Range Dps"
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
