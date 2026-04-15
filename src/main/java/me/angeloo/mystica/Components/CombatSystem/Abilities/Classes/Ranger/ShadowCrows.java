package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.RangerAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Haste;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.ShadowCrowsDebuff;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.BossManager;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShadowCrows {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final BossManager bossManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;
    private final Focus focus;
    private final StarVolley starVolley;


    public ShadowCrows(Mystica main, AbilityManager manager, RangerAbilities rangerAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        bossManager = main.getBossManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
        starVolley = rangerAbilities.getStarVolley();
        focus = rangerAbilities.getFocus();
    }

    private final int abilityNumber = 2;
    private final int baseCooldown = 10;
    private final double range = 20;
    private final int baseDamage = 2;

    public void use(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), abilityNumber, (long) (baseCooldown * 1000));

    }

    private void execute(LivingEntity caster){

        boolean scout = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Scout);
        boolean tamer = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Tamer);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack crow = new ItemStack(Material.ARROW);
        ItemMeta meta = crow.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(2);
        crow.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(crow);



        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        armorStand.remove();
                        return;
                    }
                }


                if(!targetStillValid(target)){
                    this.cancel();
                    armorStand.remove();
                    return;
                }

                Location targetLoc = target.getLocation();
                targetLoc = targetLoc.subtract(0,1,0);

                Location current = armorStand.getLocation();
                Vector direction = targetLoc.toVector().subtract(current.toVector());

                double distance = current.distance(targetLoc);
                double distanceThisTick = Math.min(distance, 2);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                armorStand.teleport(current);

                if (distance <= 3) {
                    this.cancel();
                    crowTask();

                    if(tamer){
                        statusEffectManager.applyEffect(target, new ShadowCrowsDebuff(), null, null);
                    }

                }
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                    if(profileManager.getAnyProfile(target).getIfDead()){
                        return false;
                    }

                }

                if(bossManager.getIfResetProcessing(target)){
                    return false;
                }

                return !target.isDead();
            }


            private void crowTask(){
                new BukkitRunnable(){
                    Location targetWasLoc = target.getLocation().clone();
                    Vector initialDirection;
                    int count = 0;
                    int angle = 0;
                    @Override
                    public void run(){

                        if(targetStillValid(target)){
                            Location targetLoc = target.getLocation();
                            targetWasLoc = targetLoc.clone();
                        }

                        if (initialDirection == null) {
                            initialDirection = targetWasLoc.getDirection().setY(0).normalize();
                        }

                        Vector direction = initialDirection.clone();
                        double radians = Math.toRadians(angle);
                        direction.rotateAroundY(radians);
                        targetWasLoc.setDirection(direction);
                        armorStand.teleport(targetWasLoc);


                        if(!targetStillValid(target)){
                            this.cancel();
                            armorStand.remove();
                            return;
                        }

                        if(count%20 == 0){

                            boolean crit = damageCalculator.checkIfCrit(caster, subclassCritBonus(caster));

                            if(scout && crit){
                                starVolley.decreaseCooldown(caster);
                                statusEffectManager.applyEffect(caster, new Haste(), 2*20, 1.0);
                            }

                            double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                            changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                            if(target instanceof Player){
                                statusEffectManager.removeEffect(target, "shield");
                            }

                        }

                        if(count >= 20 * 10){
                            this.cancel();
                            armorStand.remove();
                        }

                        angle -= 10; // adjust the rotation speed here
                        if (angle <= -360) {
                            angle = 0;
                        }

                        count++;

                    }
                }.runTaskTimer(main, 0, 1);
            }

        }.runTaskTimer(main, 0, 1);

    }

    private int subclassCritBonus(LivingEntity caster){
        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equals(SubClass.Tamer)){
            return 15;
        }

        return 0;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();
        return focus.calculateFocusMultipliedDamage(caster, baseDamage) + ((int)(skillLevel/3));
    }



    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) target)){
                    return false;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return false;
                }
            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return false;
            }

            if(distance<1){
                return false;
            }

        }

        if(target == null){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), abilityNumber, statusEffectManager.getHastePercent(caster));
    }

}
