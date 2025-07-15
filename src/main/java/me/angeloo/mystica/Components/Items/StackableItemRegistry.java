package me.angeloo.mystica.Components.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StackableItemRegistry {

    private static final Map<String, Function<Map<String, Object>, StackableItem>> registry = new HashMap<>();

    public static void register(String identifier, Function<Map<String, Object>, StackableItem> deserializer){
        registry.put(identifier.toUpperCase(), deserializer);
    }

    public static StackableItem deserialize(Map<String, Object> data){
        String identifier = (String) data.get("identifier");
        Function<Map<String, Object>, StackableItem> deserializer = registry.get(identifier.toUpperCase());

        if(deserializer == null){
            throw new IllegalArgumentException("Unknown stackable item identifier: " + identifier);
        }

        return deserializer.apply(data);
    }

}
