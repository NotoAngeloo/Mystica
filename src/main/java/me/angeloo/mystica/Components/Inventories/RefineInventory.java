package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RefineInventory implements Listener {

    private final EquipmentManager equipmentManager;


    public RefineInventory(Mystica main){
        equipmentManager = new EquipmentManager(main);
        ItemManager manager = main.getItemManager();

    }

    public Inventory openRefineInventory(Player player){

        Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF807" + "\uE0B4" + "\uF80D" + "\uF82B\uF829" +"\uE0B5");


        return inv;
    }

    private int stoneCount(Player player){

        int count = 0;
        for(ItemStack item : player.getInventory().getContents()){
            if(item == null){
                continue;
            }

            if(!item.hasItemMeta()){
                continue;
            }

            ItemMeta meta = item.getItemMeta();

            assert meta != null;
            if(!meta.hasDisplayName()){
                continue;
            }


        }
        return count;
    }

    private boolean validItem(ItemStack item){

        if(!item.hasItemMeta()){
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if(!meta.hasLore()){
            return false;
        }

        List<String> lores = meta.getLore();
        assert lores != null;
        for(String loreLine : lores){
            if(loreLine.contains("Special Attribute")){
                return true;
            }
        }

        return false;
    }

    private int getRequired(ItemStack equipment){

        int tier = equipmentManager.getEquipmentTier(equipment);

        return tier * 2;
    }



}
