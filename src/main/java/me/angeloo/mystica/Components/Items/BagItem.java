package me.angeloo.mystica.Components.Items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.angeloo.mystica.Mystica.rareColor;
import static me.angeloo.mystica.Mystica.uncommonColor;

public class BagItem extends StackableItem{


    public BagItem(int amount) {
        super(amount);
    }

    @Override
    public String identifier() {
        return "Bag";
    }



    @Override
    public Material getBaseMaterial(){
        return Material.LEATHER;
    }

    @Override
    public List<String> getLore() {
        List<String> lores = new ArrayList<>();
        return lores;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.of(rareColor) + "Bag";
    }

    @Override
    public int getCustomModelData() {
        return 1;
    }


    public static SoulStone deserialize(Map<String, Object> data){
        int amount = (int) data.getOrDefault("amount", 1);
        return new SoulStone(amount);
    }
}
