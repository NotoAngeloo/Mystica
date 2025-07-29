package me.angeloo.mystica.Components.Abilities.Paladin;

import org.bukkit.entity.LivingEntity;

import java.util.*;

public class Purity {

    private final Map<UUID, List<Integer>> savedAbilities = new HashMap<>();
    private final Map<UUID, Boolean> active = new HashMap<>();

    public Purity(){

    }

    public void add(LivingEntity entity, int skill){

        List<Integer> abilities = getSaved(entity);

        if(abilities.contains(skill)){
            reset(entity);
        }

        abilities.add(skill);

        if(abilities.size()==3){
            active.put(entity.getUniqueId(), true);
            reset(entity);
            return;
        }

        savedAbilities.put(entity.getUniqueId(), abilities);
    }

    private List<Integer> getSaved(LivingEntity entity){

        if(!savedAbilities.containsKey(entity.getUniqueId())){
            reset(entity);
        }

        return savedAbilities.get(entity.getUniqueId());
    }

    public boolean active(LivingEntity entity){
        return active.getOrDefault(entity.getUniqueId(), false);
    }

    public void reset(LivingEntity entity){
        savedAbilities.put(entity.getUniqueId(), new ArrayList<>());
    }

    public int get(LivingEntity entity){

        int size = 0;

        if(savedAbilities.containsKey(entity.getUniqueId())){
            size = savedAbilities.get(entity.getUniqueId()).size();
        }


        return size;

    }


}
