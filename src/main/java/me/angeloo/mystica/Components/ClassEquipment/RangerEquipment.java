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

public class RangerEquipment {

    private final ItemManager manager;

    public RangerEquipment(ItemManager manager){
        this.manager = manager;
    }

    public ItemStack getBaseWeapon(int level) {

        return manager.getItem(Material.FEATHER, 1,
                ChatColor.of(rangerColor) + "Bow",
                manager.buildCommonTop(2),
                ChatColor.of(rangerColor) + "Bow" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Ranger",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + " + manager.getWeaponBaseAttack(level),
                ChatColor.of(Color.WHITE) + "Health + " + manager.getWeaponBaseHealth(level),
                ChatColor.of(Color.WHITE) + "Defense + " + manager.getWeaponBaseDefense(level),
                ChatColor.of(Color.WHITE) + "Magic Defense + " + manager.getWeaponBaseDefense(level),
                manager.buildCommonBottom(2));
    }

    public ItemStack getBaseHelmet(int level) {

        return manager.getItem(Material.CHAIN, 2,
                ChatColor.of(rangerColor) + "Ranger's Hood",
                manager.buildCommonTop(2),
                ChatColor.of(rangerColor) + "Hood" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Ranger",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + " + manager.getHelmetBaseHealth(level),
                manager.buildCommonBottom(2));
    }

    public ItemStack getBaseChestPlate(int level) {

        return manager.getItem(Material.CHAINMAIL_CHESTPLATE, 2,
                ChatColor.of(rangerColor) + "Ranger's Tunic",
                manager.buildCommonTop(2),
                ChatColor.of(rangerColor) + "Tunic" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Ranger",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + " + manager.getChestBaseHealth(level),
                ChatColor.of(Color.WHITE) + "Defense + " + manager.getWeaponBaseDefense(level),
                ChatColor.of(Color.WHITE) + "Magic Defense + " + manager.getChestBaseDefense(level),
                manager.buildCommonBottom(2));
    }

    public ItemStack getBaseLeggings(int level) {

        return manager.getItem(Material.CHAINMAIL_LEGGINGS, 2,
                ChatColor.of(rangerColor) + "Ranger's Breeches",
                manager.buildCommonTop(2),
                ChatColor.of(rangerColor) + "Breeches" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Ranger",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + " + manager.getLeggingBaseAttack(level),
                manager.buildCommonBottom(2));
    }

    public ItemStack getBaseBoots(int level) {

        return manager.getItem(Material.CHAINMAIL_BOOTS, 2,
                ChatColor.of(rangerColor) + "Ranger's Boots",
                manager.buildCommonTop(2),
                ChatColor.of(rangerColor) + "Boots" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Ranger",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + " + manager.getBootsBaseAttack(level),
                manager.buildCommonBottom(2));
    }


}
