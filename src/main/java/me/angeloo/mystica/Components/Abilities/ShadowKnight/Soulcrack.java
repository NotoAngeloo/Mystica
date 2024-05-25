package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Soulcrack {

    private final Mystica main;
    private final AbilityManager abilityManager;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Soulcrack(Mystica main, AbilityManager manager){
        this.main = main;
        abilityManager = manager;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(getCooldown(player) > 0){
            return;
        }


        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 25);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 8);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private void execute(Player player){

        int castTime = 45;

        Location start = player.getLocation();
        ArmorStand armorStand = player.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        armorStand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(90), Math.toRadians(40)));
        armorStand.setLeftArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(-90), Math.toRadians(-40)));

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack weapon = profileManager.getAnyProfile(player).getPlayerEquipment().getWeapon();
        ItemStack offhand = profileManager.getAnyProfile(player).getPlayerEquipment().getOffhand();

        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(weapon);
        entityEquipment.setItemInOffHand(offhand);

        player.getInventory().setItemInMainHand(null);
        player.getInventory().setItemInOffHand(null);
        armorStand.teleport(start);

        double skillDamage =getSkillDamage(player);

        skillDamage = skillDamage * .25;

        double manaRestoration = getEnergyRestored();

        manaRestoration = manaRestoration/ (double) castTime;

        abilityManager.setCasting(player, true);
        double finalSkillDamage = skillDamage;
        double finalManaRestoration = manaRestoration;
        new BukkitRunnable(){
            int ran = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = player.getLocation().getDirection().setY(0).normalize();
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                Location playerLoc = player.getLocation().clone().add(0,0.5,0);
                playerLoc.setDirection(direction);

                armorStand.teleport(playerLoc);

                if(ran%10==0){
                    damageNear();
                }

                changeResourceHandler.addManaToEntity(player, finalManaRestoration);

                double percent = ((double) ran / castTime) * 100;

                abilityManager.setCastBar(player, percent);

                if(ran >= castTime){
                    cancelTask();
                }

                ran++;
                angle += 45;
                if (angle >= 360) {
                    angle = 0;
                }
            }

            private void damageNear(){

                BoundingBox hitBox = new BoundingBox(
                        player.getLocation().getX() - 5,
                        player.getLocation().getY() - 2,
                        player.getLocation().getZ() - 5,
                        player.getLocation().getX() + 5,
                        player.getLocation().getY() + 4,
                        player.getLocation().getZ() + 5
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
                    double damage = damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit);

                    //pvp logic
                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(player, (Player) entity)){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);

                            if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                                Vector awayDirection = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                                Vector velocity = awayDirection.multiply(.75).add(new Vector(0, .5, 0));
                                livingEntity.setVelocity(velocity);
                                buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                            }

                        }
                        continue;
                    }

                    if(pveChecker.pveLogic(livingEntity)){
                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);

                        if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                            Vector awayDirection = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                            Vector velocity = awayDirection.multiply(.75).add(new Vector(0, .5, 0));
                            livingEntity.setVelocity(velocity);
                            buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                        }

                    }

                }
            }

            private void cancelTask() {
                this.cancel();
                abilityManager.setCasting(player, false);
                abilityManager.setCastBar(player, 0);
                armorStand.remove();
                buffAndDebuffManager.getHidden().showWeapons(player);
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_8_Level_Bonus();
        return 50 + ((int)(skillLevel/3));
    }

    public double getEnergyRestored(){
        return 50;
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
