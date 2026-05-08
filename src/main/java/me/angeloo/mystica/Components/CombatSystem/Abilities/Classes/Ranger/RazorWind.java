package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Haste;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
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
import org.bukkit.util.Vector;

public class RazorWind extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;
    private final Focus focus;


    public RazorWind(Mystica main, AbilityManager manager){
        super("razor_wind");
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
        focus = manager.getFocus();
    }

    private final int baseCooldown = 16;
    private final int baseRange = 20;
    private final int baseDamage = 40;

    @Override
    public boolean use(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, getRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 4, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private double getRange(LivingEntity caster){
        double extraRange = statusEffectManager.getAdditionalRange(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster){

        boolean scout = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Scout);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        double skillDamage = getSkillDamage(caster);

        double castTime = 20;

        castTime = castTime - statusEffectManager.getHastePercent(caster);

        if(caster instanceof Player){
            ((Player)caster).setWalkSpeed(0.05f);
        }

        abilityManager.setSkillCurrentlyCasting(caster, statusBarIcon());

        double crit_bonus = 0;

        if(scout){
            crit_bonus = 1.2;
        }

        double finalCastTime = castTime;
        double finalCrit_bonus = crit_bonus;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.stopCasting(caster);
                        ((Player)caster).setWalkSpeed(.3f);
                        return;
                    }
                }

                if(!statusEffectManager.canCast(caster)){
                    this.cancel();
                    abilityManager.stopCasting(caster);

                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.3f);
                    }

                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                double distanceToTarget = caster.getLocation().distance(targetWasLoc);

                if(distanceToTarget>getRange(caster)){
                    this.cancel();
                    abilityManager.stopCasting(caster);
                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.3f);
                    }
                    return;
                }

                double percent = ((double) count / finalCastTime) * 100;

                abilityManager.setCastBar(caster, percent);

                if(count >= finalCastTime){
                    this.cancel();
                    abilityManager.stopCasting(caster);
                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.3f);
                    }
                    startLaunchTask();
                }

                count ++;
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private void startLaunchTask(){

                Location start = caster.getLocation();

                start.subtract(0, 1, 0);
                ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setMarker(true);

                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack razor = new ItemStack(Material.ARROW);
                ItemMeta meta = razor.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(5);
                razor.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(razor);

                new BukkitRunnable(){
                    boolean toFrom = false;
                    Location newTargetWasLoc = targetWasLoc.clone();
                    Location playerWasLoc = caster.getLocation().clone();
                    Vector initialDirection;
                    int angle = 0;
                    @Override
                    public void run(){

                        if(targetStillValid(target)){
                            Location targetLoc = target.getLocation();
                            targetLoc = targetLoc.subtract(0,1,0);
                            newTargetWasLoc = targetLoc.clone();
                        }

                        if(targetStillValid(caster)){
                            Location playerLoc = caster.getLocation();
                            playerLoc = playerLoc.subtract(0,1,0);
                            playerWasLoc = playerLoc.clone();
                        }

                        Location current = armorStand.getLocation();

                        if (!sameWorld(current, targetWasLoc)) {
                            cancelTask();
                            return;
                        }

                        Vector direction;
                        double distance;

                        if(!toFrom){
                            direction = targetWasLoc.toVector().subtract(current.toVector());
                            distance = current.distance(targetWasLoc);
                        }
                        else{
                            direction = playerWasLoc.toVector().subtract(current.toVector());
                            distance = current.distance(playerWasLoc);
                        }

                        double distanceThisTick = Math.min(distance, .6);

                        if(distanceThisTick!=0){
                            current.add(direction.normalize().multiply(distanceThisTick));
                        }

                        if (initialDirection == null) {
                            initialDirection = playerWasLoc.getDirection().setY(0).normalize();
                        }

                        Vector rotation = initialDirection.clone();
                        double radians = Math.toRadians(angle);
                        rotation.rotateAroundY(radians);
                        current.setDirection(rotation);

                        armorStand.teleport(current);

                        if(toFrom){
                            if (distance <= 1) {
                                cancelTask();
                            }
                        }

                        if(!toFrom){
                            if (distance <= 1) {

                                toFrom = true;

                                boolean crit = damageCalculator.checkIfCrit(caster, subclassCritBonus(caster));

                                if(scout && crit){
                                    lookup.get(PlayerClass.Ranger,SubClass.Scout,-1).onExternalTrigger(caster);
                                    statusEffectManager.applyEffect(caster, new Haste(), 2*20, 0.1, caster);
                                }

                                double damage = damageCalculator.calculateDamage(caster, target, DamageType.Physical, skillDamage, crit, finalCrit_bonus);

                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                                changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                            }
                        }

                        angle += 60; // adjust the rotation speed here
                        if (angle >= 360) {
                            angle = 0;
                        }

                    }

                    private boolean sameWorld(Location loc1, Location loc2) {
                        return loc1.getWorld().equals(loc2.getWorld());
                    }

                    private void cancelTask() {
                        this.cancel();
                        armorStand.remove();
                    }

                }.runTaskTimer(main, 0, 1);
            }

        }.runTaskTimer(main, 0, 2);

    }

    private int subclassCritBonus(LivingEntity caster){
        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equals(SubClass.Scout)){
            return 15;
        }

        return 0;
    }



    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();
        return focus.calculateFocusMultipliedDamage(caster, baseDamage) + ((int)(skillLevel/3));
    }


    @Override
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

            if(distance > getRange(caster)){
                return false;
            }

            if(distance<1){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 4, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3f8";
    }
}
