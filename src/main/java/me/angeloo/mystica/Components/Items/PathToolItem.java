package me.angeloo.mystica.Components.Items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;

public class PathToolItem extends ItemStack {

    public PathToolItem(){
        super(Material.BLAZE_ROD);
        ItemMeta meta = this.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.of(new Color(178, 144, 32)) + "Path Tool");
        this.setItemMeta(meta);
    }

}
