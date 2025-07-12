package me.angeloo.mystica.Components.Items;

import org.bukkit.inventory.ItemStack;


import java.util.Map;

public abstract class MysticaItem {

    public abstract MysticaItemType type();

    public abstract String identifier();

    public abstract ItemStack build();

    public abstract Map<String, Object> serialize();

    public static MysticaItem deserialize(Map<String, Object> map) {
        MysticaItemType type = MysticaItemType.valueOf((String) map.get("type"));

        switch (type) {
            case EQUIPMENT:
                return MysticaEquipment.deserialize(map);
            case UNIDENTIFIED:
                return UnidentifiedItem.deserialize(map);
            default:
                throw new IllegalArgumentException("Unknown item type: " + type);
        }
    }



}
