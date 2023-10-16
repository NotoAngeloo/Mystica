package me.angeloo.mystica.Components.Items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SoulStone extends ItemStack {

    public SoulStone() {
        super(Material.LAPIS_LAZULI);
        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName("§6Soul Stone");
        List<String> lore = new ArrayList<>();
        lore.add("The Lifeblood of Stonemont"); // Set the lore
        meta.setLore(lore);
        this.setItemMeta(meta);
    }
}
