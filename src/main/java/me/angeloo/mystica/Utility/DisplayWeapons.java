package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DisplayWeapons {

    private final MysticaEquipment none;
    private final ProfileManager profileManager;

    public DisplayWeapons(Mystica main){
        profileManager = main.getProfileManager();
        none = new MysticaEquipment(EquipmentSlot.WEAPON, PlayerClass.NONE, 1);
    }

    public void displayArmor(Player player){

        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        if(playerEquipment.getHelmet() != null){
            player.getInventory().setHelmet(playerEquipment.getHelmet().build());
        }

        if(playerEquipment.getChestPlate() != null){
            player.getInventory().setChestplate(playerEquipment.getChestPlate().build());
        }

        if(playerEquipment.getLeggings() != null){
            player.getInventory().setLeggings(playerEquipment.getLeggings().build());
        }

        if(playerEquipment.getBoots() != null){
            player.getInventory().setBoots(playerEquipment.getBoots().build());
        }


        ItemStack displayedWeapon = none.build();

        if(playerEquipment.getWeapon() != null){
            displayedWeapon = playerEquipment.getWeapon().build();
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
