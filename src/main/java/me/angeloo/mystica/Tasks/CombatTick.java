package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CombatTick {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final AbilityManager abilityManager;
    private final ChangeResourceHandler changeResourceHandler;

    public CombatTick(Mystica main, CombatManager combatmanager, AbilityManager abilityManager){
        this.main = main;
        profileManager = main.getProfileManager();
        this.combatManager = combatmanager;
        this.abilityManager = abilityManager;
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void startCombatTickFor(Player player){

        new BukkitRunnable(){

            @Override
            public void run(){

                if(combatManager.canLeaveCombat(player)){
                    endCombatTick();
                    this.cancel();
                    return;
                }


                Bukkit.getScheduler().runTask(main, () -> {
                    abilityManager.incrementResource(player);
                    Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent(player));
                    if(!profileManager.getCompanions(player).isEmpty()){
                        List<UUID> companions = profileManager.getCompanions(player);
                        for(UUID companion : companions){
                            LivingEntity livingEntity = (LivingEntity) Bukkit.getEntity(companion);

                            if(livingEntity == null) {
                                continue;
                            }

                            abilityManager.incrementResource(livingEntity);



                        }
                    }
                });


            }

            private void endCombatTick(){

                Bukkit.getScheduler().runTask(main, () ->{
                    changeResourceHandler.healPlayerToFull(player);
                    combatManager.forceCombatEnd(player);
                    abilityManager.resetResource(player);

                    if(!profileManager.getCompanions(player).isEmpty()){
                        List<UUID> companions = profileManager.getCompanions(player);
                        for(UUID companion : companions){
                            LivingEntity livingEntity = (LivingEntity) Bukkit.getEntity(companion);

                            if(livingEntity == null) {
                                continue;
                            }

                            double max = profileManager.getAnyProfile(livingEntity).getTotalHealth();

                            changeResourceHandler.addHealthToEntity(livingEntity, max, null);
                            abilityManager.resetResource(livingEntity);
                        }
                    }
                });

            }

        }.runTaskTimerAsynchronously(main, 0, 40);

    }

}
