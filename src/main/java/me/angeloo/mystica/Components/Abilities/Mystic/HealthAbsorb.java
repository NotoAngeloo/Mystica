package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class HealthAbsorb {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;


    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public HealthAbsorb(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();

    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double totalRange = getRange(player);

        targetManager.setTargetToNearestValid(player, totalRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) target)){
                    return;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return;
                }
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > totalRange){
                return;
            }
        }

        if(target == null){
            return;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 20);
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

    private double getRange(Player player){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return baseRange + extraRange;
    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        double skillDamage = 3;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_8_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_8_Level_Bonus();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        abilityManager.setCasting(player, true);

        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            final List<ArmorStand> armorStands = new ArrayList<>();
            int ran = 0;

            Vector initialDirection;
            boolean up = true;
            int angle = 0;
            double height = 0;
            final double radius = 4;
            @Override
            public void run(){

                if(targetInvalid(target)){
                    cancelTask();
                    return;
                }

                if(!player.isOnline() || buffAndDebuffManager.getIfInterrupt(player)){
                    cancelTask();
                    return;
                }

                Location playerLoc = player.getLocation().clone();

                if(profileManager.getAnyProfile(player).getIfDead()){
                    cancelTask();
                    return;
                }

                Location targetLoc = target.getLocation().clone().subtract(0,1,0);

                double distanceToTarget = playerLoc.distance(targetLoc);

                if(distanceToTarget>getRange(player)){
                    cancelTask();
                    return;
                }

                if (initialDirection == null) {
                    initialDirection = playerLoc.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);
                playerLoc.setDirection(rotation);

                double x = playerLoc.getX() + rotation.getX() * radius;
                double z = playerLoc.getZ() + rotation.getZ() * radius;

                double x2 = playerLoc.getX() - rotation.getX() * radius;
                double z2 = playerLoc.getZ() - rotation.getZ() * radius;

                Location particleLoc = new Location(playerLoc.getWorld(), x, playerLoc.getY() + height, z);
                Location particleLoc2 = new Location(playerLoc.getWorld(), x2, playerLoc.getY() + height, z2);

                player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc, 1, 0, 0, 0, 0);
                player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc2, 1, 0, 0, 0, 0);


                if(up){
                    height += .1;
                }
                else{
                    height -= .1;
                }

                angle += 5;

                if(height >= 4){
                    up = false;
                }

                if(height < 0){
                    up = true;
                }



                ArmorStand armorStand = targetLoc.getWorld().spawn(targetLoc, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setMarker(true);

                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack absorbItem = new ItemStack(Material.SPECTRAL_ARROW);
                ItemMeta meta = absorbItem.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(10);
                absorbItem.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(absorbItem);

                armorStands.add(armorStand);

                new BukkitRunnable(){
                    final Location current = targetLoc.clone();
                    @Override
                    public void run(){

                        if(!player.isOnline()){
                            this.cancel();
                            return;
                        }

                        if(targetInvalid(target)){
                            this.cancel();
                            return;
                        }

                        Location playerLoc = player.getLocation().clone().subtract(0,1,0);

                        Vector direction = playerLoc.toVector().subtract(current.toVector());
                        double distance = current.distance(playerLoc);
                        double distanceThisTick = Math.min(distance, .75);
                        current.add(direction.normalize().multiply(distanceThisTick));
                        current.setDirection(direction);

                        armorStand.teleport(current);

                        if (distance <= 1) {
                            this.cancel();
                            armorStand.remove();
                        }

                    }
                }.runTaskTimer(main, 0, 1);

                if(ran%20==0){
                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit);

                    double healed = damage * .3;

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);
                    changeResourceHandler.addHealthToEntity(player, healed, player);
                }

                double percent = ((double) ran /(20*5)) * 100;

                abilityManager.setCastBar(player, percent);

                ran++;

                if(ran >= 20*5){
                    cancelTask();
                }

            }

            private boolean targetInvalid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){

                        return true;
                    }
                }
                return target.isDead();
            }

            private void cancelTask() {
                this.cancel();
                removeArmorStands(armorStands);
                abilityManager.setCasting(player, false);
                abilityManager.setCastBar(player, 0);
            }


            private void removeArmorStands(List<ArmorStand> stands){

                if(armorStands.isEmpty()){
                    return;
                }

                for(ArmorStand stand : stands){
                    stand.remove();
                }
            }

        }.runTaskTimer(main, 0L, 1);

    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
