package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Focus {

    private final ProfileManager profileManager;

    private final Map<UUID, Integer> manaAmount = new HashMap<>();

    private final int maxMana = 10;

    public Focus(Mystica main){
        profileManager = main.getProfileManager();
    }


    public void loseFocus(LivingEntity caster){
        manaAmount.put(caster.getUniqueId(), 0);

        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Resource, false));
        }
    }

    private void addFocusToEntity(LivingEntity entity, int amount){

        if(!profileManager.getAnyProfile(entity).getIfInCombat() && !profileManager.getAnyProfile(entity).getIfDead()){
           return;
        }

        int currentMana = getFocus(entity);
        int newCurrentMana = currentMana + amount;

        if(newCurrentMana > maxMana){
            newCurrentMana = maxMana;
        }
        manaAmount.put(entity.getUniqueId(), newCurrentMana);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Resource, false));
        }
    }

    public int getFocus(LivingEntity livingEntity){

        if(!manaAmount.containsKey(livingEntity.getUniqueId())){
            manaAmount.put(livingEntity.getUniqueId(), 0);
        }

        return manaAmount.get(livingEntity.getUniqueId());
    }

    public void regenFocusNaturally(LivingEntity entity) {

        int currentMana = getFocus(entity);

        int manaRegenRate = 1;

        if (currentMana > maxMana) {
            manaAmount.put(entity.getUniqueId(),maxMana);
        }

        if (currentMana < maxMana) {
            addFocusToEntity(entity, manaRegenRate);

        }
    }

    public double calculateFocusMultipliedDamage(LivingEntity entity, double damage){

        int percent = (int) Math.floor(((double) getFocus(entity) / 10) * 100);

        return damage *  (1 +((double)percent / 100));
    }

}
