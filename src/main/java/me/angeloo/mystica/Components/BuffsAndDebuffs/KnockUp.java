package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KnockUp {

    private final Mystica main;
    private final ProfileManager profileManager;

    private final Map<UUID, Boolean> knockedUp = new HashMap<>();

    public KnockUp(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
    }

    public void applyKnockUp(LivingEntity entity){

        knockedUp.put(entity.getUniqueId(), true);

        new BukkitRunnable(){
            @Override
            public void run(){

                if(entity instanceof Player){

                    if(profileManager.getAnyProfile(entity).getIfDead()){
                        this.cancel();
                        removeKnockUp(entity);
                        return;
                    }

                    if(((Player)entity).isOnline()){
                        this.cancel();
                        removeKnockUp(entity);
                        return;
                    }
                }

                if(entity.isDead()){
                    this.cancel();
                    removeKnockUp(entity);
                    return;
                }

                Block block = entity.getLocation().subtract(0,1,0).getBlock();

                if(block.getType() == Material.AIR){
                    this.cancel();
                    removeKnockUp(entity);
                }

            }

        }.runTaskTimer(main, 5, 1);

    }

    public void removeKnockUp(LivingEntity entity){
        knockedUp.remove(entity.getUniqueId());
    }

    public boolean getIfKnockUp(LivingEntity entity){
        return knockedUp.getOrDefault(entity.getUniqueId(), false);
    }


}
