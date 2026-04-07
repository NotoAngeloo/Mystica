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

import static me.angeloo.mystica.Mystica.uncommonColor;


public class SoulStone extends StackableItem{


    public SoulStone(int amount) {
        super(amount);
    }

    @Override
    public String identifier() {
        return "Soul Stone";
    }

    @Override
    public boolean questItem() {
        return false;
    }


    @Override
    public Material getBaseMaterial(){
        return Material.LAPIS_LAZULI;
    }

    @Override
    public List<String> getLore() {
        List<String> lores = new ArrayList<>();
        lores.add(ChatColor.of(Color.WHITE) + "Made from condensed mana force");
        lores.add("");
        lores.add(ChatColor.of(Color.WHITE) + "A useful material for enhancing equipment");
        lores.add("");
        lores.add(ChatColor.of(uncommonColor) + "(" + amount + ")");
        return lores;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.of(uncommonColor) + "Soul Stone";
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
