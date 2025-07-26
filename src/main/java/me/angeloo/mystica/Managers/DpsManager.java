package me.angeloo.mystica.Managers;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class DpsManager {

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;


    private final Map<UUID, Long> dpsStarted = new HashMap<>();
    private final Map<UUID, Double> totalDamage = new HashMap<>();


    public DpsManager(Mystica main){
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
    }

    public double getRawDps(LivingEntity entity){

        if(!totalDamage.containsKey(entity.getUniqueId())){
            return 0;
        }

        return getSaved(entity) / getTime(entity);

    }

    public int getRoundedDps(LivingEntity entity){

        if(!totalDamage.containsKey(entity.getUniqueId())){
            return 0;
        }

        return (int) (Math.round(getRawDps(entity)));
    }



    public void addToDamageDealt(LivingEntity entity, double damage){

        if(profileManager.getAnyProfile(entity).getIfDead()){
            return;
        }

        getStart(entity);

        double saved = getSaved(entity);
        saved = saved + damage;
        totalDamage.put(entity.getUniqueId(), saved);

        updateDpsMeter(entity);
    }


    private double getSaved(LivingEntity entity){
        return totalDamage.getOrDefault(entity.getUniqueId(), 0.0);
    }

    private long getTime(LivingEntity entity){

        long now = System.currentTimeMillis();
        long start = getStart(entity);

        //Bukkit.getLogger().info("time in dps: " + ((now - start) / 1000));

        return ((now - start) / 1000) + 1;
    }

    private long getStart(LivingEntity entity){
        if(!dpsStarted.containsKey(entity.getUniqueId())){
            dpsStarted.put(entity.getUniqueId(), System.currentTimeMillis());
        }

        return dpsStarted.get(entity.getUniqueId());
    }

    public void removeDps(LivingEntity entity){
        dpsStarted.remove(entity.getUniqueId());
        totalDamage.remove(entity.getUniqueId());


    }



    private void updateDpsMeter(LivingEntity entity){

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(entity));

        for(LivingEntity member : mParty){
            if(member instanceof Player){
                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent((Player)member, BarType.Dps, false));
            }
        }



    }

}
