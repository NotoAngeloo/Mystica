package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Enlightenment {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Consolation consolation;
    private final PurifyingBlast purifyingBlast;

    public Enlightenment(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        consolation = mysticAbilities.getConsolation();
        purifyingBlast = mysticAbilities.getPurifyingBlast();
    }

    public void use(Player player){
        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (abilityReadyInMap.get(player.getUniqueId()) > 0) {
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 20);
        new BukkitRunnable() {
            @Override
            public void run() {

                if (abilityReadyInMap.get(player.getUniqueId()) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0, 20);
    }

    private void execute(Player player){

        List<Player> targets = consolation.getTargets(player);
        targets.add(player);

        double increment = (2 * Math.PI) / 16; // angle between particles

        for (int i = 0; i < 16; i++) {
            double angle = i * increment;
            double x = player.getLocation().getX() + (1 * Math.cos(angle));
            double z = player.getLocation().getZ() + (1 * Math.sin(angle));
            Location loc = new Location(player.getLocation().getWorld(), x, (player.getLocation().getY()), z);

            player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
        }

        for (Player target : targets) {

            boolean crit = damageCalculator.checkIfCrit(player, 0);

            double healAmount  = damageCalculator.calculateHealing(target, player, getHealPercent(player), crit);

            if(crit){
                purifyingBlast.resetCooldown(player);
            }

            changeResourceHandler.addHealthToEntity(target, healAmount, player);
            buffAndDebuffManager.getDamageReduction().applyDamageReduction(target, .6, 20*10);

            if(player.getWorld() == target.getWorld()){
                Location start = player.getLocation();
                Location end = target.getLocation();

                // Calculate the direction vector between the two locations
                double distance = start.distance(end);
                double incrementX = (end.getX() - start.getX()) / distance * 0.5;
                double incrementY = (end.getY() - start.getY()) / distance * 0.5;
                double incrementZ = (end.getZ() - start.getZ()) / distance * 0.5;


                // Iterate over the points between the start and end locations
                while (distance > 0) {
                    // Spawn particle at current location

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = start.getX() + (1 * Math.cos(angle));
                        double z = start.getZ() + (1 * Math.sin(angle));
                        Location loc = new Location(start.getWorld(), x, (start.getY()), z);

                        player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }


                    // Move to the next point
                    start.add(incrementX, incrementY, incrementZ);
                    distance -= 0.5;
                }
            }

        }

        consolation.removeTargets(player);

    }

    public double getHealPercent(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        return 10 + ((int)(skillLevel/3));
    }

    public double getCost(){
        return 20;
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
