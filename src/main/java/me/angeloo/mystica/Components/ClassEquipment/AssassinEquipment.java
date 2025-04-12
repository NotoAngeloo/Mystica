package me.angeloo.mystica.Components.ClassEquipment;

import me.angeloo.mystica.Managers.ItemManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

import static me.angeloo.mystica.Mystica.assassinColor;
import static me.angeloo.mystica.Mystica.menuColor;

public class AssassinEquipment {

    private final ItemManager manager;

    public AssassinEquipment(ItemManager manager){
        this.manager = manager;
    }

    public ItemStack getBaseWeapon(int level) {

        return manager.getItem(Material.FLINT, 1,
                ChatColor.of(assassinColor) + "Dagger",
                manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Dagger" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + " + manager.getWeaponBaseAttack(level),
                ChatColor.of(Color.WHITE) + "Health + " + manager.getWeaponBaseHealth(level),
                ChatColor.of(Color.WHITE) + "Defense + " + manager.getWeaponBaseDefense(level),
                ChatColor.of(Color.WHITE) + "Magic Defense + " + manager.getWeaponBaseDefense(level),
                manager.buildCommonBottom(2));
    }

    public ItemStack getBaseHelmet(int level) {

        return manager.getItem(Material.CHAIN, 7,
                ChatColor.of(assassinColor) + "Assassin's Scarf",
                manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Scarf" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + " + manager.getHelmetBaseHealth(level),
                manager.buildCommonBottom(2));
    }

    public ItemStack getBaseChestPlate(int level) {

        return manager.getItem(Material.CHAINMAIL_CHESTPLATE, 7,
                ChatColor.of(assassinColor) + "Assassin's Tunic",
                manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Tunic" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + " + manager.getChestBaseHealth(level),
                ChatColor.of(Color.WHITE) + "Defense + " + manager.getChestBaseDefense(level),
                ChatColor.of(Color.WHITE) + "Magic Defense + " + manager.getChestBaseDefense(level),
                manager.buildCommonBottom(2));
    }

    public ItemStack getBaseLeggings(int level) {

        return manager.getItem(Material.CHAINMAIL_LEGGINGS, 7,
                ChatColor.of(assassinColor) + "Assassin's Breeches",
                manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Breeches" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + " + manager.getLeggingBaseAttack(level),
                manager.buildCommonBottom(2));
    }

    public ItemStack getBaseBoots(int level) {

        return manager.getItem(Material.CHAINMAIL_BOOTS, 7,
                ChatColor.of(assassinColor) + "Assassin's Boots",
                manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Boots" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: " + level,
                manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + " + manager.getBootsBaseAttack(level),
                manager.buildCommonBottom(2));
    }


}
