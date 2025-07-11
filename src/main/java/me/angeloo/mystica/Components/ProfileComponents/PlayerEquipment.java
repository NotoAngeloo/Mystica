package me.angeloo.mystica.Components.ProfileComponents;

import me.angeloo.mystica.Components.Items.MysticaItem;
import org.bukkit.inventory.ItemStack;

public class PlayerEquipment {

    private MysticaItem[] equipment;

    public PlayerEquipment(MysticaItem[] equipment) {
        this.equipment = equipment;
    }

    public MysticaItem[] getEquipment() {
        return equipment;
    }

    public void setEquipment(MysticaItem[] equipment) {
        this.equipment = equipment;
    }

    public MysticaItem getWeapon(){
        return equipment[0];
    }

    public MysticaItem getHelmet(){
        return equipment[1];
    }

    public MysticaItem getChestPlate(){
        return equipment[2];
    }

    public MysticaItem getLeggings(){
        return equipment[3];
    }

    public MysticaItem getBoots(){return equipment[4];}

    public void setWeapon(MysticaItem weapon){
        equipment[0] = weapon;
    }

    public void setHelmet(MysticaItem helmet){
        equipment[1] = helmet;
    }

    public void setChestPlate(MysticaItem chestPlate){
        equipment[2] = chestPlate;
    }

    public void setLeggings(MysticaItem leggings){
        equipment[3] = leggings;
    }

    public void setBoots(MysticaItem boots){
        equipment[4] = boots;
    }

}
