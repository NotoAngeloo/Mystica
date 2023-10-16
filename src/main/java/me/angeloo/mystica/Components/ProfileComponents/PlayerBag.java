package me.angeloo.mystica.Components.ProfileComponents;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerBag {

    private ArrayList<ItemStack> Items;
    private int NumUnlocks;

    public PlayerBag(ArrayList<ItemStack> items, int numUnlocks){
        Items = items;
        NumUnlocks = numUnlocks;
    }

    public ArrayList<ItemStack> getItems(){return Items;}

    public void setItems(ArrayList<ItemStack> items){
        Items = items;
    }

    public int getNumUnlocks() {
        return NumUnlocks;
    }

    public void setNumUnlocks(int numUnlocks) {
        NumUnlocks = numUnlocks;
    }

}
