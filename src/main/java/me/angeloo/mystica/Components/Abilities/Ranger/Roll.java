package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Roll {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Roll(Mystica main, AbilityManager manager){
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


        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 13);
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

        Location start = player.getLocation();
        Vector direction = start.getDirection().normalize().multiply(-1);

        double forwardPower = 3;
        double jumpPower = .2;
        Vector dashVector = direction.multiply(forwardPower).setY(jumpPower);
        player.setVelocity(dashVector);

        //also give a shield and increase move speed
        buffAndDebuffManager.getSpeedUp().applySpeedUp(player, .3f);

        double fourthHealth = (double) profileManager.getAnyProfile(player).getTotalHealth() / 4;
        buffAndDebuffManager.getGenericShield().applyOrAddShield(player, fourthHealth);

        new BukkitRunnable(){
            @Override
            public void run(){
                buffAndDebuffManager.getSpeedUp().removeSpeedUp(player);
                buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, fourthHealth);
            }
        }.runTaskLater(main, 100);

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
