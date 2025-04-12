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

public class ReforgeInventory {

    private final EquipmentManager equipmentManager;

    public ReforgeInventory(Mystica main){

        equipmentManager = new EquipmentManager(main);
    }

    public Inventory openReforgeInventory(Player player, ItemStack oldItem, boolean calculateNewItem){

        Inventory inv = Bukkit.createInventory(null, 9 * 3,"Reforge");

        /*for(int i=0;i<27;i++){
            inv.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, 0, " "));
        }

        inv.setItem(11, oldItem);

        inv.setItem(13, getItem(Material.LIME_DYE, 0, "Reforge"));

        inv.setItem(15, new ItemStack(Material.AIR));

        if(calculateNewItem){
            inv.setItem(15, equipmentManager.reforge(oldItem));
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
