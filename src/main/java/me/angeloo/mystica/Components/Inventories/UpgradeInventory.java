package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UpgradeInventory {

    private final EquipmentManager equipmentManager;

    public UpgradeInventory (Mystica main){
        equipmentManager = new EquipmentManager(main);
    }

    public Inventory openUpgradeInventory(Player player, ItemStack selected, ItemStack oldItem, ItemStack fodder){

        Inventory inv = Bukkit.createInventory(null, 9 * 3,"Upgrade");

        /*for(int i=0;i<27;i++){
            inv.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, 0, " "));
        }

        inv.setItem(9, selected);

        inv.setItem(11, oldItem);

        if(oldItem.getType() == Material.AIR){
            inv.setItem(20, getItem(Material.YELLOW_DYE, 0, "Old Item"));
        }
        else{
            inv.setItem(20, getItem(Material.LIME_DYE, 0, "Old Item"));
        }

        inv.setItem(13, fodder);

        if(fodder.getType() == Material.AIR){
            inv.setItem(22, getItem(Material.YELLOW_DYE, 0, "Fodder"));
        }
        else{
            inv.setItem(22, getItem(Material.LIME_DYE, 0, "Fodder"));
        }


        inv.setItem(15, new ItemStack(Material.AIR));

        //calculate what 15 should be, only if old and fodder exist, and fodder level is higher than old level

        if(oldItem.getType() != Material.AIR && fodder.getType() != Material.AIR){

            //but only calculate if new > old

            int oldLevel = equipmentManager.getItemLevel(oldItem);
            int newLevel = equipmentManager.getItemLevel(fodder);

            if(newLevel > oldLevel){
                inv.setItem(15, equipmentManager.upgrade(oldItem, newLevel));
            }
            else{
                inv.setItem(15, getItem(Material.RED_DYE, 0,"",
                        "Fodder item must be",
                        "higher level"));
            }

        }*/

        return inv;
    }

    private ItemStack getItem(Material material, int modelData, String name, String ... lore){

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }

}
