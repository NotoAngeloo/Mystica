package me.angeloo.mystica.Utility;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class EquipmentInformation {

    private static List<Material> allEquipmentTypes;

    public EquipmentInformation(){
        allEquipmentTypes = new ArrayList<>();
        allEquipmentTypes.add(Material.STICK);
        allEquipmentTypes.add(Material.BONE); //TEMPORARY until i get an offhand for elementalist
        allEquipmentTypes.add(Material.CHAINMAIL_HELMET);
        allEquipmentTypes.add(Material.CHAINMAIL_CHESTPLATE);
        allEquipmentTypes.add(Material.CHAINMAIL_LEGGINGS);
        allEquipmentTypes.add(Material.CHAINMAIL_BOOTS);

    }

    public List<Material> getAllEquipmentTypes(){
        return allEquipmentTypes;
    }

}
