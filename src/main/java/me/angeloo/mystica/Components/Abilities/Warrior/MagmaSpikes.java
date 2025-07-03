package me.angeloo.mystica.Components.Abilities.Warrior;

import me.angeloo.mystica.Components.Abilities.WarriorAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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

import java.util.*;

public class MagmaSpikes {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;
    private final Rage rage;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public MagmaSpikes(Mystica main, AbilityManager manager, WarriorAbilities warriorAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        damageCalculator = main.getDamageCalculator();
        combatManager = manager.getCombatManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        changeResourceHandler = main.getChangeResourceHandler();
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

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 20);
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

    private void execute(LivingEntity caster){

        Location start = caster.getLocation();
        Location up = start.clone().add(0,4,0);


        double skillDamage = getSkillDamage(caster);

        //abilityManager.setSkillRunning(player, true);
        skillDamage = skillDamage / 2;
        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            final List<ArmorStand> stands = new ArrayList<>();
            boolean going = true;
            int count = 0;
            int ran = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(caster.isDead()){
                    cancelTask();
                    return;
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    cancelTask();
                    return;
                }

                Location current = caster.getLocation();

                if(going){
                    double distance = current.distance(up);
                    double distanceThisTick = Math.min(distance, .3);
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
                    double distanceThisTick = Math.min(distance, .3);
                    Vector downDir = start.toVector().subtract(current.toVector());

                    if(distanceThisTick!=0){
                        current.add(downDir.normalize().multiply(distanceThisTick));
                    }

                    if(distance<=1){
                        going = true;

                        //damage and stand
                        if(count==0){
                            spawnSmallStands(start);
                        }
                        if(count==1){
                            spawnLargeStands(start);
                            abilityManager.setCasting(caster, false);
                        }

                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double x = start.getX() + (5 * Math.cos(angle));
                            double y = start.getY() + 1;
                            double z = start.getZ() + (5 * Math.sin(angle));
                            Location loc = new Location(start.getWorld(), x, y, z);
                            caster.getWorld().spawnParticle(Particle.LAVA, loc, 1,0, 0, 0, 0);
                        }

                        BoundingBox hitBox = new BoundingBox(
                                start.getX() - 5,
                                start.getY() - 2,
                                start.getZ() - 5,
                                start.getX() + 5,
                                start.getY() + 5,
                                start.getZ() + 5
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

                            double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage, crit));

                            //pvp logic
                            if(entity instanceof Player){
                                if(pvpManager.pvpLogic(caster, (Player) entity)){
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                    rage.addRageToEntity(caster, 10);

                                    if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                                        Vector velocity = (new Vector(0, .75, 0));
                                        livingEntity.setVelocity(velocity);
                                        buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                                    }
                                }
                                continue;
                            }

                            if(pveChecker.pveLogic(livingEntity)){
                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                rage.addRageToEntity(caster, 10);

                                if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                                    Vector velocity = (new Vector(0, .75, 0));
                                    livingEntity.setVelocity(velocity);
                                    buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                                }
                            }

                        }

                        count++;
                    }
                }

                if(count<2){
                    caster.teleport(current);
                }


                if(ran>=20*5){
                    cancelTask();
                }

                ran++;
            }

            private void spawnSmallStands(Location center){
                ItemStack item = new ItemStack(Material.NETHER_WART);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(8);
                item.setItemMeta(meta);

                Vector direction = center.getDirection().setY(0).normalize();
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location s1spawn = center.clone().add(direction.clone().multiply(2)).setDirection(direction.clone());
                ArmorStand stand = caster.getWorld().spawn(s1spawn.clone().subtract(0,5,0), ArmorStand.class);
                stand.setInvisible(true);
                stand.setGravity(false);
                stand.setCollidable(false);
                stand.setInvulnerable(true);
                stand.setMarker(true);
                EntityEquipment entityEquipment = stand.getEquipment();
                assert entityEquipment != null;
                entityEquipment.setHelmet(item);
                stand.teleport(s1spawn);
                stands.add(stand);

                Location s2spawn = center.clone().subtract(direction.clone().multiply(2)).setDirection(direction.clone().multiply(-1));
                ArmorStand stand2 = caster.getWorld().spawn(s2spawn.clone().subtract(0,5,0), ArmorStand.class);
                stand2.setInvisible(true);
                stand2.setGravity(false);
                stand2.setCollidable(false);
                stand2.setInvulnerable(true);
                stand2.setMarker(true);
                EntityEquipment entityEquipment2 = stand2.getEquipment();
                assert entityEquipment2 != null;
                entityEquipment2.setHelmet(item);
                stand2.teleport(s2spawn);
                stands.add(stand2);

                Location s3spawn = center.clone().add(crossProduct.clone().multiply(2)).setDirection(crossProduct.clone());
                ArmorStand stand3 = caster.getWorld().spawn(s3spawn.clone().subtract(0,5,0), ArmorStand.class);
                stand3.setInvisible(true);
                stand3.setGravity(false);
                stand3.setCollidable(false);
                stand3.setInvulnerable(true);
                stand3.setMarker(true);
                EntityEquipment entityEquipment3 = stand3.getEquipment();
                assert entityEquipment3 != null;
                entityEquipment3.setHelmet(item);
                stand3.teleport(s3spawn);
                stands.add(stand3);

                Location s4spawn = center.clone().subtract(crossProduct.clone().multiply(2)).setDirection(crossProduct.clone().multiply(-1));
                ArmorStand stand4 = caster.getWorld().spawn(s4spawn.clone().subtract(0,5,0), ArmorStand.class);
                stand4.setInvisible(true);
                stand4.setGravity(false);
                stand4.setCollidable(false);
                stand4.setInvulnerable(true);
                stand4.setMarker(true);
                EntityEquipment entityEquipment4 = stand4.getEquipment();
                assert entityEquipment4 != null;
                entityEquipment4.setHelmet(item);
                stand4.teleport(s4spawn);
                stands.add(stand4);
            }

            private void spawnLargeStands(Location center){
                ItemStack item = new ItemStack(Material.NETHER_WART);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(9);
                item.setItemMeta(meta);

                Vector direction = center.getDirection().setY(0).normalize();
                direction.rotateAroundY(45);
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location s1spawn = center.clone().add(direction.clone().multiply(4)).setDirection(direction.clone());
                ArmorStand stand = caster.getWorld().spawn(s1spawn.clone().subtract(0,5,0), ArmorStand.class);
                stand.setInvisible(true);
                stand.setGravity(false);
                stand.setCollidable(false);
                stand.setInvulnerable(true);
                stand.setMarker(true);
                EntityEquipment entityEquipment = stand.getEquipment();
                assert entityEquipment != null;
                entityEquipment.setHelmet(item);
                stand.teleport(s1spawn);
                stands.add(stand);

                Location s2spawn = center.clone().subtract(direction.clone().multiply(4)).setDirection(direction.clone().multiply(-1));
                ArmorStand stand2 = caster.getWorld().spawn(s2spawn.clone().subtract(0,5,0), ArmorStand.class);
                stand2.setInvisible(true);
                stand2.setGravity(false);
                stand2.setCollidable(false);
                stand2.setInvulnerable(true);
                stand2.setMarker(true);
                EntityEquipment entityEquipment2 = stand2.getEquipment();
                assert entityEquipment2 != null;
                entityEquipment2.setHelmet(item);
                stand2.teleport(s2spawn);
                stands.add(stand2);

                Location s3spawn = center.clone().add(crossProduct.clone().multiply(4)).setDirection(crossProduct.clone());
                ArmorStand stand3 = caster.getWorld().spawn(s3spawn.clone().subtract(0,5,0), ArmorStand.class);
                stand3.setInvisible(true);
                stand3.setGravity(false);
                stand3.setCollidable(false);
                stand3.setInvulnerable(true);
                stand3.setMarker(true);
                EntityEquipment entityEquipment3 = stand3.getEquipment();
                assert entityEquipment3 != null;
                entityEquipment3.setHelmet(item);
                stand3.teleport(s3spawn);
                stands.add(stand3);

                Location s4spawn = center.clone().subtract(crossProduct.clone().multiply(4)).setDirection(crossProduct.clone().multiply(-1));
                ArmorStand stand4 = caster.getWorld().spawn(s4spawn.clone().subtract(0,5,0), ArmorStand.class);
                stand4.setInvisible(true);
                stand4.setGravity(false);
                stand4.setCollidable(false);
                stand4.setInvulnerable(true);
                stand4.setMarker(true);
                EntityEquipment entityEquipment4 = stand4.getEquipment();
                assert entityEquipment4 != null;
                entityEquipment4.setHelmet(item);
                stand4.teleport(s4spawn);
                stands.add(stand4);
            }

            private void cancelTask(){
                //abilityManager.setSkillRunning(player, false);
                this.cancel();
                for(ArmorStand stand : stands){
                    stand.remove();
                }
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
        return 50 + ((int)(skillLevel/3));
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        return block.getType() != Material.AIR;
    }

}
