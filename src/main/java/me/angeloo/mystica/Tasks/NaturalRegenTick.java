package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.Components.ProfileComponents.Stats;
import me.angeloo.mystica.Components.ProfileComponents.StatsFromGear;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.ShieldAbilityManaDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NaturalRegenTick extends BukkitRunnable {

    private final ProfileManager profileManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;

    public NaturalRegenTick(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        changeResourceHandler = main.getChangeResourceHandler();
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
    }

    @Override
    public void run() {
        for(Player player: Bukkit.getOnlinePlayers()){

            Stats stats = profileManager.getAnyProfile(player).getStats();
            StatsFromGear gearStats = profileManager.getAnyProfile(player).getGearStats();

            boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

            long currentTime = System.currentTimeMillis()/1000;
            long lastManaed = changeResourceHandler.getLastManaed(player.getUniqueId());

            if(currentTime - lastManaed >= 20 || profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("shadow knight")){
                int maxMana = stats.getMana() + gearStats.getMana();
                double currentMana = profileManager.getAnyProfile(player).getCurrentMana();

                double manaRegenRate = maxMana * .01;

                if(profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("shadow knight")){
                    manaRegenRate = maxMana * .05;
                }

                if(!combatStatus){
                    manaRegenRate = maxMana * .3;
                }

                if(currentMana > maxMana){
                    profileManager.getAnyProfile(player).setCurrentMana(maxMana);
                }

                if(currentMana < maxMana){
                    changeResourceHandler.addManaToPlayer(player, manaRegenRate);
                }
            }


            player.setFoodLevel(20);


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
