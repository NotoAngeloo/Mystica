package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.Components.Abilities.ElementalistAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class FieryMagma {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final FieryWing fieryWing;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public FieryMagma(Mystica main, AbilityManager manager, ElementalistAbilities elementalistAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        fieryWing = elementalistAbilities.getFieryWing();

    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

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


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 2);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 2);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location end = target.getLocation();
        Location playerLoc = player.getLocation();
        Vector spawnDirection = end.toVector().subtract(playerLoc.toVector());
        Location spawnLoc = target.getLocation().clone().add(spawnDirection.clone().normalize().multiply(10));
        spawnLoc.add(0,10,0);

        ArmorStand armorStand = spawnLoc.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack meteorItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = meteorItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(5);
        meteorItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(meteorItem);

        double finalSkillDamage = getSkillDamage(player);
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location current = armorStand.getLocation();

                if (!sameWorld(current, targetWasLoc)) {
                    cancelTask();
                    return;
                }

                Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                double distance = current.distance(targetWasLoc);
                double distanceThisTick = Math.min(distance, .75);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                armorStand.teleport(current);

                armorStand.getWorld().spawnParticle(Particle.LAVA, current.clone().add(0,2,0), 1);

                if (distance <= 1) {

                    fieryWing.addInflame(player);


                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                    startBurningTask();
                }

            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private boolean sameWorld(Location loc1, Location loc2) {
                return loc1.getWorld().equals(loc2.getWorld());
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

            private void startBurningTask(){

                double burn = finalSkillDamage * .1;

                new BukkitRunnable(){
                    int ticks = 0;
                    @Override
                    public void run(){

                        if(profileManager.getIfResetProcessing(target)){
                            this.cancel();
                            return;
                        }

                        if(target.isDead()){
                            this.cancel();
                            return;
                        }

                        if(target instanceof Player){
                            if(!((Player)target).isOnline()){
                                this.cancel();
                                return;
                            }

                            if(profileManager.getAnyProfile(target).getIfDead()){
                                this.cancel();
                                return;
                            }
                        }

                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double tickDamage = damageCalculator.calculateDamage(player, target, "Magical", burn, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                        changeResourceHandler.subtractHealthFromEntity(target, tickDamage, player);

                        ticks ++;


                        if(ticks >=3){
                            this.cancel();
                            //explode
                            Set<LivingEntity> hitBySkill = new HashSet<>();

                            BoundingBox hitBox = new BoundingBox(
                                    target.getLocation().getX() - 4,
                                    target.getLocation().getY() - 2,
                                    target.getLocation().getZ() - 4,
                                    target.getLocation().getX() + 4,
                                    target.getLocation().getY() + 4,
                                    target.getLocation().getZ() + 4
                            );

                            double increment = (2 * Math.PI) / 16; // angle between particles

                            for (int i = 0; i < 16; i++) {
                                double angle = i * increment;
                                double x = target.getLocation().getX() + (4 * Math.cos(angle));
                                double z = target.getLocation().getZ() + (4 * Math.sin(angle));
                                Location loc = new Location(target.getWorld(), x, (target.getLocation().getY()), z);

                                target.getWorld().spawnParticle(Particle.FLAME, loc, 1,0, 0, 0, 0);
                            }

                            for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                                if(entity == player){
                                    continue;
                                }

                                if(!(entity instanceof LivingEntity)){
                                    continue;
                                }

                                if(entity instanceof ArmorStand){
                                    continue;
                                }

                                LivingEntity livingEntity = (LivingEntity) entity;

                                if(hitBySkill.contains(livingEntity)){
                                    continue;
                                }

                                hitBySkill.add(livingEntity);

                                boolean crit2 = damageCalculator.checkIfCrit(player, 0);
                                double damage = (damageCalculator.calculateDamage(player, livingEntity, "Magical", finalSkillDamage, crit2));

                                //pvp logic
                                if(entity instanceof Player){
                                    if(pvpManager.pvpLogic(player, (Player) entity)){
                                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                                    }
                                    continue;
                                }

                                if(pveChecker.pveLogic(livingEntity)){
                                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                                }

                            }

                        }
                    }
                }.runTaskTimer(main, 0, 20);

            }


        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level_Bonus();

        return 20 + ((int)(skillLevel/10));
    }

    public double getCost(){
        return 5;
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }
}
