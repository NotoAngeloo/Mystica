package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Utility.InventoryItemGetter;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;


public class UpgradeInventory implements Listener {

    private final InventoryItemGetter inventoryItemGetter;


    public UpgradeInventory (Mystica main){
        inventoryItemGetter = main.getItemGetter();
    }

    public Inventory openUpgradeInventory(Player player){

        Inventory inv = Bukkit.createInventory(null, 9 * 6,ChatColor.WHITE + "\uF807" + "\uE0B7");



        //inv.setItem(22, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(24, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        return inv;
    }





}
