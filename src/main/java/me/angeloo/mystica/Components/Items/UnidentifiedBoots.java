package me.angeloo.mystica.Components.Items;

import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.angeloo.mystica.Mystica.*;
import static me.angeloo.mystica.Mystica.levelColor;

public class UnidentifiedBoots {

    private final ItemManager manager;

    public UnidentifiedBoots(ItemManager manager){
        this.manager = manager;
    }


    public ItemStack getUnidentifiedT1Boots(int level){

        return manager.getItem(Material.IRON_INGOT, 1,
                ChatColor.of(commonColor) + "Unidentified Boots",
                manager.buildCommonTop(2),
                ChatColor.of(commonColor) + "Unidentified Boots",
                manager.buildCommonDivider(2),
                ChatColor.of(menuColor) + "Level: " + level,
                ChatColor.of(menuColor) + "Tier: 1",
                manager.buildCommonBottom(2));
    }

    public ItemStack getUnidentifiedT2Boots(int level){

        return manager.getItem(Material.IRON_INGOT, 1,
                ChatColor.of(uncommonColor) + "Unidentified Boots",
                manager.buildUncommonTop(2),
                ChatColor.of(uncommonColor) + "Unidentified Boots",
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
