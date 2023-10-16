package me.angeloo.mystica.Components.ClassEquipment;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TrialClassEquipment {

    private final ElementalistEquipment elementalistEquipment;
    private final RangerEquipment rangerEquipment;

    public TrialClassEquipment(){
        elementalistEquipment = new ElementalistEquipment();
        rangerEquipment = new RangerEquipment();
    }

    public ItemStack getTrialWeapon(String trial){

        switch (trial.toLowerCase()){
            case "elementalist":{
                return elementalistEquipment.getBaseWeapon();
            }
            case "ranger":{
                return rangerEquipment.getBaseWeapon();
            }
        }

        return new ItemStack(Material.AIR);
    }

}
