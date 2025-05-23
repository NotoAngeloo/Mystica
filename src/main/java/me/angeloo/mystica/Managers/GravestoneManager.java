package me.angeloo.mystica.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GravestoneManager {

    private final Map<Entity, UUID> gravestones = new HashMap<>();
    private final Map<UUID, Entity> playersGravestone = new HashMap<>();

    public GravestoneManager(){

    }

    public void placeGravestone(Entity gravestone, LivingEntity player){
        gravestones.put(gravestone, player.getUniqueId());
        playersGravestone.put(player.getUniqueId(), gravestone);

    }

    public boolean isGravestone(Entity entity){
        return gravestones.containsKey(entity);
    }

    public LivingEntity getPlayer(Entity entity){

        return (LivingEntity) Bukkit.getEntity(gravestones.get(entity));
    }

    public Entity getGravestone(LivingEntity player){

        if(playersGravestone.containsKey(player.getUniqueId())){
            return playersGravestone.get(player.getUniqueId());
        }

        return null;
    }

    public void removeGravestone(LivingEntity player){

        if(playersGravestone.containsKey(player.getUniqueId())){
            Entity gravestone = getGravestone(player);
            gravestone.remove();
            gravestones.remove(gravestone);
            playersGravestone.remove(player.getUniqueId());
        }


    }
}
