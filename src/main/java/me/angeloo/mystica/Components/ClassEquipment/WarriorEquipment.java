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

public class WarriorEquipment {

    private final ItemManager manager;

    public WarriorEquipment(ItemManager manager){
        this.manager = manager;
    }

    public ItemStack getBaseWeapon() {

        return manager.getItem(Material.BRICK, 1,
                ChatColor.of(warriorColor) + "Axe",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(warriorColor) + "Axe" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Warrior",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 3",
                ChatColor.of(Color.WHITE) + "Health + 18",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseHelmet() {

        return manager.getItem(Material.CHAIN, 6,
                ChatColor.of(warriorColor) + "Warrior's Helmet",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(warriorColor) + "Helmet" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Warrior",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + 50",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseChestPlate() {

        return manager.getItem(Material.CHAINMAIL_CHESTPLATE, 6,
                ChatColor.of(warriorColor) + "Warrior's Plate",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(warriorColor) + "Plate" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Warrior",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Health + 31",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseLeggings() {

        return manager.getItem(Material.CHAINMAIL_LEGGINGS, 6,
                ChatColor.of(warriorColor) + "Warrior's Breeches",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(warriorColor) + "Breeches" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Warrior",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 4",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }

    public ItemStack getBaseBoots() {

        return manager.getItem(Material.CHAINMAIL_BOOTS, 6,
                ChatColor.of(warriorColor) + "Warrior's Boots",
                ChatColor.WHITE + manager.buildCommonTop(2),
                ChatColor.of(warriorColor) + "Boots" + ChatColor.of(Color.WHITE) + " Tier 1",
                "",
                ChatColor.of(menuColor) + "Class: Warrior",
                ChatColor.of(menuColor) + "Level: 1",
                ChatColor.WHITE + manager.buildCommonDivider(2),
                ChatColor.of(Color.WHITE) + "Attack + 2",
                ChatColor.WHITE + manager.buildCommonBottom(2));
    }


}
