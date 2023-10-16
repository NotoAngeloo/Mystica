package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.Components.Abilities.ElementalistAbilities;
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

public class DragonBreathing {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final FieryWing fieryWing;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DragonBreathing(Mystica main, AbilityManager manager, ElementalistAbilities elementalistAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();

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

        combatManager.startCombatTimer(player);


        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 16);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location spawnStart = player.getLocation().clone();
        spawnStart.subtract(0, 1.5, 0);

        ArmorStand spawnTexture = spawnStart.getWorld().spawn(spawnStart, ArmorStand.class);
        spawnTexture.setInvisible(true);
        spawnTexture.setGravity(false);
        spawnTexture.setCollidable(false);
        spawnTexture.setInvulnerable(true);
        spawnTexture.setMarker(true);

        EntityEquipment entityEquipment2 = spawnTexture.getEquipment();

        ItemStack spawnItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta2 = spawnItem.getItemMeta();
        assert meta2 != null;
        meta2.setCustomModelData(11);
        spawnItem.setItemMeta(meta2);
        assert entityEquipment2 != null;
        entityEquipment2.setHelmet(spawnItem);

        new BukkitRunnable(){
            @Override
            public void run(){
                spawnTexture.remove();
            }
        }.runTaskLater(main, 20*3);

        Location start = player.getLocation().clone();
        Location end = target.getLocation();
        Vector direction = end.toVector().subtract(start.toVector());
        Location spawnLoc = start.clone().subtract(direction.clone().normalize().multiply(5));
        spawnLoc.add(0, 5, 0);


        ArmorStand armorStand = spawnLoc.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack dragonItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = dragonItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(10);
        dragonItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(dragonItem);

        double skillDamage = 10;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level_Bonus();

        new BukkitRunnable(){
            final Location end = target.getLocation().add(0, 5, 0);
            final Set<LivingEntity> hitBySkill = new HashSet<>();
            boolean inflamed = false;
            @Override
            public void run(){

                Location current = armorStand.getLocation();

                Vector direction = end.toVector().subtract(current.toVector());
                double distance = current.distance(end);
                double distanceThisTick = Math.min(distance, .5);
                current.add(direction.normalize().multiply(distanceThisTick));

                armorStand.teleport(current);

                Location loc = armorStand.getLocation();

                Vector dir = new Vector(0, -1, 0).multiply(Math.tan(Math.toRadians(45)));
                dir = dir.add(direction.normalize());

                double xOffset = 5 * direction.getX() / direction.length();
                double zOffset = 5 * direction.getZ() / direction.length();
                loc.add(xOffset, 0, zOffset);

                for (int i = 0; i < 5; i++) {
                    loc.add(dir);

                    if(i==4){
                        double increment = (2 * Math.PI) / 16;
                        int radius = 2;

                        for (int j = 0; j < 16; j++) {
                            double angle = j * increment;
                            double x = loc.getX() + (radius * Math.cos(angle));
                            double z = loc.getZ() + (radius * Math.sin(angle));
                            Location jloc = new Location(loc.getWorld(), x, loc.getY(), z);

                            loc.getWorld().spawnParticle(Particle.LAVA, jloc, 1, 0, 0, 0, 0);
                        }

                        BoundingBox hitBox = new BoundingBox(
                                loc.getX() - radius,
                                loc.getY() - 2,
                                loc.getZ() - radius,
                                loc.getX() + radius,
                                loc.getY() + 4,
                                loc.getZ() + radius
                        );

                        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                            if(entity == player){
                                continue;
                            }

                            if(entity == armorStand){
                                continue;
                            }

                            if(!(entity instanceof LivingEntity)){
                                continue;
                            }

                            LivingEntity livingEntity = (LivingEntity) entity;

                            if(hitBySkill.contains(livingEntity)){
                                continue;
                            }

                            hitBySkill.add(livingEntity);

                            if(!inflamed){
                                fieryWing.addInflame(player);
                                inflamed = true;
                            }

                            boolean crit = damageCalculator.checkIfCrit(player, 0);
                            double damage = (damageCalculator.calculateDamage(player, livingEntity, "Magical", skillDamage * skillLevel, crit));

                            //pvp logic
                            if(entity instanceof Player){
                                if(pvpManager.pvpLogic(player, (Player) entity)){
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                                    burnTask(livingEntity);
                                }
                                continue;
                            }

                            if(pveChecker.pveLogic(livingEntity)){
                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                                burnTask(livingEntity);
                            }
                        }
                    }
                }

                if(distance <=1){
                    cancelTask();
                }

            }

            private void burnTask(LivingEntity entity){
                new BukkitRunnable(){
                    int ticks = 0;
                    @Override
                    public void run(){

                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double tickDamage = damageCalculator.calculateDamage(player, entity, "Magical", (skillDamage * skillLevel)/3, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(entity, player));
                        changeResourceHandler.subtractHealthFromEntity(entity, tickDamage, player);

                        ticks ++;

                        if(ticks >=5){
                            this.cancel();
                        }
                    }
                }.runTaskTimer(main, 0, 20);
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
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
