package me.angeloo.mystica.Components.CombatSystem.Abilities.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PaladinAbilities;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc.SpeedUp;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class DivineInfusion {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Purity purity;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DivineInfusion(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        purity = paladinAbilities.getPurity();
    }

    private final double range = 10;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target == null){
            target = caster;
        }

        execute(caster, target);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 18);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 4);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - statusEffectManager.getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 4);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

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
                            double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage, crit));

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

                                statusEffectManager.applyEffect(thisEntity, new SpeedUp(), null, 0.5);

                            }

                            statusEffectManager.applyEffect(thisEntity, new GenericShield(), null, amount);

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

        double damage = 15 + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }


    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return false;
            }

            if(target instanceof Player){
                if(profileManager.getAnyProfile(target).getIfDead()){
                    target = caster;
                }
            }

        }


        return getCooldown(caster) <= 0;
    }

}
