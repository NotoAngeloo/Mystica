package me.angeloo.mystica.Components.Abilities.Warrior;

import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Rage {

    private final ProfileManager profileManager;

    private final Map<UUID, Integer> manaAmount = new HashMap<>();

    private final int maxMana = 500;

    public Rage(Mystica main){
        profileManager = main.getProfileManager();
    }


    public void subTractRageFromEntity(LivingEntity caster, int cost){

        int currentMana = getCurrentRage(caster);
        int newCurrentMana = currentMana - cost;
        manaAmount.put(caster.getUniqueId(), newCurrentMana);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(caster, true));
    }

    public void addRageToEntity(LivingEntity entity, int amount){
        int currentMana = getCurrentRage(entity);
        int newCurrentMana = currentMana + amount;

        if(newCurrentMana > maxMana){
            newCurrentMana = maxMana;
        }
        manaAmount.put(entity.getUniqueId(), newCurrentMana);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
    }

    public int getCurrentRage(LivingEntity livingEntity){

        if(!manaAmount.containsKey(livingEntity.getUniqueId())){
            manaAmount.put(livingEntity.getUniqueId(), 0);
        }

        return manaAmount.get(livingEntity.getUniqueId());
    }

    //warriors lose rage naturally. gain it by dealing or taking damage. big skill has no cd, but costs rage

    public void loseRageNaturally(LivingEntity entity) {


        int currentMana = getCurrentRage(entity);

        int manaRegenRate = 50;

        if (!profileManager.getAnyProfile(entity).getIfInCombat()) {
            manaRegenRate = 300;
        }

        if (currentMana > maxMana) {
            manaAmount.put(entity.getUniqueId(),maxMana);
        }

        if (currentMana < maxMana) {
            subTractRageFromEntity(entity, manaRegenRate);

        }
    }


}
