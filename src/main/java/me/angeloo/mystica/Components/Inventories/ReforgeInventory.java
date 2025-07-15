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
import java.util.List;

import static me.angeloo.mystica.Mystica.uncommonColor;

public class ReforgeInventory implements Listener {

    private final EquipmentManager equipmentManager;


    public ReforgeInventory(Mystica main){
        equipmentManager = new EquipmentManager(main);
        ItemManager manager = main.getItemManager();
    }

    public Inventory openReforgeInventory(Player player){

        Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF807" + "\uE0B1" + "\uF80D" + "\uF82B\uF829" +"\uE0B2");


        return inv;
    }



    private int getRequired(ItemStack equipment){

        int tier = equipmentManager.getEquipmentTier(equipment);

        return tier * 2;
    }



}
