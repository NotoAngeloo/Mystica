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
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final PurifyingBlast purifyingBlast;

    public Enlightenment(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        purifyingBlast = mysticAbilities.getPurifyingBlast();
    }

    public void use(Player player){
        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (abilityReadyInMap.get(player.getUniqueId()) > 0) {
            return;
        }

        double cost = 20;

        if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 20);
        new BukkitRunnable() {
            @Override
            public void run() {

                if (abilityReadyInMap.get(player.getUniqueId()) <= 0) {
                    cooldownDisplayer.displayUltimateCooldown(player);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayUltimateCooldown(player);

            }
        }.runTaskTimer(main, 0, 20);
    }

    private void execute(Player player){
        //crit heal resets puri blast

        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();

        Location center = player.getLocation();

        BoundingBox hitBox = new BoundingBox(
                center.getX() - 10,
                center.getY() - 10,
                center.getZ() - 10,
                center.getX() + 10,
                center.getY() + 10,
                center.getZ() + 10
        );

        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

            if(!(entity instanceof Player)){
                continue;
            }

            if(entity instanceof ArmorStand){
                continue;
            }

            Player healedPlayer = (Player) entity;

            if (pvpManager.pvpLogic(player, healedPlayer)) {
                continue;
            }

            double totalTargetHealth = profileManager.getAnyProfile(healedPlayer).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(healedPlayer);
            double yourMagic = profileManager.getAnyProfile(player).getTotalMagic();
            boolean crit = damageCalculator.checkIfCrit(player, 0);

            double healAmount = (totalTargetHealth + skillLevel) * .1;
            healAmount = healAmount * (yourMagic/4);

            if(crit){
                healAmount = healAmount * 1.5;
                purifyingBlast.resetCooldown(player);
            }

            changeResourceHandler.addHealthToEntity(healedPlayer, healAmount);
            buffAndDebuffManager.getDamageReduction().applyDamageReduction(healedPlayer, .9, 20*10);

            double increment = (2 * Math.PI) / 16; // angle between particles

            for (int i = 0; i < 16; i++) {
                double angle = i * increment;
                double x = center.getX() + (1 * Math.cos(angle));
                double z = center.getZ() + (1 * Math.sin(angle));
                Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                healedPlayer.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
            }

        }

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
