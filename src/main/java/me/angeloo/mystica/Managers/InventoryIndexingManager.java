package me.angeloo.mystica.Managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryIndexingManager {

    private final Map<UUID, Integer> bagIndex = new HashMap<>();
    private final Map<UUID, Integer> classIndex = new HashMap<>();
    private final Map<UUID, Integer> questIndex = new HashMap<>();

    public InventoryIndexingManager(){
    }

    public int getBagIndex(Player player){

        if(!bagIndex.containsKey(player.getUniqueId())){
            bagIndex.put(player.getUniqueId(), 0);
        }

        return bagIndex.get(player.getUniqueId());
    }

    public void setBagIndex(Player player, Integer index){
        bagIndex.put(player.getUniqueId(), index);
    }

    public int getQuestIndex(Player player){

        if(!questIndex.containsKey(player.getUniqueId())){
            questIndex.put(player.getUniqueId(), 0);
        }

        return questIndex.get(player.getUniqueId());
    }

    public void setQuestIndex(Player player, Integer index){questIndex.put(player.getUniqueId(), index);}

    public int getClassIndex(Player player){

        if(!classIndex.containsKey(player.getUniqueId())){
            classIndex.put(player.getUniqueId(), 0);
        }

        return classIndex.get(player.getUniqueId());
    }


    public void setClassIndex(Player player, Integer index){
        classIndex.put(player.getUniqueId(), index);
    }



}
