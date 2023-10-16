package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EquipmentInventory {

    private final ProfileManager profileManager;

    public EquipmentInventory(Mystica main){
        profileManager = main.getProfileManager();
    }

    public Inventory openEquipmentInventory(Player player, ItemStack actionItem, boolean fromTop){

        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        Inventory inv = Bukkit.createInventory(null, 9*3, player.getName() + "'s Equipment");

        for(int i=0;i<27;i++){

            if(i== 10){
                continue;
            }

            inv.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        inv.setItem(5, playerEquipment.getWeapon());
        inv.setItem(7, playerEquipment.getOffhand());
        inv.setItem(13, playerEquipment.getHelmet());
        inv.setItem(14, playerEquipment.getChestPlate());
        inv.setItem(15, playerEquipment.getLeggings());
        inv.setItem(16, playerEquipment.getBoots());

        if(actionItem == null){
            return inv;
        }

        inv.setItem(10, actionItem);

        inv.setItem(19, getAddItem());

        if(fromTop){
            inv.setItem(19, getRemoveItem());
        }


        //get parameters
        //more types of equipment later

        return inv;
    }

    private ItemStack getItem(Material material, String name, String ... lore){
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getRemoveItem(){
        return getItem(Material.RED_DYE,
                "Remove");
    }

    private ItemStack getAddItem(){
        return getItem(Material.LIME_DYE,
                "Equip");
    }

}
