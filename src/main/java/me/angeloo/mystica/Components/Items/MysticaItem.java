package me.angeloo.mystica.Components.Items;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public abstract class MysticaItem {

    protected String name;
    protected List<String> lore;
    protected ItemStack baseItem;


    public abstract ItemStack build();

    public abstract Map<String, Object> serialize();

}
