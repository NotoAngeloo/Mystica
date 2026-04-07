package me.angeloo.mystica.Utility.Listeners;


import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class MMListeners implements Listener {

    private final ProfileManager profileManager;

    public MMListeners(Mystica main){
        profileManager = main.getProfileManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMythicSpawn(MythicMobSpawnEvent event){

        String name = event.getMob().getMobType();

        UUID id = event.getMob().getUniqueId();
        profileManager.addToNonPlayerNameMap(name, id);

        Entity entity = event.getEntity();

        if(entity instanceof LivingEntity){
            profileManager.getAnyProfile((LivingEntity) event.getEntity());

            //Bukkit.getLogger().info("Made a profile for " + name);

        }


        
    }


}
