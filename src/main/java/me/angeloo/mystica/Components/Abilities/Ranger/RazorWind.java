package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Components.Abilities.RangerAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
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

public class RazorWind {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;
    private final StarVolley starVolley;
    private final Focus focus;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public RazorWind(Mystica main, AbilityManager manager, RangerAbilities rangerAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        starVolley = rangerAbilities.getStarVolley();
        focus = rangerAbilities.getFocus();
    }

    public void use(LivingEntity caster){
        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        targetManager.setTargetToNearestValid(caster, getRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 16);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 4);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 4);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private double getRange(LivingEntity caster){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster){

        boolean scout = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("scout");

        LivingEntity target = targetManager.getPlayerTarget(caster);

        double skillDamage = getSkillDamage(caster);

        double castTime = 20;

        castTime = castTime - buffAndDebuffManager.getHaste().getHasteLevel(caster);

        abilityManager.setCasting(caster, true);

        if(caster instanceof Player){
            ((Player)caster).setWalkSpeed(.06f);
        }


        double finalCastTime = castTime;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        abilityManager.setCasting(caster, false);
                        ((Player)caster).setWalkSpeed(.2f);
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    this.cancel();
                    abilityManager.setCasting(caster, false);

                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.2f);
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
                    abilityManager.setCasting(caster, false);
                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.2f);
                    }
                    return;
                }

                double percent = ((double) count / finalCastTime) * 100;

                abilityManager.setCastBar(caster, percent);

                if(count >= finalCastTime){
                    this.cancel();
                    abilityManager.setCasting(caster, false);
                    if(caster instanceof Player){
                        ((Player)caster).setWalkSpeed(.2f);
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
                                    starVolley.decreaseCooldown(caster);
                                    buffAndDebuffManager.getHaste().applyHaste(caster, 1, 2*20);
                                }

                                double damage = damageCalculator.calculateDamage(caster, target, "Physical", skillDamage, crit);

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
        String subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("scout")){
            return 15;
        }

        return 0;
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
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_4_Level_Bonus();
        return focus.calculateFocusMultipliedDamage(caster, 40) + ((int)(skillLevel/3));
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
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

        if(getCooldown(caster) > 0){
            return false;
        }

        return true;
    }

}
