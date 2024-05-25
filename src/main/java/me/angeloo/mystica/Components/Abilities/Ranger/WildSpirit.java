package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Components.Abilities.RangerAbilities;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WildSpirit {

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
    private final StarVolley starVolley;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, ArmorStand> wildSpiritMap = new HashMap<>();

    public WildSpirit(Mystica main, AbilityManager manager, RangerAbilities rangerAbilities){
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
        starVolley = rangerAbilities.getStarVolley();
    }

    public void sendSignal(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(getCooldown(player) > 0){
            return;
        }

        if(wildSpiritMap.containsKey(player.getUniqueId())){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        spawn(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 7);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 7);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private void spawn(Player player){

        Location start = player.getLocation();
        start.subtract(0, 1.7, 0);
        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack wolf = new ItemStack(Material.ARROW);
        ItemMeta meta = wolf.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(6);
        wolf.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(wolf);

        wildSpiritMap.put(player.getUniqueId(), armorStand);

        wolfAiTask(player);

    }

    private void wolfAiTask(Player player){

        boolean scout = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("scout");

        double attack = profileManager.getAnyProfile(player).getTotalAttack();


        boolean tamer = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("animal tamer");

        ArmorStand wolf = wildSpiritMap.get(player.getUniqueId());

        double finalSkillDamage = getSkillDamage(player);
        new BukkitRunnable(){
            int wolfAttackReadyIn = 0;
            LivingEntity wolfTarget = null;
            int count = 0;
            @Override
            public void run(){

                if(profileManager.getAnyProfile(player).getIfDead()){
                    despawn();
                    return;
                }

                LivingEntity target = targetManager.getPlayerTarget(player);

                if(target != null){

                    if(target instanceof Player){
                        if(pvpManager.pvpLogic(player, (Player) target)){
                            wolfTarget = target;
                        }
                    }

                    if(!(target instanceof Player)){
                        if(pveChecker.pveLogic(target)){
                            wolfTarget = target;
                        }
                    }
                }

                if(wolfTarget != null){

                    if(wolfTarget instanceof Player && wolfTarget != player){
                        boolean deathStatus = profileManager.getAnyProfile(wolfTarget).getIfDead();

                        if(deathStatus){
                            wolfTarget = null;
                        }
                    }

                }

                if(wolfTarget != null){
                    if(wolfTarget.isDead()){
                        wolfTarget = null;
                    }
                }

                if(wolfTarget == null){
                    wolfTarget = player;
                }

                if(wolf.getWorld() != player.getWorld()
                || wolf.getWorld() != wolfTarget.getWorld()){
                    despawn();
                    return;
                }

                if(wolfTarget == player){
                    goToOwner();
                }
                else{
                    goToEnemy();
                }

                if(tamer){

                    if(count%40 == 0){
                        healNearby();
                    }

                }

                if(count%20 == 0){
                    if(wolfAttackReadyIn > 0){
                        wolfAttackReadyIn --;
                    }
                }


                if(count >= 20 * 15){
                    despawn();
                }

                count++;
            }


            private void goToEnemy(){
                Location current = wolf.getLocation();
                Location enemyLoc = wolfTarget.getLocation().clone().subtract(0,1.7,0);
                Vector direction = enemyLoc.toVector().subtract(current.toVector());

                double distance = current.distance(enemyLoc);

                double distanceThisTick = Math.min(distance, .3);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                if(distance > 4){
                    wolf.teleport(current);
                }
                else{
                    tryToAttack();
                }

            }

            private void tryToAttack(){

                if(wolfAttackReadyIn > 0){
                    return;
                }

                wolfAttackReadyIn = 3;

                boolean crit = damageCalculator.checkIfCrit(player, 0);

                if(scout && crit){
                    starVolley.decreaseCooldown(player);
                    buffAndDebuffManager.getHaste().applyHaste(player, 1, 2*20);
                }

                double damage = damageCalculator.calculateDamage(player, wolfTarget, "Physical", finalSkillDamage, crit);

                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(wolfTarget, player));
                changeResourceHandler.subtractHealthFromEntity(wolfTarget, damage, player);
            }

            private void goToOwner(){

                Location current = wolf.getLocation();
                Location ownerLoc = player.getLocation().clone().subtract(0,1.7,0);
                Vector direction = ownerLoc.toVector().subtract(current.toVector());

                double distance = current.distance(ownerLoc);

                double distanceThisTick = Math.min(distance, .2);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                if(distance > 4){
                    wolf.teleport(current);
                }

            }

            private void healNearby(){

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = wolf.getLocation().getX() + (5 * Math.cos(angle));
                    double z = wolf.getLocation().getZ() + (5 * Math.sin(angle));
                    Location loc = new Location(wolf.getWorld(), x, (wolf.getLocation().getY() + 1.7), z);

                    wolf.getWorld().spawnParticle(Particle.SCRAPE, loc, 1,0, 0, 0, 0);
                }

                BoundingBox hitBox = new BoundingBox(
                        wolf.getLocation().getX() - 5,
                        wolf.getLocation().getY() - 4,
                        wolf.getLocation().getZ() - 5,
                        wolf.getLocation().getX() + 5,
                        wolf.getLocation().getY() + 4,
                        wolf.getLocation().getZ() + 5
                );

                for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity hitEntity = (LivingEntity) entity;

                    if(entity != player){

                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(player, (Player)hitEntity)){
                                continue;
                            }
                        }
                    }

                    if(!(entity instanceof Player)){
                        if(pveChecker.pveLogic(hitEntity)){
                            continue;
                        }
                    }

                    double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                            profileManager.getAnyProfile(player).getSkillLevels().getSkill_7_Level_Bonus();
                    double healAmount = (attack * .1) * skillLevel;

                    changeResourceHandler.addHealthToEntity(hitEntity, healAmount, player);

                }
            }

            private void despawn(){

                wolf.remove();
                wildSpiritMap.remove(player.getUniqueId());
                this.cancel();

            }

        }.runTaskTimer(main, 0, 1);

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_7_Level_Bonus();
        return 10 + ((int)(skillLevel/3));
    }

    public double getCost(){
        return 10;
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
