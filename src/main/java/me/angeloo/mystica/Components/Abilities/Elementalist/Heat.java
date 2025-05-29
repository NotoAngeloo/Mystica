package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Heat {

    private final ProfileManager profileManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final BuffAndDebuffManager buffAndDebuffManager;

    private final Map<UUID, Integer> manaAmount = new HashMap<>();

    private final int maxMana = 100;

    public Heat(Mystica main){
        profileManager = main.getProfileManager();
        changeResourceHandler = main.getChangeResourceHandler();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
    }

    public void reduceHeat(LivingEntity caster, int cost){

        int currentMana = getHeat(caster);
        int newCurrentMana = currentMana - cost;
        if(newCurrentMana < 0){
            newCurrentMana = 0;
        }
        manaAmount.put(caster.getUniqueId(), newCurrentMana);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(caster, true));

        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "resource"));
        }
    }

    public void addHeat(LivingEntity entity, int amount){
        int currentMana = getHeat(entity);
        int newCurrentMana = currentMana + amount;

        if(newCurrentMana > maxMana){

            //damage entity 10%
            double maxHp = profileManager.getAnyProfile(entity).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(entity);
            changeResourceHandler.subtractHealthFromEntity(entity, maxHp * .1, null);
            entity.getWorld().spawnParticle(Particle.FLAME, entity.getLocation(), 50, .5, 1, .5, 0);

            newCurrentMana = maxMana;
        }
        manaAmount.put(entity.getUniqueId(), newCurrentMana);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));

        if(entity instanceof Player){
            Player player = (Player) entity;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "resource"));
        }
    }

    public int getHeat(LivingEntity livingEntity){

        if(!manaAmount.containsKey(livingEntity.getUniqueId())){
            manaAmount.put(livingEntity.getUniqueId(), 0);
        }

        return manaAmount.get(livingEntity.getUniqueId());
    }

    public void loseHeatNaturally(LivingEntity entity) {

        int currentMana = getHeat(entity);

        int manaRegenRate = 1;

        if(entity instanceof Player){
            if (!profileManager.getAnyProfile(entity).getIfInCombat()) {
                manaRegenRate = 20;
            }
        }
        else{
            if(!profileManager.getIfCompanionInCombat(entity.getUniqueId())){
                manaRegenRate = 20;
            }
        }


        if(currentMana > 0){
            reduceHeat(entity, manaRegenRate);
        }

    }

}
