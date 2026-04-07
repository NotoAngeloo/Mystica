package me.angeloo.mystica.Components.Items;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


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

    public static MysticaItem toMysticaItem(ItemStack item){

        ItemMeta meta = item.getItemMeta();

        if(meta == null){
            throw new IllegalArgumentException("ItemStack can't be converted");
        }

        Gson gson = new Gson();

        NamespacedKey equipmentKey = new NamespacedKey(Mystica.getPlugin(), "equipment_data");

        if(meta.getPersistentDataContainer().has(equipmentKey)){
            String json = meta.getPersistentDataContainer().get(equipmentKey, PersistentDataType.STRING);
            return gson.fromJson(json, MysticaEquipment.class);
        }


        NamespacedKey unidentifiedKey = new NamespacedKey(Mystica.getPlugin(), "unidentified_data");

        if(meta.getPersistentDataContainer().has(unidentifiedKey)){
            String json = meta.getPersistentDataContainer().get(unidentifiedKey, PersistentDataType.STRING);
            return gson.fromJson(json, UnidentifiedItem.class);
        }

        NamespacedKey stackableKey = new NamespacedKey(Mystica.getPlugin(), "stackable_data");

        if(meta.getPersistentDataContainer().has(stackableKey)){
            String json = meta.getPersistentDataContainer().get(stackableKey, PersistentDataType.STRING);

            Map<String, Object> map = gson.fromJson(
                    json,
                    new TypeToken<Map<String, Object>>() {}.getType()
            );

            assert map != null;

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();

                if (value instanceof Number) {
                    entry.setValue(((Number) value).intValue());
                }
            }

            //this is sketchy if i ever change the display name
            String colorlessName = meta.getDisplayName().replaceAll("§.", "");
            map.put("identifier",colorlessName);

            return StackableItemRegistry.deserialize(map);
        }


        throw new IllegalArgumentException("ItemStack can't be converted");
    }


}
