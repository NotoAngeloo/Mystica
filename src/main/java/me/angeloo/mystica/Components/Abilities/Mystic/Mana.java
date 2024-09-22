package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mana {

    private final ProfileManager profileManager;

    private final Map<UUID, Long> lastManaed = new HashMap<>();

    private final Map<UUID, Integer> manaAmount = new HashMap<>();

    private final int maxMana = 500;

    public Mana(Mystica main){
        profileManager = main.getProfileManager();
    }

    //here change it

    //max mana 500

    public void subTractManaFromEntity(LivingEntity caster, int cost){

        int currentMana = getCurrentMana(caster);
        int newCurrentMana = currentMana - cost;
        if(newCurrentMana < 0){
            newCurrentMana = 0;
        }
        manaAmount.put(caster.getUniqueId(), newCurrentMana);
        lastManaed.put(caster.getUniqueId(), (System.currentTimeMillis()/1000));
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(caster, true));
    }

    public void addManaToEntity(LivingEntity entity, int amount){
        int currentMana = getCurrentMana(entity);
        int newCurrentMana = currentMana + amount;

        if(newCurrentMana > maxMana){
            newCurrentMana = maxMana;
        }
        manaAmount.put(entity.getUniqueId(), newCurrentMana);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
    }

    public int getCurrentMana(LivingEntity livingEntity){

        if(!manaAmount.containsKey(livingEntity.getUniqueId())){
            manaAmount.put(livingEntity.getUniqueId(), maxMana);
        }

        return manaAmount.get(livingEntity.getUniqueId());
    }

    public void regenManaNaturally(LivingEntity entity) {

        long currentTime = System.currentTimeMillis() / 1000;

        if (currentTime - getLastManaed(entity.getUniqueId()) >= 20) {
            int currentMana = getCurrentMana(entity);

            int manaRegenRate = 50;

            if (!profileManager.getAnyProfile(entity).getIfInCombat()) {
                manaRegenRate = 150;
            }

            if (currentMana > maxMana) {
                manaAmount.put(entity.getUniqueId(),maxMana);
            }

            if (currentMana < maxMana) {
                addManaToEntity(entity, manaRegenRate);

            }
        }
    }

    private Long getLastManaed(UUID uuid){

        if(!lastManaed.containsKey(uuid)){
            lastManaed.put(uuid, (System.currentTimeMillis() / 1000) - 20);
        }

        return lastManaed.get(uuid);
    }


}
