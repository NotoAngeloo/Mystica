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

    public ItemStack getBaseWeapon() {

        return manager.getItem(Material.FLINT, 1,
                ChatColor.of(assassinColor) + "Dagger",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Dagger" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 3",
                ChatColor.of(Color.WHITE) + "Health + 18",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseHelmet() {

        return manager.getItem(Material.CHAIN, 7,
                ChatColor.of(assassinColor) + "Assassin's Scarf",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Scarf" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + 50",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseChestPlate() {

        return manager.getItem(Material.CHAINMAIL_CHESTPLATE, 7,
                ChatColor.of(assassinColor) + "Assassin's Tunic",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Tunic" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + 31",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseLeggings() {

        return manager.getItem(Material.CHAINMAIL_LEGGINGS, 7,
                ChatColor.of(assassinColor) + "Assassin's Breeches",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Breeches" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseBoots() {

        return manager.getItem(Material.CHAINMAIL_BOOTS, 7,
                ChatColor.of(assassinColor) + "Assassin's Boots",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(assassinColor) + "Boots" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Assassin",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 2",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }


}
