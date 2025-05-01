package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClassSetter {

    private final ProfileManager profileManager;
    private final EquipmentManager equipmentManager;
    private final ItemManager itemManager;
    private final DisplayWeapons displayWeapons;
    private final GearReader gearReader;

    public ClassSetter(Mystica main){
        profileManager = main.getProfileManager();
        equipmentManager = new EquipmentManager(main);
        itemManager = main.getItemManager();
        displayWeapons = main.getDisplayWeapons();
        gearReader = new GearReader(main);
    }

    public void setClass(Player player, String clazz){


        Profile playerProfile = profileManager.getAnyProfile(player);

        if(playerProfile.getPlayerClass().equalsIgnoreCase(clazz)){
            player.sendMessage("you are already this class");
            return;
        }


        playerProfile.setPlayerClass(clazz);

        playerProfile.setPlayerSubclass("none");
        profileManager.getAnyProfile(player).getStats().setLevelStats(profileManager.getAnyProfile(player).getStats().getLevel(), clazz, "none");
        player.sendMessage("You are now a(n) " + clazz);


        displayWeapons.displayArmor(player);
        gearReader.setGearStats(player);

    }


}
