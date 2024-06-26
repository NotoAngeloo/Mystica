package me.angeloo.mystica.Components.Inventories;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ClassSelectInventory {

    public ClassSelectInventory(){

    }

    public Inventory openClassSwap(int index){
        Inventory inv = Bukkit.createInventory(null, 9 * 3,"Swap Class");

        //13 is mid

        inv.setItem(22, getItem(Material.LIME_DYE, 0,"Select"));

        inv.setItem(15, getItem(Material.ARROW,0,"Next"));

        inv.setItem(11, getItem(Material.ARROW, 0, "Previous"));


        switch (index){
            case 0:{
                inv.setItem(13, getAssassinItem());
                break;
            }
            case 1:{
                inv.setItem(13, getElementalistItem());
                break;
            }
            case 2:{
                inv.setItem(13, getMysticItem());
                break;
            }
            case 3:{
                inv.setItem(13, getPaladinItem());
                break;
            }
            case 4:{
                inv.setItem(13, getRangerItem());
                break;
            }
            case 5:{
                inv.setItem(13, getShadowKnightItem());
                break;
            }
            case 6:{
                inv.setItem(13, getWarriorItem());
                break;
            }
        }

        return inv;
    }

    public Inventory openClassSelect(int index){

        Inventory inv = Bukkit.createInventory(null, 9 * 3,"Select a Class");

        //13 is mid

        inv.setItem(22, getItem(Material.LIME_DYE, 0,"Select"));

        inv.setItem(15, getItem(Material.ARROW,0,"Next"));

        inv.setItem(11, getItem(Material.ARROW, 0, "Previous"));


        switch (index){
            case 0:{
                inv.setItem(13, getAssassinItem());
                break;
            }
            case 1:{
                inv.setItem(13, getElementalistItem());
                break;
            }
            case 2:{
                inv.setItem(13, getMysticItem());
                break;
            }
            case 3:{
                inv.setItem(13, getPaladinItem());
                break;
            }
            case 4:{
                inv.setItem(13, getRangerItem());
                break;
            }
            case 5:{
                inv.setItem(13, getShadowKnightItem());
                break;
            }
            case 6:{
                inv.setItem(13, getWarriorItem());
                break;
            }
        }

        return inv;
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
                /*ChatColor.of(new Color(59, 14, 114)) + "Chaos",
                ChatColor.of(new Color(217, 217, 217)) + "Long Range Dps",
                "",*/
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

    private ItemStack getPaladinItem(){
        return getItem(Material.IRON_SWORD, 1, ChatColor.of(new Color(207, 214, 61)) + "Paladin",
                "",
                ChatColor.of(new Color(217, 217, 217)) + "Physical Damage Type",
                "",
                ChatColor.of(new Color(207, 180, 80)) + "Templar",
                ChatColor.of(new Color(217, 217, 217)) + "Tank",
                "",
                ChatColor.of(new Color(207, 214, 61)) + "Dawn",
                ChatColor.of(new Color(217, 217, 217)) + "Short Range Dps"/*,
                "",
                ChatColor.of(new Color(207, 180, 129)) + "Divine",
                ChatColor.of(new Color(217, 217, 217)) + "Medium Range Healer"*/
        );
    }

    private ItemStack getWarriorItem(){
        return getItem(Material.BRICK, 1, ChatColor.of(new Color(214, 126, 61)) + "Warrior",
                "",
                ChatColor.of(new Color(217, 217, 217)) + "Physical Damage Type",
                "",
                ChatColor.of(new Color(214, 126, 102)) + "Gladiator",
                ChatColor.of(new Color(217, 217, 217)) + "Tank",
                "",
                ChatColor.of(new Color(214, 126, 61)) + "Executioner",
                ChatColor.of(new Color(217, 217, 217)) + "Short Range Dps/Off Tank"
        );
    }

    private ItemStack getAssassinItem(){
        return getItem(Material.FLINT, 1, ChatColor.of(new Color(214, 61, 207)) + "Assassin",
                "",
                ChatColor.of(new Color(217, 217, 217)) + "Physical Damage Type",
                "",
                ChatColor.of(new Color(248, 61, 119)) + "Alchemist",
                ChatColor.of(new Color(217, 217, 217)) + "Short Range Damage Support",
                "",
                ChatColor.of(new Color(214, 61, 207)) + "Duelist",
                ChatColor.of(new Color(217, 217, 217)) + "Short Range Dps"
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
