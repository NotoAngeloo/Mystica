package me.angeloo.mystica.Components.ClassEquipment;

import me.angeloo.mystica.Managers.ItemManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


import java.awt.*;

import static me.angeloo.mystica.Mystica.*;

public class ShadowKnightEquipment {

    private final ItemManager manager;

    public ShadowKnightEquipment(ItemManager manager){
        this.manager = manager;
    }

    public ItemStack getBaseWeapon() {

        return manager.getItem(Material.DIAMOND_SWORD, 1,
                ChatColor.of(shadowKnightColor) + "Greatsword",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(shadowKnightColor) + "Greatsword" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Shadow Knight",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 3",
                ChatColor.of(Color.WHITE) + "Health + 18",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseHelmet() {

        return manager.getItem(Material.CHAIN, 4,
                ChatColor.of(shadowKnightColor) + "Shadow Knight's Helmet",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(shadowKnightColor) + "Helmet" + ChatColor.of(Color.WHITE) + " vTier 1",
                "",
                ChatColor.of(menuColor) + "Class: Shadow Knight",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + 50",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseChestPlate() {

        return manager.getItem(Material.CHAINMAIL_CHESTPLATE, 4,
                ChatColor.of(shadowKnightColor) + "Shadow Knight's Plate",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(shadowKnightColor) + "Plate" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Shadow Knight",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + 31",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseLeggings() {

        return manager.getItem(Material.CHAINMAIL_LEGGINGS, 4,
                ChatColor.of(shadowKnightColor) + "Shadow Knight's Breeches",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(shadowKnightColor) + "Breeches" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Shadow Knight",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseBoots() {

        return manager.getItem(Material.CHAINMAIL_BOOTS, 4,
                ChatColor.of(shadowKnightColor) + "Shadow Knight's Boots",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(shadowKnightColor) + "Boots" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Shadow Knight",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 2",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }


}
