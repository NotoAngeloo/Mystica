package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.Components.ProfileComponents.Stats;
import me.angeloo.mystica.Components.ProfileComponents.StatsFromGear;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.ShieldAbilityManaDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class NaturalRegenTick extends BukkitRunnable {

    private final ProfileManager profileManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
    private final CombatManager combatManager;

    public NaturalRegenTick(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        combatManager = main.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        abilityManager = main.getAbilityManager();
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
    }

    @Override
    public void run() {
        for(Player player: Bukkit.getOnlinePlayers()){

            if(profileManager.getAnyProfile(player).getIfInCombat()){

                if(combatManager.canLeaveCombat(player)){
                    combatManager.forceCombatEnd(player);
                }
            }

            boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
            long currentTime = System.currentTimeMillis()/1000;

            //profileManager.updateCompanions(player);

            if(!profileManager.getCompanions(player).isEmpty()){

                List<UUID> companions = profileManager.getCompanions(player);
                for(UUID companion : companions){

                    LivingEntity livingEntity = (LivingEntity) Bukkit.getEntity(companion);

                    if(livingEntity == null){
                        continue;
                    }

                    Stats stats = profileManager.getAnyProfile(livingEntity).getStats();

                    abilityManager.incrementResource(livingEntity);

                    long lastDamaged = changeResourceHandler.getLastDamaged(companion);

                    //Bukkit.getLogger().info(String.valueOf(currentTime - lastDamaged));

                    if(currentTime - lastDamaged >= 20){
                        int maxHealth = stats.getHealth();
                        double currentHealth = profileManager.getAnyProfile(livingEntity).getCurrentHealth();

                        double healthRegenRate = maxHealth * .01;

                        if(!combatStatus){
                            healthRegenRate = maxHealth;
                        }

                        if(currentHealth > maxHealth){
                            profileManager.getAnyProfile(livingEntity).setCurrentHealth(maxHealth);
                            currentHealth = maxHealth;
                        }

                        if(currentHealth < maxHealth){
                            changeResourceHandler.addHealthToEntity(livingEntity, healthRegenRate, null);
                            //Bukkit.getLogger().info(String.valueOf(healthRegenRate));
                        }

                    }
                }
            }


            Stats stats = profileManager.getAnyProfile(player).getStats();
            StatsFromGear gearStats = profileManager.getAnyProfile(player).getGearStats();

            player.setFoodLevel(20);

            abilityManager.incrementResource(player);

            long lastDamaged = changeResourceHandler.getLastDamaged(player.getUniqueId());

            //Bukkit.getLogger().info(String.valueOf(currentTime - lastDamaged));

            if(currentTime - lastDamaged >= 20){
                AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                assert maxHealthAttribute != null;

                if(maxHealthAttribute.getBaseValue() != 20){
                    maxHealthAttribute.setBaseValue(20);
                    player.setHealth(20);
                }


                maxHealthAttribute.setBaseValue(20);

                int maxHealth = stats.getHealth() + gearStats.getHealth();

                double currentHealth = profileManager.getAnyProfile(player).getCurrentHealth();

                double healthRegenRate = maxHealth * .01;

                if(!combatStatus){
                    healthRegenRate = maxHealth * .3;
                }

                if(currentHealth > maxHealth){
                    profileManager.getAnyProfile(player).setCurrentHealth(maxHealth);
                    player.setHealth(20);
                    currentHealth = maxHealth;
                }

                if(currentHealth < maxHealth){
                    changeResourceHandler.addHealthToEntity(player, healthRegenRate, null);
                    //Bukkit.getLogger().info(String.valueOf(healthRegenRate));
                }

            }

            if(!profileManager.getAnyProfile(player).getIfInCombat() || profileManager.getAnyProfile(player).getIfDead()){
                continue;
            }

            shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo(player);

        }

    }
}
