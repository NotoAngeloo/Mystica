package me.angeloo.mystica.Managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryIndexingManager {

    private final Map<UUID, Integer> bagIndex = new HashMap<>();
    private final Map<UUID, Integer> classIndex = new HashMap<>();

    public InventoryIndexingManager(){
    }

    public int getBagIndex(Player player){
        return bagIndex.get(player.getUniqueId());
    }
    public void innitBagIndex(Player player){
        bagIndex.put(player.getUniqueId(), 0);
    }
    public void setBagIndex(Player player, Integer index){
        bagIndex.put(player.getUniqueId(), index);
    }

    public int getClassIndex(Player player){
        return classIndex.get(player.getUniqueId());
    }
    public void innitClassIndex(Player player){
        classIndex.put(player.getUniqueId(), 0);
    }
    public void setClassIndex(Player player, Integer index){
        classIndex.put(player.getUniqueId(), index);
    }


}
