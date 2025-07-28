package me.angeloo.mystica.Components.Items;

import org.bukkit.inventory.ItemStack;


import java.util.Map;

public abstract class MysticaItem {

    public abstract MysticaItemFormat format();

    public abstract String identifier();

    public abstract ItemStack build();

    public abstract boolean questItem();

    public abstract Map<String, Object> serialize();

    public static MysticaItem deserialize(Map<String, Object> map) {

        MysticaItemFormat format = MysticaItemFormat.valueOf((String) map.get("format"));


        return switch (format) {
            case EQUIPMENT -> MysticaEquipment.deserialize(map);
            case UNIDENTIFIED -> UnidentifiedItem.deserialize(map);
            case STACKABLE -> StackableItemRegistry.deserialize(map);
            default -> throw new IllegalArgumentException("Unknown item format: " + format);
        };
    }



}
