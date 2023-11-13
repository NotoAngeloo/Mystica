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

public class ShadowKnightEquipment {

    public ShadowKnightEquipment(){

    }

    public ItemStack getBaseWeapon(){

        return getItem(Material.DIAMOND_SWORD, 1,
                ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight's Broadsword",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Main Hand",
                "",
                ChatColor.of(new Color(255,255,255)) + "Attack + 3",
                ChatColor.of(new Color(255,255,255)) + "Health + 18",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight");
    }

    public ItemStack getBaseOffhand(){
        return getItem(Material.DIAMOND_SWORD, 2,
                ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight's Blade",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Secondary",
                "",
                ChatColor.of(new Color(255,255,255)) + "Health + 18",
                ChatColor.of(new Color(255,255,255)) + "Defense + 4",
                ChatColor.of(new Color(255,255,255)) + "Magic Defense + 4",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight");
    }

    public ItemStack getBaseHelmet(){
        return getItem(Material.CHAIN, 4,
                ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight's Helmet",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Helmet",
                "",
                ChatColor.of(new Color(255,255,255)) + "Health + 50",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight");
    }

    public ItemStack getBaseChestPlate(){
        return getItem(Material.CHAINMAIL_CHESTPLATE, 4,
                ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight's Plate",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Chestplate",
                "",
                ChatColor.of(new Color(255,255,255)) + "Health + 31",
                ChatColor.of(new Color(255,255,255)) + "Defense + 4",
                ChatColor.of(new Color(255,255,255)) + "Magic Defense + 4",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight");
    }

    public ItemStack getBaseLeggings(){
        return getItem(Material.CHAINMAIL_LEGGINGS, 4,
                ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight's Breeches",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Leggings",
                "",
                ChatColor.of(new Color(255,255,255)) + "Attack + 4",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight");
    }

    public ItemStack getBaseBoots(){
        return getItem(Material.CHAINMAIL_BOOTS, 4,
                ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight's Boots",
                ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + "1",
                ChatColor.of(new Color(176, 159, 109)) + "Boots",
                "",
                ChatColor.of(new Color(255,255,255)) + "Attack + 2",
                "",
                ChatColor.of(new Color(176, 159, 109)) + "Requires " + ChatColor.of(new Color(213, 33, 3)) + "Shadow Knight");
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