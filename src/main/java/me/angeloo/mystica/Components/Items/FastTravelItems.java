package me.angeloo.mystica.Components.Items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;

public class FastTravelItems {

    public FastTravelItems(){

    }

    public ItemStack teleportStonemont(){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.of(new Color(0, 76, 153)) + "Teleport: Stonemont");
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack teleportLindwyrm(){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.of(new Color(153, 76, 0)) + "Teleport: Cave of the Lindwyrm");
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack teleportWindbluff(){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.of(new Color(255, 51, 51)) + "Teleport: Windbluff Prison");
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack teleportOutpost(){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.of(new Color(153, 153, 0)) + "Teleport: Traders Outpost");
        item.setItemMeta(meta);
        return item;
    }


}
