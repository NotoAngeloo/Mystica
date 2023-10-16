package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryIndexingManager {

    private final Map<UUID, Integer> bagIndex;

    public InventoryIndexingManager(Mystica main){
        bagIndex = new HashMap<>();
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


}
