package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ClassEquipment.NoneEquipment;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayWeapons {

    private final ProfileManager profileManager;

    public DisplayWeapons(Mystica main){
        profileManager = main.getProfileManager();
    }

    public void displayArmor(Player player){

        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        if(playerEquipment.getHelmet() != null){
            player.getInventory().setHelmet(playerEquipment.getHelmet());
        }

        if(playerEquipment.getChestPlate() != null){
            player.getInventory().setChestplate(playerEquipment.getChestPlate());
        }


        if(playerEquipment.getLeggings() != null){
            player.getInventory().setLeggings(playerEquipment.getLeggings());
        }

        if(playerEquipment.getBoots() != null){
            player.getInventory().setBoots(playerEquipment.getBoots());
        }
    }

    public void unDisplayArmor(Player player){

    }

    public void displayWeapons(Player player){

        //maybe change color based on class

        ItemStack displayedWeapon = new NoneEquipment().getBaseWeapon();

        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        ItemStack weapon = equipment.getWeapon();

        if(weapon != null && !weapon.getType().isAir()){
            displayedWeapon = equipment.getWeapon().clone();
        }



        if(displayedWeapon.hasItemMeta() && displayedWeapon.getItemMeta().hasCustomModelData()){
            ItemMeta meta = displayedWeapon.getItemMeta();
            meta.setDisplayName(profileManager.getAnyProfile(player).getPlayerClass());
            List<String> lore = new ArrayList<>();

            lore.add(ChatColor.of(new Color(102, 0, 0)) + "Attack: " + profileManager.getAnyProfile(player).getTotalAttack());
            lore.add(ChatColor.of(new Color(0, 102, 0)) + "Health: " + profileManager.getAnyProfile(player).getTotalHealth());
            lore.add(ChatColor.of(new Color(153, 153, 0)) + "Defense: " + profileManager.getAnyProfile(player).getTotalDefense());
            lore.add(ChatColor.of(new Color(0, 102, 102)) + "Magic Defense: " + profileManager.getAnyProfile(player).getTotalMagicDefense());
            lore.add(ChatColor.of(new Color(255, 255, 255)) + "Crit: " + profileManager.getAnyProfile(player).getTotalCrit());


            meta.setLore(lore);
            int displayModel = meta.getCustomModelData();
            displayModel = displayModel + 2;
            meta.setCustomModelData(displayModel);
            displayedWeapon.setItemMeta(meta);
        }

        player.getInventory().setItemInOffHand(displayedWeapon);
    }


}
