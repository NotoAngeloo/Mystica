package me.angeloo.mystica.Components.Items;

import org.bukkit.inventory.ItemStack;


import java.util.Map;

public abstract class MysticaItem {


    public abstract ItemStack build();

    public abstract Map<String, Object> serialize();

}
