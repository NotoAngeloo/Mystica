package me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Yield {

    private final float XpYield;

    private final List<ItemStack> ItemYield;

    public Yield(float xpYield, List<ItemStack> itemYield){
        XpYield = xpYield;
        ItemYield = itemYield;
    }

    public float getXpYield(){
        return XpYield;
    }

    public List<ItemStack> getItemYield(){
        return ItemYield;
    }

}
