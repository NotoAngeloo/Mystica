package me.angeloo.mystica.Components.Items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static me.angeloo.mystica.Mystica.soulstoneColor;

public class SoulStone extends ItemStack {

    public SoulStone() {
        super(Material.LAPIS_LAZULI);
        ItemMeta meta = this.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.of(soulstoneColor) + "Soul Stone");
        this.setItemMeta(meta);
    }
}
