package me.angeloo.mystica.Components.ProfileComponents;

import org.bukkit.inventory.ItemStack;

public class PlayerEquipment {

    private ItemStack[] Equipment;

    public PlayerEquipment(ItemStack[] equipment) {
        Equipment = equipment;
    }

    public ItemStack[] getEquipment() {
        return Equipment;
    }

    public void setEquipment(ItemStack[] equipment) {
        Equipment = equipment;
    }

    public ItemStack getWeapon(){
        return Equipment[0];
    }

    public ItemStack getHelmet(){
        return Equipment[1];
    }

    public ItemStack getChestPlate(){
        return Equipment[2];
    }

    public ItemStack getLeggings(){
        return Equipment[3];
    }

    public ItemStack getBoots(){return Equipment[4];}

    public void setWeapon(ItemStack weapon){
        Equipment[0] = weapon;
    }

    public void setHelmet(ItemStack helmet){
        Equipment[1] = helmet;
    }

    public void setChestPlate(ItemStack chestPlate){
        Equipment[2] = chestPlate;
    }

    public void setLeggings(ItemStack leggings){
        Equipment[3] = leggings;
    }

    public void setBoots(ItemStack boots){
        Equipment[4] = boots;
    }

}
