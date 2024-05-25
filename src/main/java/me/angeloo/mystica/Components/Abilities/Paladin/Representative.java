package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import me.angeloo.mystica.Utility.ShieldAbilityManaDisplayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Representative {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Map<UUID, Double> repBuff = new HashMap<>();


    public Representative(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
    }

    public void use(Player player){

        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (getCooldown(player) > 0) {
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 30);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(player) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo(player);

            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(player.getUniqueId(), task);
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

        double finalHealPower = getHealPower(player);
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


                            if(!(entity instanceof LivingEntity)){
                                continue;
                            }

                            if(entity instanceof ArmorStand){
                                continue;
                            }

                            LivingEntity hitEntity = (LivingEntity) entity;

                            if(entity instanceof Player){
                                if(pvpManager.pvpLogic(player, (Player)hitEntity)){
                                    continue;
                                }
                            }

                            if(!(entity instanceof Player)){
                                if(pveChecker.pveLogic(hitEntity)){
                                    continue;
                                }
                            }


                            boolean crit = damageCalculator.checkIfCrit(player, 0);
                            double healAmount = damageCalculator.calculateHealing(player, finalHealPower, crit);
                            changeResourceHandler.addHealthToEntity(hitEntity, healAmount, player);

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

    public double getHealPower(Player player){
        double level = profileManager.getAnyProfile(player).getStats().getLevel();
        return 25 +  ((int)(level/3));
    }

    public double getCost() {
        return 20;
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
