package me.angeloo.mystica.Components.Abilities.Paladin;

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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class DivineGuidance {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DivineGuidance(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }


        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 12);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        Set<LivingEntity> hitBySkill = new HashSet<>();

        Location start = player.getLocation();

        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

        Location h1spawn = start.clone().add(direction.clone().multiply(4)).setDirection(crossProduct);

        ArmorStand hammer = player.getWorld().spawn(h1spawn.clone().subtract(0,5,0), ArmorStand.class);
        hammer.setInvisible(true);
        hammer.setGravity(false);
        hammer.setCollidable(false);
        hammer.setInvulnerable(true);
        hammer.setMarker(true);

        EntityEquipment entityEquipment = hammer.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(4);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);

        hammer.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        hammer.teleport(h1spawn);

        Location h2spawn = start.clone().subtract(direction.clone().multiply(4)).setDirection(crossProduct);

        ArmorStand hammer2 = player.getWorld().spawn(h2spawn.clone().subtract(0,5,0), ArmorStand.class);
        hammer2.setInvisible(true);
        hammer2.setGravity(false);
        hammer2.setCollidable(false);
        hammer2.setInvulnerable(true);
        hammer2.setMarker(true);

        EntityEquipment entityEquipment2 = hammer2.getEquipment();
        assert entityEquipment2 != null;
        entityEquipment2.setItemInMainHand(item);

        hammer2.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        hammer2.teleport(h2spawn);

        Location h3spawn = start.clone().add(crossProduct.clone().multiply(4)).setDirection(direction);

        ArmorStand hammer3 = player.getWorld().spawn(h3spawn.clone().subtract(0,5,0), ArmorStand.class);
        hammer3.setInvisible(true);
        hammer3.setGravity(false);
        hammer3.setCollidable(false);
        hammer3.setInvulnerable(true);
        hammer3.setMarker(true);

        EntityEquipment entityEquipment3 = hammer3.getEquipment();
        assert entityEquipment3 != null;
        entityEquipment3.setItemInMainHand(item);

        hammer3.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        hammer3.teleport(h3spawn);

        Location h4spawn = start.clone().subtract(crossProduct.clone().multiply(4)).setDirection(direction);

        ArmorStand hammer4 = player.getWorld().spawn(h4spawn.clone().subtract(0,5,0), ArmorStand.class);
        hammer4.setInvisible(true);
        hammer4.setGravity(false);
        hammer4.setCollidable(false);
        hammer4.setInvulnerable(true);
        hammer4.setMarker(true);

        EntityEquipment entityEquipment4 = hammer4.getEquipment();
        assert entityEquipment4 != null;
        entityEquipment4.setItemInMainHand(item);

        hammer4.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        hammer4.teleport(h4spawn);

        double skillDamage = 7;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level_Bonus();

        skillDamage = skillDamage + ((int)(skillLevel/10));

        BoundingBox hitBox = new BoundingBox(
                start.getX() - 4,
                start.getY() - 2,
                start.getZ() - 4,
                start.getX() + 4,
                start.getY() + 4,
                start.getZ() + 4
        );

        List<Player> validPlayers = new ArrayList<>();

        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

            if(!(entity instanceof Player)){
                continue;
            }

            Player hitPlayer = (Player) entity;

            if(pvpManager.pvpLogic(player, hitPlayer)){
                continue;
            }

            boolean deathStatus = profileManager.getAnyProfile(hitPlayer).getIfDead();

            if(deathStatus){
                continue;
            }

            validPlayers.add(hitPlayer);
        }

        validPlayers.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth()
                /(double)profileManager.getAnyProfile(p).getTotalHealth()));

        List<Player> affected = validPlayers.subList(0, Math.min(3, validPlayers.size()));

        for(Player thisPlayer : affected){
            double totalTargetHealth = profileManager.getAnyProfile(thisPlayer).getTotalHealth();
            double yourAttack = profileManager.getAnyProfile(player).getTotalAttack();
            boolean crit = damageCalculator.checkIfCrit(player, 0);

            double healAmount = totalTargetHealth * .05;
            healAmount = healAmount * (yourAttack/3);

            if(crit){
                healAmount = healAmount * 1.5;
            }

            changeResourceHandler.addHealthToEntity(thisPlayer, healAmount, player);

            Location center = thisPlayer.getLocation().clone().add(0,1,0);

            double increment = (2 * Math.PI) / 16; // angle between particles

            for (int i = 0; i < 16; i++) {
                double angle = i * increment;
                double x = center.getX() + (1 * Math.cos(angle));
                double z = center.getZ() + (1 * Math.sin(angle));
                Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
            }
        }

        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            Vector initialDirection;
            double angle = 0;
            double eulerAngle = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                    return;
                }

                if (initialDirection == null) {
                    initialDirection = player.getLocation().getDirection().setY(0).normalize();
                }

                Location center = player.getLocation();

                if(angle%100==0){
                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = center.getX() + (4 * Math.cos(angle));
                        double y = center.getY() + 1;
                        double z = center.getZ() + (4 * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), x, y, z);

                        player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }

                }

                BoundingBox hitBox = new BoundingBox(
                        center.getX() - 4,
                        center.getY() - 2,
                        center.getZ() - 4,
                        center.getX() + 4,
                        center.getY() + 4,
                        center.getZ() + 4
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

                    if(hitBySkill.contains(livingEntity)){
                        continue;
                    }

                    hitBySkill.add(livingEntity);

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit);

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

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location h1Loc = center.clone().add(direction.clone().multiply(4)).setDirection(crossProduct);
                hammer.teleport(h1Loc);
                hammer.setRightArmPose(new EulerAngle(Math.toRadians(eulerAngle), Math.toRadians(0), Math.toRadians(0)));

                Location h2Loc = center.clone().subtract(direction.clone().multiply(4)).setDirection(crossProduct);
                hammer2.teleport(h2Loc);
                hammer2.setRightArmPose(new EulerAngle(Math.toRadians(eulerAngle), Math.toRadians(0), Math.toRadians(0)));

                Location h3Loc = center.clone().add(crossProduct.clone().multiply(4)).setDirection(direction);
                hammer3.teleport(h3Loc);
                hammer3.setRightArmPose(new EulerAngle(Math.toRadians(eulerAngle), Math.toRadians(0), Math.toRadians(0)));

                Location h4Loc = center.clone().subtract(crossProduct.clone().multiply(4)).setDirection(direction);
                hammer4.teleport(h4Loc);
                hammer4.setRightArmPose(new EulerAngle(Math.toRadians(eulerAngle), Math.toRadians(0), Math.toRadians(0)));

                if(angle>360){
                    cancelTask();
                }

                angle+=10;
                eulerAngle+=5;
            }

            private void cancelTask(){
                this.cancel();
                hammer.remove();
                hammer2.remove();
                hammer3.remove();
                hammer4.remove();
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
