package me.angeloo.mystica.Components.Items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RezItem extends ItemStack {

    public RezItem() {
        super(Material.ENDER_EYE);
        ItemMeta meta = this.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.of(new Color(0, 153, 0)) + "Revive");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.of(new Color(0, 153, 0)) + "Click to Revive");
        meta.setLore(lore);
        this.setItemMeta(meta);
    }
}
