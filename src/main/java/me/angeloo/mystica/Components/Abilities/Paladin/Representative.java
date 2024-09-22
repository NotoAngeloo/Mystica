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

    public void use(LivingEntity caster){

        if (!abilityReadyInMap.containsKey(caster.getUniqueId())) {
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }


        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 30);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(caster) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(caster instanceof Player){
                    shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo((Player) caster);
                }


            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void execute(LivingEntity caster){

        if(caster instanceof Player){
            if(!((Player)caster).isOnline()){
                return;
            }
        }



        if(profileManager.getAnyProfile(caster).getIfDead()){
            return;
        }

        ItemStack wings = new ItemStack(Material.SUGAR);
        ItemMeta meta = wings.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(13);
        wings.setItemMeta(meta);

        if(caster instanceof Player){
            ((Player)caster).getInventory().setHelmet(wings);
        }



        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        applyBuff(caster, level);
        buffAndDebuffManager.getHaste().applyHaste(caster, 3, 10*20);

        Location center = caster.getLocation().clone();

        double finalHealPower = getHealPower(caster);
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
                        Location loc = new Location(caster.getWorld(), x, center.getY(), z);
                        caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0, 0, 0, 0);
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

                        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {


                            if(!(entity instanceof LivingEntity)){
                                continue;
                            }

                            if(entity instanceof ArmorStand){
                                continue;
                            }

                            LivingEntity hitEntity = (LivingEntity) entity;

                            if(entity instanceof Player){
                                if(pvpManager.pvpLogic(caster, (Player)hitEntity)){
                                    continue;
                                }
                            }

                            if(!(entity instanceof Player)){
                                if(pveChecker.pveLogic(hitEntity)){
                                    continue;
                                }
                            }


                            boolean crit = damageCalculator.checkIfCrit(caster, 0);
                            double healAmount = damageCalculator.calculateHealing(caster, finalHealPower, crit);
                            changeResourceHandler.addHealthToEntity(hitEntity, healAmount, caster);

                        }
                    }
                }

                if(count>=10*20){
                    this.cancel();
                    removeBuff(caster);
                }

                count++;
            }



        }.runTaskTimer(main, 0, 1);
    }

    private void applyBuff(LivingEntity caster, double amount){
        repBuff.put(caster.getUniqueId(), amount);
    }

    private void removeBuff(LivingEntity caster){
        repBuff.remove(caster.getUniqueId());

        boolean combatStatus = profileManager.getAnyProfile(caster).getIfInCombat();

        if(!combatStatus){
            return;
        }

        if(caster instanceof Player){
            PlayerEquipment playerEquipment = profileManager.getAnyProfile((Player)caster).getPlayerEquipment();
            ((Player)caster).getInventory().setHelmet(playerEquipment.getHelmet());
        }


    }

    public double getAdditionalBonusFromBuff(LivingEntity caster){
        return repBuff.getOrDefault(caster.getUniqueId(), 0.0);
    }

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getHealPower(LivingEntity caster){
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 25 +  ((int)(level/3));
    }


    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        return getCooldown(caster) <= 0;
    }

}
