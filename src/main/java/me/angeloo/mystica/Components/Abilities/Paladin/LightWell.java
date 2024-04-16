package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
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
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
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
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
    }

    public void use(Player player){

        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (getCooldown(player) > 0) {
            return;
        }

        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 30);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(player) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo(player);

            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(player.getUniqueId(), task);
    }

    private void execute(Player player){

        Location spawnStart = player.getLocation().clone();

        ArmorStand well = player.getWorld().spawn(spawnStart, ArmorStand.class);
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
        double finalSkillDamage = getSkillDamage(player);
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
                        player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }

                    BoundingBox hitBox = new BoundingBox(
                            current.getX() - 5,
                            current.getY() - 2,
                            current.getZ() - 5,
                            current.getX() + 5,
                            current.getY() + 6,
                            current.getZ() + 5
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
                        double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit));

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

                if(ran>=20*5){
                    cancelTask();
                }

                angle += 5;
                ran++;

            }

            private void spawnOrb(Location end){

                ArmorStand orb = player.getWorld().spawn(current, ArmorStand.class);
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

                        player.getWorld().spawnParticle(Particle.WAX_OFF,orbCurrent,1,0,0,0);

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
                                player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
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

                        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                            if(!(entity instanceof Player)){
                                continue;
                            }

                            Player thisPlayer = (Player) entity;

                            if(pvpManager.pvpLogic(player, thisPlayer)){
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

    public double getSkillDamage(Player player){
        double level = profileManager.getAnyProfile(player).getStats().getLevel();
        return 25 + ((int)(level/3));
    }

    public double getCost(){
        return 20;
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
