package me.angeloo.mystica.Managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomInventoryManager {

    private final Map<UUID, Integer> bagIndex = new HashMap<>();
    private final Map<UUID, Integer> classIndex = new HashMap<>();
    private final Map<UUID, Integer> questIndex = new HashMap<>();

    public CustomInventoryManager(){
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



    public int getClassIndex(Player player){

        if(!classIndex.containsKey(player.getUniqueId())){
            classIndex.put(player.getUniqueId(), 0);
        }

        return classIndex.get(player.getUniqueId());
    }


    public void setClassIndex(Player player, Integer index){
        classIndex.put(player.getUniqueId(), index);
    }

    public String addBagPng(String originalTitle){

        StringBuilder newTitle = new StringBuilder();

        newTitle.append(originalTitle);


        //negative space before this

        //-256
        newTitle.append("\uF80D");

        //+78
        newTitle.append("\uF82B\uF828\uF826");

        newTitle.append("\uE05C");

        return String.valueOf(newTitle);
    }



}
