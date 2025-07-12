package me.angeloo.mystica.Components.Items;

import me.angeloo.mystica.Managers.ItemManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.angeloo.mystica.Mystica.uncommonColor;


public class SoulStone extends MysticaItem{

    public SoulStone(){

    }


    @Override
    public MysticaItemType type() {
        return MysticaItemType.OTHER;
    }

    @Override
    public String identifier() {
        return "Soul Stone";
    }

    @Override
    public ItemStack build() {

        ItemStack item = new ItemStack(Material.LAPIS_LAZULI);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(ChatColor.of(uncommonColor) + "Soul Stone");

        List<String> lores = new ArrayList<>();
        lores.add(ChatColor.of(Color.WHITE) + "Made from condensed mana force");
        lores.add("");
        lores.add(ChatColor.of(Color.WHITE) + "A useful material for enhancing equipment");
        meta.setLore(lores);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
