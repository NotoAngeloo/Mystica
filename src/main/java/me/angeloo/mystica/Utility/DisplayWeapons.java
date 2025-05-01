package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayWeapons {

    private final ProfileManager profileManager;
    private final ItemManager itemManager;

    public DisplayWeapons(Mystica main){
        profileManager = main.getProfileManager();
        itemManager = main.getItemManager();
    }

    public void displayArmor(Player player){

        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        player.getInventory().setHelmet(playerEquipment.getHelmet());

        player.getInventory().setChestplate(playerEquipment.getChestPlate());


        player.getInventory().setLeggings(playerEquipment.getLeggings());

        player.getInventory().setBoots(playerEquipment.getBoots());

        ItemStack displayedWeapon = itemManager.getNoneEquipment().getBaseWeapon();

        if(playerEquipment.getWeapon() != null){
            displayedWeapon = playerEquipment.getWeapon().clone();
            ItemMeta meta = displayedWeapon.getItemMeta();
            assert meta != null;
            int displayModel = meta.getCustomModelData();
            displayModel = displayModel + 2;
            meta.setCustomModelData(displayModel);
            displayedWeapon.setItemMeta(meta);
        }

        player.getInventory().setItemInOffHand(displayedWeapon);
    }


}
