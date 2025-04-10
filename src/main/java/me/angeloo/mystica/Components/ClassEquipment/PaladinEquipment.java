package me.angeloo.mystica.Components.ClassEquipment;

import me.angeloo.mystica.Managers.ItemManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.angeloo.mystica.Mystica.*;
import static me.angeloo.mystica.Mystica.assassinColor;

public class PaladinEquipment {

    private final ItemManager manager;

    public PaladinEquipment(ItemManager manager){
        this.manager = manager;
    }

    public ItemStack getBaseWeapon() {

        return manager.getItem(Material.IRON_SWORD, 1,
                ChatColor.of(paladinColor) + "Sword",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(paladinColor) + "Sword" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Paladin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 3",
                ChatColor.of(Color.WHITE) + "Health + 18",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseHelmet() {

        return manager.getItem(Material.CHAIN, 5,
                ChatColor.of(paladinColor) + "Paladin's Helmet",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(paladinColor) + "Helmet" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Paladin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + 50",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseChestPlate() {

        return manager.getItem(Material.CHAINMAIL_CHESTPLATE, 5,
                ChatColor.of(paladinColor) + "Paladin's Plate",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(paladinColor) + "Plate" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Paladin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + 31",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseLeggings() {

        return manager.getItem(Material.CHAINMAIL_LEGGINGS, 5,
                ChatColor.of(paladinColor) + "Paladin's Breeches",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(paladinColor) + "Breeches" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Paladin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseBoots() {

        return manager.getItem(Material.CHAINMAIL_BOOTS, 5,
                ChatColor.of(paladinColor) + "Paladin's Boots",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(paladinColor) + "Boots" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Paladin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 2",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }



}
