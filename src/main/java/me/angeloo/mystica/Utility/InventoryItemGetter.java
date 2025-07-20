package me.angeloo.mystica.Utility;



import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class InventoryItemGetter {


    public InventoryItemGetter() {

    }


    public ItemStack getNumberItem(String number, int wholeNumber){

        ItemStack item = new ItemStack(Material.EMERALD);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', String.valueOf(wholeNumber)));
        meta.setUnbreakable(true);


        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        //figure out if the number is over 99

        int modelData = 0;

        if(number.length() == 2 && number.startsWith("0")){
            int value = Integer.parseInt(number);

            if(value >=0 && value <= 9){
                modelData = 101 + value;
            }
        }
        else{
            modelData = Integer.parseInt(number) + 1;
        }

        meta.setCustomModelData(modelData);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItem(Material material, int modelData, String name, String ... lore) {


        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);

        List<String> lores = new ArrayList<>(Arrays.asList(lore));

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }


}
