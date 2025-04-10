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

import static me.angeloo.mystica.Mystica.menuColor;

public class NoneEquipment {

    private final ItemManager manager;

    public NoneEquipment(ItemManager manager) {
        this.manager = manager;
    }

    public ItemStack getBaseWeapon(){

        return manager.getItem(Material.KELP, 1,
                ChatColor.of(Color.WHITE) + "Empty Hand",
                ChatColor.of(menuColor) + "Level: " + ChatColor.of(Color.WHITE) + "0");
    }


}
