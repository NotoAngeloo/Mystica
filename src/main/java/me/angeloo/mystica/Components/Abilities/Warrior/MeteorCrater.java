package me.angeloo.mystica.Components.Abilities.Warrior;

import me.angeloo.mystica.Components.Abilities.WarriorAbilities;
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
import org.bukkit.block.Block;
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

public class MeteorCrater {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Rage rage;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public MeteorCrater(Mystica main, AbilityManager manager, WarriorAbilities warriorAbilities){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        rage = warriorAbilities.getRage();
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        rage.subTractRageFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 2);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 4);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 4);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        double baseRange = 8;

        targetManager.setTargetToNearestValid(caster, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        boolean targeted = false;

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(caster, (Player) target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }

            if(!(target instanceof Player)){
                if(pveChecker.pveLogic(target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }


        }

        if(targeted){
            direction = target.getLocation().toVector().subtract(caster.getLocation().toVector()).setY(0).normalize();
        }

        Location start = caster.getLocation().clone();
        Location up = start.clone().add(0,3,0);
        Location end = start.clone().add(direction.multiply(2));
        end.setDirection(direction);



        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(caster);
        Vector finalDirection = direction;
        new BukkitRunnable(){
            ArmorStand stand;
            int count = 0;
            boolean spawn = false;
            boolean land = false;
            boolean going = true;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    cancelTask();
                    //abilityManager.setSkillRunning(player, false);
                    return;
                }

                Location current = caster.getLocation();
                current.setDirection(finalDirection);

                if(going){
                    double distance = current.distance(up);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector upDir = up.toVector().subtract(current.toVector());

                    if(distanceThisTick!=0){
                        current.add(upDir.normalize().multiply(distanceThisTick));
                    }

                    if(distance<=1){
                        going=false;
                    }
                }
                else{
                    double distance = current.distance(start);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector downDir = start.toVector().subtract(current.toVector());

                    if(distanceThisTick!=0){
                        current.add(downDir.normalize().multiply(distanceThisTick));
                    }

                    if(distance<=1){
                        land=true;
                        //abilityManager.setSkillRunning(player, false);
                    }
                }

                if(!land){
                    caster.teleport(current);
                }

                if(land && !spawn){
                    stand = caster.getWorld().spawn(end.clone().subtract(0,5,0), ArmorStand.class);
                    stand.setInvisible(true);
                    stand.setGravity(false);
                    stand.setCollidable(false);
                    stand.setInvulnerable(true);
                    stand.setMarker(true);
                    ItemStack item = new ItemStack(Material.NETHER_WART);
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    meta.setCustomModelData(6);
                    item.setItemMeta(meta);
                    EntityEquipment entityEquipment = stand.getEquipment();
                    assert entityEquipment != null;
                    entityEquipment.setHelmet(item);
                    stand.teleport(end);
                    spawn=true;

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = end.getX() + (4 * Math.cos(angle));
                        double y = end.getY() + 1;
                        double z = end.getZ() + (4 * Math.sin(angle));
                        Location loc = new Location(end.getWorld(), x, y, z);
                        caster.getWorld().spawnParticle(Particle.CRIT, loc, 1,0, 0, 0, 0);
                    }

                    BoundingBox hitBox = new BoundingBox(
                            end.getX() - 4,
                            end.getY() - 2,
                            end.getZ() - 4,
                            end.getX() + 4,
                            end.getY() + 4,
                            end.getZ() + 4
                    );

                    for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == caster){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        LivingEntity livingEntity = (LivingEntity) entity;

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);

                        double healthPercent = profileManager.getAnyProfile(livingEntity).getCurrentHealth() / (profileManager.getAnyProfile(livingEntity).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(livingEntity));

                        double bonus = 1;

                        if(healthPercent>=.7){
                            bonus = 1.3;
                        }

                        double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage * bonus, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(caster, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);
                                buffAndDebuffManager.getBossInterrupt().interrupt(caster, target);
                                stunEntity(target);
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);
                            buffAndDebuffManager.getBossInterrupt().interrupt(caster, target);
                            stunEntity(target);
                        }

                    }
                }

                if(spawn){
                    count++;
                }

                if(count>=20*5){
                    cancelTask();
                }
            }

            private void stunEntity(LivingEntity target){

                if(!profileManager.getAnyProfile(target).getIsMovable()){
                    return;
                }

                //should be stun instead
                buffAndDebuffManager.getStun().applyStun(target, 20);
            }

            private void cancelTask(){
                this.cancel();
                stand.remove();
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

    public int getCost(){
        return 100;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();
        return 80 + ((int)(skillLevel/3));
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return false;
        }

        if(rage.getCurrentRage(caster) < getCost()){
            return false;
        }

        return true;
    }

    public int returnWhichItem(Player player){

        if(rage.getCurrentRage(player)<getCost()){
            return 8;
        }

        return 0;
    }

}


