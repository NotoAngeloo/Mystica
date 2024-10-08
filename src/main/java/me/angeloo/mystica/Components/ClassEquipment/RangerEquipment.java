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

import static me.angeloo.mystica.Mystica.menuColor;
import static me.angeloo.mystica.Mystica.rangerColor;

public class RangerEquipment {

    public RangerEquipment(){

    }

    public ItemStack getBaseWeapon(){

        return getItem(Material.FEATHER, 1,
                ChatColor.of(rangerColor) + "Ranger's Bow",
                ChatColor.of(menuColor) + "Level: " + ChatColor.of(Color.WHITE) + "1",
                ChatColor.of(menuColor) + "Weapon",
                "",
                ChatColor.of(Color.WHITE) + "Attack + 3",
                ChatColor.of(Color.WHITE) + "Health + 18",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                "",
                ChatColor.of(menuColor) + "Requires " + ChatColor.of(rangerColor) + "Ranger");
    }


    public ItemStack getBaseHelmet(){
        return getItem(Material.CHAIN, 2,
                ChatColor.of(rangerColor) + "Ranger's Hood",
                ChatColor.of(menuColor) + "Level: " + ChatColor.of(Color.WHITE) + "1",
                ChatColor.of(menuColor) + "Helmet",
                "",
                ChatColor.of(Color.WHITE) + "Health + 50",
                "",
                ChatColor.of(menuColor) + "Requires " + ChatColor.of(rangerColor) + "Ranger");
    }

    public ItemStack getBaseChestPlate(){
        return getItem(Material.CHAINMAIL_CHESTPLATE, 2,
                ChatColor.of(rangerColor) + "Ranger's Tunic",
                ChatColor.of(menuColor) + "Level: " + ChatColor.of(Color.WHITE) + "1",
                ChatColor.of(menuColor) + "Chestplate",
                "",
                ChatColor.of(Color.WHITE) + "Health + 31",
                ChatColor.of(Color.WHITE) + "Defense + 4",
                ChatColor.of(Color.WHITE) + "Magic Defense + 4",
                "",
                ChatColor.of(menuColor) + "Requires " + ChatColor.of(rangerColor) + "Ranger");
    }

    public ItemStack getBaseLeggings(){
        return getItem(Material.CHAINMAIL_LEGGINGS, 2,
                ChatColor.of(rangerColor) + "Ranger's Breeches",
                ChatColor.of(menuColor) + "Level: " + ChatColor.of(Color.WHITE) + "1",
                ChatColor.of(menuColor) + "Leggings",
                "",
                ChatColor.of(Color.WHITE) + "Attack + 4",
                "",
                ChatColor.of(menuColor) + "Requires " + ChatColor.of(rangerColor) + "Ranger");
    }

    public ItemStack getBaseBoots(){
        return getItem(Material.CHAINMAIL_BOOTS, 2,
                ChatColor.of(rangerColor) + "Ranger's Boots",
                ChatColor.of(menuColor) + "Level: " + ChatColor.of(Color.WHITE) + "1",
                ChatColor.of(menuColor) + "Boots",
                "",
                ChatColor.of(Color.WHITE) + "Attack + 2",
                "",
                ChatColor.of(menuColor) + "Requires " + ChatColor.of(rangerColor) + "Ranger");
    }

    private ItemStack getItem(Material material, int modelData, String name, String ... lore){

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setUnbreakable(true);

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
