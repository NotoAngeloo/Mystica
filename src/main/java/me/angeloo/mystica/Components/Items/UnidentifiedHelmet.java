package me.angeloo.mystica.Components.Items;

import me.angeloo.mystica.Managers.ItemManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

import static me.angeloo.mystica.Mystica.*;

public class UnidentifiedHelmet {

    private final ItemManager manager;

    public UnidentifiedHelmet(ItemManager manager){
        this.manager = manager;
    }


    public ItemStack getUnidentifiedT1Helmet(int level){

        return manager.getItem(Material.IRON_INGOT, 1,
                ChatColor.of(commonColor) + "Unidentified Helmet",
                manager.buildCommonTop(2),
                ChatColor.of(commonColor) + "Unidentified Helmet",
                manager.buildCommonDivider(2),
                ChatColor.of(menuColor) + "Level: " + level,
                ChatColor.of(menuColor) + "Tier: 1",
                manager.buildCommonBottom(2));
    }

    public ItemStack getUnidentifiedT2Helmet(int level){

        return manager.getItem(Material.IRON_INGOT, 1,
                ChatColor.of(uncommonColor) + "Unidentified Helmet",
                manager.buildUncommonTop(2),
                ChatColor.of(uncommonColor) + "Unidentified Helmet",
                manager.buildUncommonDivider(2),
                ChatColor.of(menuColor) + "Level: " + level,
                ChatColor.of(menuColor) + "Tier: 2",
                manager.buildUncommonDivider(2),
                ChatColor.of(menuColor) + "Bonus Attribute",
                ChatColor.of(uncommonColor) + "Attack",
                ChatColor.of(uncommonColor) + "Health",
                ChatColor.of(uncommonColor) + "Defense",
                ChatColor.of(uncommonColor) + "Magic Defense",
                ChatColor.of(uncommonColor) + "Crit",
                manager.buildUncommonBottom(2));
    }

}
