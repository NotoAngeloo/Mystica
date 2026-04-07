package me.angeloo.mystica.Components.ProfileComponents;

import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.MysticaItem;
import org.bukkit.inventory.ItemStack;

public class PlayerEquipment {

    private final MysticaEquipment[] equipment;

    public PlayerEquipment(MysticaEquipment[] equipment) {
        this.equipment = equipment;
    }

    public MysticaEquipment[] getEquipment() {
        return equipment;
    }

    /*public void setEquipment(MysticaEquipment[] equipment) {
        this.equipment = equipment;
    }*/

    public MysticaEquipment getWeapon(){
        return equipment[0];
    }

    public MysticaEquipment getHelmet(){
        return equipment[1];
    }

    public MysticaEquipment getChestPlate(){
        return equipment[2];
    }

    public MysticaEquipment getLeggings(){
        return equipment[3];
    }

    public MysticaEquipment getBoots(){return equipment[4];}

    public void setWeapon(MysticaEquipment weapon){
        equipment[0] = weapon;
    }

    public void setHelmet(MysticaEquipment helmet){
        equipment[1] = helmet;
    }

    public void setChestPlate(MysticaEquipment chestPlate){
        equipment[2] = chestPlate;
    }

    public void setLeggings(MysticaEquipment leggings){
        equipment[3] = leggings;
    }

    public void setBoots(MysticaEquipment boots){
        equipment[4] = boots;
    }


}
