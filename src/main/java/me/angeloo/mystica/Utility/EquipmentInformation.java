package me.angeloo.mystica.Utility;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class EquipmentInformation {

    private static List<Material> allEquipmentTypes;

    public EquipmentInformation(){
        allEquipmentTypes = new ArrayList<>();
        allEquipmentTypes.add(Material.STICK);
        allEquipmentTypes.add(Material.FLINT);
        allEquipmentTypes.add(Material.BLAZE_ROD);
        allEquipmentTypes.add(Material.IRON_SWORD);
        allEquipmentTypes.add(Material.FEATHER);
        allEquipmentTypes.add(Material.DIAMOND_SWORD);
        allEquipmentTypes.add(Material.BRICK);
        allEquipmentTypes.add(Material.CHAIN);
        allEquipmentTypes.add(Material.CHAINMAIL_CHESTPLATE);
        allEquipmentTypes.add(Material.CHAINMAIL_LEGGINGS);
        allEquipmentTypes.add(Material.CHAINMAIL_BOOTS);

    }

    public List<Material> getAllEquipmentTypes(){
        return allEquipmentTypes;
    }

}
