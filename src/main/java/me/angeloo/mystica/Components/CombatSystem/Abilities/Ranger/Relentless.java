package me.angeloo.mystica.Components.CombatSystem.Abilities.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.RangerAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.BuffAndDebuffManager;
import me.angeloo.mystica.Components.CombatSystem.CombatManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Enums.SubClass;
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

import java.util.*;

public class Relentless {

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

    public Relentless(Mystica main, AbilityManager manager, RangerAbilities rangerAbilities){
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
        focus = rangerAbilities.getFocus();
        starVolley = rangerAbilities.getStarVolley();
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

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 16);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 3);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 3);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private double getRange(LivingEntity caster){
        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster){

        boolean scout = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Scout);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        double castTime = 15;

        castTime = castTime - buffAndDebuffManager.getHaste().getHasteLevel(caster);

        double skillDamage = getSkillDamage(caster);

        abilityManager.setCasting(caster, true);

        if(caster instanceof Player){
            buffAndDebuffManager.getSpeedUp().applySpeedUp((Player) caster, .5f);
        }


        double finalSkillDamage = skillDamage / castTime;
        double finalCastTime = castTime;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            final Set<ArmorStand> allStands = new HashSet<>();
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    cancelTask();
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location start = caster.getLocation();
                start.subtract(0, 1, 0);

                double distanceToTarget = start.distance(targetWasLoc);

                if(distanceToTarget>getRange(caster)){
                    cancelTask();
                    return;
                }

                ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setMarker(true);

                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack arrow = new ItemStack(Material.ARROW);
                ItemMeta meta = arrow.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(1);
                arrow.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(arrow);

                allStands.add(armorStand);

                double randomValue = Math.random() * 2 - 1;

                new BukkitRunnable(){
                    final double initialDistance = armorStand.getLocation().distance(target.getLocation());
                    final double halfDistance = initialDistance/2;
                    double traveled = 0;
                    @Override
                    public void run(){

                        Location current = armorStand.getLocation();

                        Vector direction = targetWasLoc.toVector().subtract(current.toVector());

                        double distance = current.distance(targetWasLoc);
                        double distanceThisTick = Math.min(distance, .75);

                        if(distanceThisTick!=0){
                            current.add(direction.normalize().multiply(distanceThisTick));
                            traveled = traveled + distanceThisTick;
                        }


                        if(traveled < halfDistance){
                            current.add(direction.clone().crossProduct(new Vector(0,1,0).normalize().multiply(randomValue)));
                        }
                        else{
                            current.setDirection(direction);
                        }

                        armorStand.teleport(current);

                        if (distance <= 1) {

                            this.cancel();
                            armorStand.remove();


                            boolean crit = damageCalculator.checkIfCrit(caster, 0);

                            if(scout && crit){
                                starVolley.decreaseCooldown(caster);
                                buffAndDebuffManager.getHaste().applyHaste(caster, 1, 2*20);
                            }

                            double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                            changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                        }

                    }
                }.runTaskTimer(main, 0, 1);

                double percent = ((double) count /15) * 100;

                abilityManager.setCastBar(caster, percent);

                if(count >= finalCastTime){
                    cancelTask();
                }

                count++;
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private void cancelTask(){
                this.cancel();
                removeStands();
                abilityManager.setCasting(caster, false);
                abilityManager.setCastBar(caster, 0);

                if(caster instanceof Player){
                    buffAndDebuffManager.getSpeedUp().removeSpeedUp((Player) caster);
                }


            }

            private void removeStands(){
                for(ArmorStand stand : allStands){
                    stand.remove();
                }
            }

        }.runTaskTimer(main, 0, 4);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_3_Level_Bonus();
        return focus.calculateFocusMultipliedDamage(caster, 40) + ((int)(skillLevel/3));
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

        return getCooldown(caster) <= 0;
    }


}
