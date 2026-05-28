package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.Items.Equipment.MysticaEquipment;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DisplayWeapons {

    /*private final MysticaEquipment none;
    private final ProfileManager profileManager;

    public DisplayWeapons(Mystica main){
        profileManager = main.getProfileManager();
        none = new MysticaEquipment(EquipmentSlot.WEAPON, PlayerClass.NONE, 1);
    }

    public void displayArmor(Player player){


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
    }*/


}
