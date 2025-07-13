package me.angeloo.mystica.Components.Items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class BagItem extends MysticaItem{


    @Override
    public MysticaItemFormat format() {
        return MysticaItemFormat.OTHER;
    }

    @Override
    public String identifier() {
        return "Bag";
    }

    @Override
    public ItemStack build() {

        ItemStack item = new ItemStack(Material.LEATHER);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setCustomModelData(1);
        meta.setDisplayName("Bag");

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
