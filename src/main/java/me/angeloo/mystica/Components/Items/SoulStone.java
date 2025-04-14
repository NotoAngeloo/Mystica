package me.angeloo.mystica.Components.Items;

import me.angeloo.mystica.Managers.ItemManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


import java.awt.*;

import static me.angeloo.mystica.Mystica.uncommonColor;


public class SoulStone{

    private final ItemManager manager;

    public SoulStone(ItemManager manager){
        this.manager = manager;
    }

    public ItemStack getSoulStone(){
        return manager.getStackableItem(Material.LAPIS_LAZULI, 1,
                ChatColor.of(uncommonColor) + "Soul Stone",
                ChatColor.of(Color.WHITE) + "Made from condensed mana force",
                "",
                ChatColor.of(Color.WHITE) + "A useful material for enhancing equipment");
    }
}
