package me.angeloo.mystica.Components.Items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.angeloo.mystica.Mystica.epicColor;

public class MysticalCrystal extends StackableItem{

    public MysticalCrystal(int amount){
        super(amount);
    }

    @Override
    public String identifier() {
        return "Mystical Crystal";
    }

    @Override
    public boolean questItem() {
        return true;
    }


    @Override
    public Material getBaseMaterial(){
        return Material.AMETHYST_SHARD;
    }

    @Override
    public List<String> getLore() {
        List<String> lores = new ArrayList<>();
        lores.add(ChatColor.of(Color.WHITE) + "You feel a great energy emminating");
        lores.add(ChatColor.of(Color.WHITE) + "from within");
        lores.add("");
        lores.add("");
        lores.add(ChatColor.of(Color.WHITE) + "Bring this to Gaellaman");
        lores.add(ChatColor.of(epicColor) + "(" + amount + ")");
        return lores;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.of(epicColor) + "Mystical Crystal";
    }

    @Override
    public int getCustomModelData() {
        return 1;
    }


    public static MysticalCrystal deserialize(Map<String, Object> data){
        int amount = (int) data.getOrDefault("amount", 1);
        return new MysticalCrystal(amount);
    }

}
