package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class UpgradeInventory implements Listener {

    private final ItemManager itemManager;
    private final EquipmentManager equipmentManager;


    public UpgradeInventory (Mystica main){
        itemManager = main.getItemManager();
        equipmentManager = main.getEquipmentManager();
    }

    public Inventory openUpgradeInventory(Player player){

        Inventory inv = Bukkit.createInventory(null, 9 * 6,ChatColor.WHITE + "\uF807" + "\uE0B7");



        //inv.setItem(22, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(24, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        return inv;
    }



    private boolean fodderHigherLevel(ItemStack upgradeItem, ItemStack fodder){

        int level = equipmentManager.getItemLevel(upgradeItem);
        int fodderLevel = equipmentManager.getItemLevel(fodder);

        return fodderLevel > level;
    }

    private int getRequired(ItemStack equipment, ItemStack fodder){

        int level = equipmentManager.getItemLevel(fodder);
        int tier = equipmentManager.getEquipmentTier(equipment);

        return level * ((tier * 3) - 2);
    }

}
