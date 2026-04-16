package me.angeloo.mystica.Components.CombatSystem.Abilities;

import java.util.HashMap;
import java.util.Map;

public class PlayerState {

    private final Map<String, Object> flags = new HashMap<>();

    public void set(String key, Object value){
        flags.put(key, value);
    }

    public <T> T get(String key, Class<T> type){
        return type.cast(flags.get(key));
    }

    public int getInt(String key, int defaultValue){
        Object value = flags.get(key);
        return (value instanceof Integer i) ? i : defaultValue;
    }

    public boolean has(String key){
        return flags.containsKey(key);
    }

    public void remove(String key){
        flags.remove(key);
    }

}
