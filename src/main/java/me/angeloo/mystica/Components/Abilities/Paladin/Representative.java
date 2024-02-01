package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Representative {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Map<UUID, Double> repBuff = new HashMap<>();


    public Representative(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
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

        abilityReadyInMap.put(player.getUniqueId(), 30);
        new BukkitRunnable() {
            @Override
            public void run() {

                if (abilityReadyInMap.get(player.getUniqueId()) <= 0) {
                    cooldownDisplayer.displayUltimateCooldown(player);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayUltimateCooldown(player);

            }
        }.runTaskTimer(main, 0, 20);
    }

    private void execute(Player player){

        if(!player.isOnline()){
            return;
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        ItemStack wings = new ItemStack(Material.SUGAR);
        ItemMeta meta = wings.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(13);
        wings.setItemMeta(meta);
        player.getInventory().setHelmet(wings);

        double level = profileManager.getAnyProfile(player).getStats().getLevel();
        applyBuff(player, level);
        buffAndDebuffManager.getHaste().applyHaste(player, 3, 10*20);

        Location center = player.getLocation().clone();

        new BukkitRunnable(){
            int count = 0;
            boolean aoe = true;
            double progress = 0;
            final int maxDistance = 10;
            @Override
            public void run(){

                if(aoe){
                    double radius = progress;
                    double thisNumber = (Math.pow(2, progress));
                    double increment = (2 * Math.PI) / thisNumber;

                    for (double i = 0; i < thisNumber; i++) {
                        double angle = i * increment;
                        double x = center.getX() + (radius * Math.cos(angle));
                        double z = center.getZ() + (radius * Math.sin(angle));
                        Location loc = new Location(player.getWorld(), x, center.getY(), z);
                        player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0, 0, 0, 0);
                    }


                    progress += 1;

                    if(progress >= maxDistance){
                        aoe = false;
                        BoundingBox hitBox = new BoundingBox(
                                center.getX() - progress,
                                center.getY() - 2,
                                center.getZ() - progress,
                                center.getX() + progress,
                                center.getY() + 4,
                                center.getZ() + progress
                        );

                        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {


                            if(!(entity instanceof Player)){
                                continue;
                            }

                            if(entity instanceof ArmorStand){
                                continue;
                            }

                            Player hitPlayer = (Player) entity;

                            if(pvpManager.pvpLogic(player, hitPlayer)){
                                continue;
                            }

                            boolean crit = damageCalculator.checkIfCrit(player, 0);
                            double amount = (profileManager.getAnyProfile(hitPlayer).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(hitPlayer)) * .25;
                            amount = amount + level;
                            amount = amount + getAdditionalBonusFromBuff(player);

                            if(crit){
                                amount = amount * 1.5;
                            }

                            changeResourceHandler.addHealthToEntity(hitPlayer, amount);

                        }
                    }
                }

                if(count>=10*20){
                    this.cancel();
                    removeBuff(player);
                }

                count++;
            }



        }.runTaskTimer(main, 0, 1);
    }

    private void applyBuff(Player player, double amount){
        repBuff.put(player.getUniqueId(), amount);
    }

    private void removeBuff(Player player){
        repBuff.remove(player.getUniqueId());

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(!combatStatus){
            return;
        }

        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();
        player.getInventory().setHelmet(playerEquipment.getHelmet());
    }

    public double getAdditionalBonusFromBuff(Player player){
        return repBuff.getOrDefault(player.getUniqueId(), 0.0);
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }


}
