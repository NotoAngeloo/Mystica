package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Components.Abilities.RangerAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Enums.SubClass;
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
    private final Focus focus;

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
        focus = rangerAbilities.getFocus();
    }

    public void sendSignal(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }


        combatManager.startCombatTimer(caster);

        spawn(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 7);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 7);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void spawn(LivingEntity caster){

        Location start = caster.getLocation();
        start.subtract(0, 1.7, 0);
        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
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

        wildSpiritMap.put(caster.getUniqueId(), armorStand);

        wolfAiTask(caster);

    }

    private void wolfAiTask(LivingEntity caster){

        boolean scout = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Scout);

        double attack = profileManager.getAnyProfile(caster).getTotalAttack();


        boolean tamer = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Tamer);

        ArmorStand wolf = wildSpiritMap.get(caster.getUniqueId());

        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            int wolfAttackReadyIn = 0;
            LivingEntity wolfTarget = null;
            int count = 0;
            @Override
            public void run(){

                if(caster.isDead()){
                    despawn();
                    return;
                }

                if(profileManager.getAnyProfile(caster).getIfDead()){
                    despawn();
                    return;
                }

                LivingEntity target = targetManager.getPlayerTarget(caster);

                if(target != null){

                    if(target instanceof Player){
                        if(pvpManager.pvpLogic(caster, (Player) target)){
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

                    if(wolfTarget instanceof Player && wolfTarget != caster){
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
                    wolfTarget = caster;
                }

                if(wolf.getWorld() != caster.getWorld()
                || wolf.getWorld() != wolfTarget.getWorld()){
                    despawn();
                    return;
                }

                if(wolfTarget == caster){
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

                boolean crit = damageCalculator.checkIfCrit(caster, 0);

                if(scout && crit){
                    starVolley.decreaseCooldown(caster);
                    buffAndDebuffManager.getHaste().applyHaste(caster, 1, 2*20);
                }

                double damage = damageCalculator.calculateDamage(caster, wolfTarget, "Physical", finalSkillDamage, crit);

                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(wolfTarget, caster));
                changeResourceHandler.subtractHealthFromEntity(wolfTarget, damage, caster, crit);
            }

            private void goToOwner(){

                Location current = wolf.getLocation();
                Location ownerLoc = caster.getLocation().clone().subtract(0,1.7,0);
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

                for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity hitEntity = (LivingEntity) entity;

                    if(entity != caster){

                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(caster, (Player)hitEntity)){
                                continue;
                            }
                        }
                    }

                    if(!(entity instanceof Player)){
                        if(pveChecker.pveLogic(hitEntity)){
                            continue;
                        }
                    }

                    double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                            profileManager.getAnyProfile(caster).getSkillLevels().getSkill_7_Level_Bonus();
                    double healAmount = (attack * .1) * skillLevel;

                    changeResourceHandler.addHealthToEntity(hitEntity, healAmount, caster);

                }
            }

            private void despawn(){

                wolf.remove();
                wildSpiritMap.remove(caster.getUniqueId());
                this.cancel();

            }

        }.runTaskTimer(main, 0, 1);

    }

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_7_Level_Bonus();
        return focus.calculateFocusMultipliedDamage(caster, 10) + ((int)(skillLevel/3));
    }


    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }

        if(wildSpiritMap.containsKey(caster.getUniqueId())){
            return false;
        }


        return true;
    }

}
