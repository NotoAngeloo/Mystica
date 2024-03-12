package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SanctityShield {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SanctityShield(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(Player player){


        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        double cost = 20;

        if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 12);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        double maxHealth = profileManager.getAnyProfile(player).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(player);
        double level = profileManager.getAnyProfile(player).getStats().getLevel();
        double shield = (level / maxHealth) * 100;
        double healAmount = maxHealth * (level/100);

        buffAndDebuffManager.getGenericShield().applyOrAddShield(player, shield);

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(player)==0){
                    this.cancel();
                    return;
                }

                if(!player.isOnline() || profileManager.getAnyProfile(player).getIfDead()){
                    this.cancel();
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, shield);
                    return;
                }

                changeResourceHandler.addManaToPlayer(player, healAmount);

                if(count>=5){
                    this.cancel();
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, shield);
                }
                count++;
            }
        }.runTaskTimer(main, 0, 20);

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }


}
