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

public class ShadowCrows {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;
    private final StarVolley starVolley;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ShadowCrows(Mystica main, AbilityManager manager, RangerAbilities rangerAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        starVolley = rangerAbilities.getStarVolley();
    }

    private final double range = 20;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        targetManager.setTargetToNearestValid(caster, range + buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        changeResourceHandler.subTractManaFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 2);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 2);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        boolean scout = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("scout");
        boolean tamer = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("animal tamer");

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
                        buffAndDebuffManager.getShadowCrowsDebuff().applyDebuff(target, 15 * 20);
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

                if(profileManager.getIfResetProcessing(target)){
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
                                buffAndDebuffManager.getHaste().applyHaste(caster, 1, 2*20);
                            }

                            double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                            changeResourceHandler.subtractHealthFromEntity(target, damage, caster);

                            if(target instanceof Player){
                                buffAndDebuffManager.getGenericShield().removeShields(target);
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
        String subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("animal tamer")){
            return 15;
        }

        return 0;
    }

    public double getCost(){return 5;}

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();
        return 2 + ((int)(skillLevel/3));
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

            if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        if(getCooldown(caster) > 0){
            return false;
        }


        if(profileManager.getAnyProfile(caster).getCurrentMana()<getCost()){
            return false;
        }

        return true;
    }

}
