package me.angeloo.mystica.Utility;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItemConverter {

    public CustomItemConverter(){
    }

    public ItemStack convert(ItemStack item, int amount){

        ItemStack convertedItem = new ItemStack(item.getType());
        ItemMeta itemMeta = item.getItemMeta();;
        convertedItem.setItemMeta(itemMeta);
        convertedItem.setAmount(amount);
        return convertedItem;
    }

}
