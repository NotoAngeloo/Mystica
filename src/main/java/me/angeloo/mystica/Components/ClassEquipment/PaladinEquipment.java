package me.angeloo.mystica.Components.ClassEquipment;

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

public class PaladinEquipment {

    public PaladinEquipment(){

    }

    public ItemStack getBaseWeapon(){

        return getItem(Material.IRON_SWORD, 1,
                ChatColor.of(new Color(207, 214, 61)) + "Paladin's Sword",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Main Hand",
                "",
                ChatColor.of(new Color(255,255,255)) + "Attack + 3",
                ChatColor.of(new Color(255,255,255)) + "Health + 18",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(207, 214, 61)) + "Paladin");
    }

    public ItemStack getBaseOffhand(){
        return getItem(Material.IRON_SWORD, 2,
                ChatColor.of(new Color(207, 214, 61)) + "Paladin's Shield",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Secondary",
                "",
                ChatColor.of(new Color(255,255,255)) + "Health + 18",
                ChatColor.of(new Color(255,255,255)) + "Defense + 4",
                ChatColor.of(new Color(255,255,255)) + "Magic Defense + 4",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(207, 214, 61)) + "Paladin");
    }

    public ItemStack getBaseHelmet(){
        return getItem(Material.CHAIN, 5,
                ChatColor.of(new Color(207, 214, 61)) + "Paladin's Helmet",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Helmet",
                "",
                ChatColor.of(new Color(255,255,255)) + "Health + 50",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(207, 214, 61)) + "Paladin");
    }

    public ItemStack getBaseChestPlate(){
        return getItem(Material.CHAINMAIL_CHESTPLATE, 5,
                ChatColor.of(new Color(207, 214, 61)) + "Paladin's Plate",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Chestplate",
                "",
                ChatColor.of(new Color(255,255,255)) + "Health + 31",
                ChatColor.of(new Color(255,255,255)) + "Defense + 4",
                ChatColor.of(new Color(255,255,255)) + "Magic Defense + 4",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(207, 214, 61)) + "Paladin");
    }

    public ItemStack getBaseLeggings(){
        return getItem(Material.CHAINMAIL_LEGGINGS, 5,
                ChatColor.of(new Color(207, 214, 61)) + "Paladin's Breeches",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Leggings",
                "",
                ChatColor.of(new Color(255,255,255)) + "Attack + 4",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(207, 214, 61)) + "Paladin");
    }

    public ItemStack getBaseBoots(){
        return getItem(Material.CHAINMAIL_BOOTS, 5,
                ChatColor.of(new Color(207, 214, 61)) + "Paladin's Boots",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Boots",
                "",
                ChatColor.of(new Color(255,255,255)) + "Attack + 2",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(207, 214, 61)) + "Paladin");
    }

    private ItemStack getItem(Material material, int modelData, String name, String ... lore){

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroer);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }

}