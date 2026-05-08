package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc.SpeedUp;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
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
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class DivineInfusion extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    private final Purity purity;

    public DivineInfusion(Mystica main, AbilityManager manager){
        super("divine_infusion");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
        purity = manager.getPurity();
    }

    private final int baseCooldown = 18;
    private final double range = 10;
    private final int baseDamage = 15;

    @Override
    public boolean use(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        if(target == null){
            target = caster;
        }

        execute(caster, target);

        cooldownManager.start(caster.getUniqueId(), 4, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        Location start = target.getLocation().clone().add(0,4,0);

        Location end = target.getLocation().clone().subtract(0,.25,0);

        ArmorStand armorStand = caster.getWorld().spawn(start.clone(), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(12);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);

        Set<LivingEntity> hitBySkill = new HashSet<>();

        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            int count = 0;
            boolean down = true;
            @Override
            public void run(){

                if(down){
                    Location current = armorStand.getLocation();
                    Vector direction = end.toVector().subtract(current.toVector());;
                    double distance = current.distance(end);
                    double distanceThisTick = Math.min(distance, .75);

                    if(distanceThisTick!=0){
                        current.add(direction.normalize().multiply(distanceThisTick));
                    }

                    armorStand.teleport(current);

                    if (distance <= 1) {
                        down = false;
                    }

                }

                if(!down){

                    if(count%20==0){
                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double x = end.getX() + (4 * Math.cos(angle));
                            double y = end.getY() + 1;
                            double z = end.getZ() + (4 * Math.sin(angle));
                            Location loc = new Location(end.getWorld(), x, y, z);
                            caster.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                        }

                        Set<LivingEntity> hitByThisTick = new HashSet<>();

                        BoundingBox hitBox = new BoundingBox(
                                end.getX() - 4,
                                end.getY() - 2,
                                end.getZ() - 4,
                                end.getX() + 4,
                                end.getY() + 6,
                                end.getZ() + 4
                        );

                        for(Entity entity : caster.getWorld().getNearbyEntities(hitBox)){

                            if(!(entity instanceof LivingEntity livingEntity)){
                                continue;
                            }

                            if(entity instanceof ArmorStand){
                                continue;
                            }

                            boolean crit = damageCalculator.checkIfCrit(caster, 0);
                            double damage = (damageCalculator.calculateDamage(caster, livingEntity, DamageType.Physical, finalSkillDamage, crit, 0));

                            if(livingEntity instanceof Player thisPlayer){

                                if(pvpManager.pvpLogic(caster, (Player) entity)){
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                }
                                else{
                                    hitByThisTick.add(thisPlayer);
                                }
                            }

                            if(!(livingEntity instanceof Player)){
                                if(pveChecker.pveLogic(livingEntity)){
                                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                }
                                else{
                                    hitByThisTick.add(livingEntity);
                                }
                            }


                        }

                        for(LivingEntity thisEntity : hitByThisTick){

                            if(hitBySkill.contains(thisEntity)){
                                continue;
                            }

                            hitBySkill.add(thisEntity);

                            double amount = (profileManager.getAnyProfile(thisEntity).getTotalHealth() + statusEffectManager.getHealthBuffAmount(thisEntity)) * .05;

                            if(thisEntity instanceof Player){

                                statusEffectManager.applyEffect(thisEntity, new SpeedUp(), null, 0.5, caster);

                            }

                            statusEffectManager.applyEffect(thisEntity, new GenericShield(), null, amount, caster);

                            removeBuffsLater(thisEntity, amount);
                        }

                    }


                    count++;
                }

                if(count>=20*6){
                    cancelTask();
                }
            }

            private void removeBuffsLater(LivingEntity thisEntity, double shield){
                new BukkitRunnable(){
                    @Override
                    public void run(){

                        if(thisEntity instanceof Player){
                            statusEffectManager.removeEffect(thisEntity, "speed_up");
                        }

                        statusEffectManager.reduceShield(thisEntity, shield);
                    }
                }.runTaskLater(main, 20*3);
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

        }.runTaskTimer(main,0,1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();

        double damage = baseDamage + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }

    @Override
    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return false;
            }

        }


        return cooldownManager.isReady(caster.getUniqueId(), 4, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3e3";
    }
}
