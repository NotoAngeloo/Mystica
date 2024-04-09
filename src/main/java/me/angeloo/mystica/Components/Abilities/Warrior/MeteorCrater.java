package me.angeloo.mystica.Components.Abilities.Warrior;

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
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MeteorCrater {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public MeteorCrater(Mystica main, AbilityManager manager){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        abilityManager = manager;
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }


        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        Block block = player.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 20);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 4);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 4);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        double baseRange = 8;

        targetManager.setTargetToNearestValid(player, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        boolean targeted = false;

        Vector direction = player.getLocation().getDirection().setY(0).normalize();

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(player, (Player) target)){

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }

            if(!(target instanceof Player)){
                if(pveChecker.pveLogic(target)){

                    double distance = player.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }


        }

        if(targeted){
            direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize();
        }

        Location start = player.getLocation().clone();
        Location up = start.clone().add(0,3,0);
        Location end = start.clone().add(direction.multiply(2));
        end.setDirection(direction);



        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(player);
        Vector finalDirection = direction;
        new BukkitRunnable(){
            ArmorStand stand;
            int count = 0;
            boolean spawn = false;
            boolean land = false;
            boolean going = true;
            @Override
            public void run(){

                if(!player.isOnline() || buffAndDebuffManager.getIfInterrupt(player)){
                    cancelTask();
                    //abilityManager.setSkillRunning(player, false);
                    return;
                }

                Location current = player.getLocation();
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
                    player.teleport(current);
                }

                if(land && !spawn){
                    stand = player.getWorld().spawn(end.clone().subtract(0,5,0), ArmorStand.class);
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
                        player.getWorld().spawnParticle(Particle.CRIT, loc, 1,0, 0, 0, 0);
                    }

                    BoundingBox hitBox = new BoundingBox(
                            end.getX() - 4,
                            end.getY() - 2,
                            end.getZ() - 4,
                            end.getX() + 4,
                            end.getY() + 4,
                            end.getZ() + 4
                    );

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

                        boolean crit = damageCalculator.checkIfCrit(player, 0);

                        double healthPercent = profileManager.getAnyProfile(livingEntity).getCurrentHealth() / (profileManager.getAnyProfile(livingEntity).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(livingEntity));

                        double bonus = 1;

                        if(healthPercent>=.7){
                            bonus = 1.3;
                        }

                        double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage * bonus, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(player, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                                stunEntity(target);
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
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

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getCost(){
        return 20;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_4_Level_Bonus();
        return 80 + ((int)(skillLevel/10));
    }
}
