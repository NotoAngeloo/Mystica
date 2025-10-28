package me.angeloo.mystica.Components.CombatSystem.Abilities.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.BuffAndDebuffManager;
import me.angeloo.mystica.Components.CombatSystem.CombatManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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

public class LightWell {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();


    public LightWell(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
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

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), getSkillCooldown());
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getPlayerCooldown(caster) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = getPlayerCooldown(caster) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(caster instanceof Player){
                    Bukkit.getScheduler().runTask(main, () ->{
                        Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent((Player) caster));
                    });

                }



            }
        }.runTaskTimerAsynchronously(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void execute(LivingEntity caster){

        Location spawnStart = caster.getLocation().clone();

        ArmorStand well = caster.getWorld().spawn(spawnStart, ArmorStand.class);
        well.setInvisible(true);
        well.setGravity(false);
        well.setCollidable(false);
        well.setInvulnerable(true);
        well.setMarker(true);

        EntityEquipment entityEquipment = well.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(9);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);



        Location current = well.getLocation();
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Vector initialDirection;
            int angle = 0;
            int ran = 0;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = current.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);
                current.setDirection(rotation);

                if(ran%33==0){
                    double range = (Math.random() * 8) + 1;

                    Location end = current.clone();

                    while (range > 0) {
                        end.add(rotation);
                        if (!end.getBlock().isPassable()) {
                            end.subtract(rotation.multiply(2));
                            break;
                        }
                        range -= 1;
                    }

                    spawnOrb(end.subtract(0,1.5,0));

                }

                if(ran%20==0){
                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = current.getX() + (5 * Math.cos(angle));
                        double y = current.getY() + 1;
                        double z = current.getZ() + (5 * Math.sin(angle));
                        Location loc = new Location(current.getWorld(), x, y, z);
                        caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }

                    BoundingBox hitBox = new BoundingBox(
                            current.getX() - 5,
                            current.getY() - 2,
                            current.getZ() - 5,
                            current.getX() + 5,
                            current.getY() + 6,
                            current.getZ() + 5
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
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                        }

                    }
                }

                if(ran>=20*5){
                    cancelTask();
                }

                angle += 5;
                ran++;

            }

            private void spawnOrb(Location end){

                ArmorStand orb = caster.getWorld().spawn(current, ArmorStand.class);
                orb.setInvisible(true);
                orb.setGravity(false);
                orb.setCollidable(false);
                orb.setInvulnerable(true);
                orb.setMarker(true);

                EntityEquipment entityEquipment = orb.getEquipment();

                ItemStack item = new ItemStack(Material.SUGAR);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(10);
                item.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(item);

                new BukkitRunnable(){
                    final double length = current.distance(end);
                    final double half = length/2;
                    double traveled = 0;
                    @Override
                    public void run(){

                        Location orbCurrent = orb.getLocation();
                        double distance = orbCurrent.distance(end);
                        double distanceThisTick = Math.min(distance, 1);

                        Vector direction = end.toVector().subtract(orbCurrent.toVector());

                        if(distanceThisTick!=0){
                            orbCurrent.add(direction.normalize().multiply(distanceThisTick));
                            traveled = traveled + distanceThisTick;
                        }


                        if(traveled<half){
                            orbCurrent.add(0,distanceThisTick*2,0);
                        }

                        orb.teleport(orbCurrent);

                        caster.getWorld().spawnParticle(Particle.WAX_OFF,orbCurrent,1,0,0,0);

                        if(distance<=1){
                            this.cancel();
                            pickMeUp(orb);
                        }

                    }
                }.runTaskTimer(main, 0, 2);

            }

            private void pickMeUp(ArmorStand orb){

                new BukkitRunnable(){
                    int count = 0;
                    @Override
                    public void run(){

                        if(count%10==0){
                            double increment = (2 * Math.PI) / 16; // angle between particles

                            for (int i = 0; i < 16; i++) {
                                double angle = i * increment;
                                double x = orb.getLocation().getX() + (1 * Math.cos(angle));
                                double y = orb.getLocation().getY() + 2;
                                double z = orb.getLocation().getZ() + (1 * Math.sin(angle));
                                Location loc = new Location(current.getWorld(), x, y, z);
                                caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                            }
                        }

                        BoundingBox hitBox = new BoundingBox(
                                orb.getLocation().getX() - 2,
                                orb.getLocation().getY() - 2,
                                orb.getLocation().getZ() - 2,
                                orb.getLocation().getX() + 2,
                                orb.getLocation().getY() + 6,
                                orb.getLocation().getZ() + 2
                        );

                        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                            if(!(entity instanceof Player)){
                                continue;
                            }

                            Player thisPlayer = (Player) entity;

                            if(pvpManager.pvpLogic(caster, thisPlayer)){
                                continue;
                            }

                            if(buffAndDebuffManager.getWellCrit().getWellCrit(thisPlayer)>0){
                                continue;
                            }

                            buffAndDebuffManager.getWellCrit().applyBonus(thisPlayer);
                            orb.remove();
                            this.cancel();
                            break;
                        }

                        if(count>=20*10){
                            this.cancel();
                            orb.remove();
                        }

                        count++;
                    }
                }.runTaskTimer(main, 0, 1);

            }

            private void cancelTask(){
                well.remove();
                this.cancel();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 25 + ((int)(level/3));
    }

    public int getPlayerCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int getSkillCooldown(){
        return 30;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        return getPlayerCooldown(caster) <= 0;
    }

}
