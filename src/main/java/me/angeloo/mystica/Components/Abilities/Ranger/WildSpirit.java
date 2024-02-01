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

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        if(wildSpiritMap.containsKey(player.getUniqueId())){
            return;
        }

        double cost = 10;

        if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        spawn(player);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 7);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 7);

            }
        }.runTaskTimer(main, 0,20);

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
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_7_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_7_Level_Bonus();
        double skillDamage = 10;

        skillDamage = skillDamage + ((int)(skillLevel/10));

        boolean tamer = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("animal tamer");

        ArmorStand wolf = wildSpiritMap.get(player.getUniqueId());

        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            int wolfAttackReadyIn = 0;
            LivingEntity wolfTarget = null;
            int count = 0;
            @Override
            public void run(){

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
                current.add(direction.normalize().multiply(distanceThisTick));
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
                current.add(direction.normalize().multiply(distanceThisTick));
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

                    if(!(entity instanceof Player)){
                        continue;
                    }

                    Player thisPlayer = (Player) entity;

                    if(entity != player){
                        if(pvpManager.pvpLogic(player, thisPlayer)){
                            continue;
                        }
                    }

                    double healAmount = (attack * .1) * skillLevel;

                    changeResourceHandler.addHealthToEntity(thisPlayer, healAmount);

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

}
