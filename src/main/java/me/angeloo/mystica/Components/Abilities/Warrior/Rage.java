package me.angeloo.mystica.Components.Abilities.Warrior;

import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Rage {

    private final ProfileManager profileManager;

    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> manaAmount = new HashMap<>();

    private final int maxMana = 500;

    public Rage(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }


    public void subTractRageFromEntity(LivingEntity caster, int cost){

        int currentMana = getCurrentRage(caster);
        int newCurrentMana = currentMana - cost;
        if(newCurrentMana < 0){
            newCurrentMana = 0;
        }
        manaAmount.put(caster.getUniqueId(), newCurrentMana);
        cooldownDisplayer.displayCooldown(caster, 4);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(caster, true));
    }

    public void addRageToEntity(LivingEntity entity, int amount){
        int currentMana = getCurrentRage(entity);
        int newCurrentMana = currentMana + amount;

        if(newCurrentMana > maxMana){
            newCurrentMana = maxMana;
        }
        manaAmount.put(entity.getUniqueId(), newCurrentMana);
        cooldownDisplayer.displayCooldown(entity, 4);
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

        int manaRegenRate = 5;

        if(entity instanceof Player){
            if (!profileManager.getAnyProfile(entity).getIfInCombat()) {
                manaRegenRate = 300;
            }
        }
        else{
            if(!profileManager.getIfCompanionInCombat(entity.getUniqueId())){
                manaRegenRate = 300;
            }
        }



        if(currentMana > 0){
            subTractRageFromEntity(entity, manaRegenRate);
        }

    }


}
