package me.angeloo.mystica.Components.Abilities.Paladin;

import org.bukkit.entity.LivingEntity;

import java.util.*;

public class Purity {

    //make this a list instead
    private final Map<UUID, LinkedList<Integer>> savedAbilities = new HashMap<>();

    public Purity(){

    }

    public void skillListAdd(LivingEntity entity, int skill){

        LinkedList<Integer> abilities = getSaved(entity);

        abilities.add(skill);

        if(abilities.size()>5){
            abilities.removeFirst();
        }

        savedAbilities.put(entity.getUniqueId(), abilities);
    }

    private LinkedList<Integer> getSaved(LivingEntity entity){

        if(!savedAbilities.containsKey(entity.getUniqueId())){
            LinkedList<Integer> defaultList = new LinkedList<>(Arrays.asList(0, 0, 0, 0, 0));
            savedAbilities.put(entity.getUniqueId(), defaultList);
        }

        return savedAbilities.get(entity.getUniqueId());
    }

    public double calculatePurityPercentDamage(LivingEntity entity, int skill, double damage){

        LinkedList<Integer> abilities = getSaved(entity);

        double purity = 120;

        int count = 0;
        for(int num : abilities){
            if(num == skill){
                count++;
            }
        }

        purity -= count*20;

        int percent = (int) Math.floor((purity/120) * 100);

        return damage * (1+ percent);
    }


}
