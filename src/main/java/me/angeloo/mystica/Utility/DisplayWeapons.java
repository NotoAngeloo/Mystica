package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ClassEquipment.ElementalistEquipment;
import me.angeloo.mystica.Components.ClassEquipment.TrialClassEquipment;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DisplayWeapons {

    private final ProfileManager profileManager;
    private final TrialClassEquipment trialClassEquipment;

    public DisplayWeapons(Mystica main){
        profileManager = main.getProfileManager();
        trialClassEquipment = new TrialClassEquipment();
    }

    public void displayWeapons(Player player){

        //maybe change color based on class

        ItemStack displayedWeapon = new ItemStack(Material.AIR);

        if(profileManager.getIfClassTrial(player)){

            String trialClass = profileManager.getTrialClass(player);
            displayedWeapon = trialClassEquipment.getTrialWeapon(trialClass);

            if(displayedWeapon.hasItemMeta() && displayedWeapon.getItemMeta().hasCustomModelData()){
                ItemMeta meta = displayedWeapon.getItemMeta();
                meta.setDisplayName(trialClass);
                meta.setLore(null);
                int displayModel = meta.getCustomModelData();
                displayModel = displayModel + 2;
                meta.setCustomModelData(displayModel);
                displayedWeapon.setItemMeta(meta);
            }


            player.getInventory().setItemInOffHand(displayedWeapon);
            return;
        }

        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        ItemStack weapon = equipment.getWeapon();

        if(weapon != null){
            displayedWeapon = equipment.getWeapon().clone();
        }



        if(displayedWeapon.hasItemMeta() && displayedWeapon.getItemMeta().hasCustomModelData()){
            ItemMeta meta = displayedWeapon.getItemMeta();
            meta.setDisplayName(profileManager.getAnyProfile(player).getPlayerClass());
            meta.setLore(null);
            int displayModel = meta.getCustomModelData();
            displayModel = displayModel + 2;
            meta.setCustomModelData(displayModel);
            displayedWeapon.setItemMeta(meta);
        }

        player.getInventory().setItemInOffHand(displayedWeapon);
    }


}
