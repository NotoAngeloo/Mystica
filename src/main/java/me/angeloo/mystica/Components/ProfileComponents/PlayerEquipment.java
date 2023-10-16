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

    public ItemStack getOffhand(){
        return Equipment[1];
    }

    public ItemStack getHelmet(){
        return Equipment[2];
    }

    public ItemStack getChestPlate(){
        return Equipment[3];
    }

    public ItemStack getLeggings(){
        return Equipment[4];
    }

    public ItemStack getBoots(){return Equipment[5];}

    public void setWeapon(ItemStack weapon){
        Equipment[0] = weapon;
    }

    public void setOffhand(ItemStack offhand){
        Equipment[1] = offhand;
    }

    public void setHelmet(ItemStack helmet){
        Equipment[2] = helmet;
    }

    public void setChestPlate(ItemStack chestPlate){
        Equipment[3] = chestPlate;
    }

    public void setLeggings(ItemStack leggings){
        Equipment[4] = leggings;
    }

    public void setBoots(ItemStack boots){
        Equipment[5] = boots;
    }

}
