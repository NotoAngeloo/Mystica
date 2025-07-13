package me.angeloo.mystica.Components.Items;

import org.bukkit.inventory.ItemStack;


import java.util.Map;

public abstract class MysticaItem {

    public abstract MysticaItemFormat format();

    public abstract String identifier();

    public abstract ItemStack build();

    public abstract Map<String, Object> serialize();

    public static MysticaItem deserialize(Map<String, Object> map) {

        if(!map.containsKey("format")){

            String itemName = (String) map.get("name");

            switch (itemName){
                case "Soul Stone":{
                    return new SoulStone();
                }
                case "Bag":{
                    return new BagItem();
                }
            }

        }

        MysticaItemFormat format = MysticaItemFormat.valueOf((String) map.get("format"));


        switch (format) {
            case EQUIPMENT:
                return MysticaEquipment.deserialize(map);
            case UNIDENTIFIED:
                return UnidentifiedItem.deserialize(map);
            default:
                throw new IllegalArgumentException("Unknown item format: " + format);
        }
    }



}
