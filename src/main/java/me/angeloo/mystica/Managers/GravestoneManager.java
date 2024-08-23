package me.angeloo.mystica.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GravestoneManager {

    private final Map<Entity, LivingEntity> gravestones = new HashMap<>();
    private final Map<LivingEntity, Entity> playersGravestone = new HashMap<>();

    public GravestoneManager(){

    }

    public void placeGravestone(Entity gravestone, LivingEntity player){
        gravestones.put(gravestone, player);
        playersGravestone.put(player, gravestone);

    }

    public boolean isGravestone(Entity entity){
        return gravestones.containsKey(entity);
    }

    public LivingEntity getPlayer(Entity entity){
        return gravestones.get(entity);
    }

    private Entity getGravestone(LivingEntity player){
        return playersGravestone.get(player);
    }

    public void removeGravestone(LivingEntity player){

        if(playersGravestone.containsKey(player)){
            Entity gravestone = getGravestone(player);
            gravestone.remove();
            gravestones.remove(gravestone);
            playersGravestone.remove(player);
        }


    }
}
