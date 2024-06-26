package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class Aurora {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PveChecker pveChecker;
    private final PvpManager pvpManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Aurora(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pveChecker = main.getPveChecker();
        pvpManager = main.getPvpManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    private final double range = 20;

    public void use(LivingEntity caster){
        if (!abilityReadyInMap.containsKey(caster.getUniqueId())) {
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target == null){
            target = caster;
        }

        changeResourceHandler.subTractManaFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster, target);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 35);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(caster) <= 0) {
                    cooldownDisplayer.displayCooldown(caster, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                cooldownDisplayer.displayCooldown(caster, 6);

            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void  execute(LivingEntity caster, LivingEntity target){

        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equalsIgnoreCase("shepard");

        Location center = target.getLocation().clone();

        double healPercent = 10;
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();
        healPercent = healPercent +  ((int)(skillLevel/3));

        double shieldAmount = (profileManager.getAnyProfile(caster).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(caster) + skillLevel) * .5;

        double finalHealPercent = healPercent;
        new BukkitRunnable(){
            final Set<LivingEntity> hitBySkill = new HashSet<>();
            Vector initialDirection;
            int angle = 0;
            int ran = 0;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = center.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);
                center.setDirection(rotation);

                double increment = (2 * Math.PI) / 16;

                for(int i = 0; i<9;i+=2){

                    for (int j = 0; j < 16; j++) {
                        double angle = j * increment;
                        double x = center.getX() + rotation.getX() + (i * Math.cos(angle));
                        double z = center.getZ() + rotation.getZ() + (i * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                        target.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0, 0, 0, 0);
                    }
                }


                if(ran%10==0){
                    BoundingBox hitBox = new BoundingBox(
                            center.getX() - 8,
                            center.getY() - 2,
                            center.getZ() - 8,
                            center.getX() + 8,
                            center.getY() + 4,
                            center.getZ() + 8
                    );

                    for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                        if(!(entity instanceof LivingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        LivingEntity hitEntity = (LivingEntity) entity;

                        if(entity instanceof Player){
                            if (pvpManager.pvpLogic(caster, (Player) hitEntity)) {
                                continue;
                            }
                        }

                        if(!(entity instanceof Player)){
                            if(pveChecker.pveLogic(hitEntity)){
                                continue;
                            }
                        }

                        if(shepard){
                            boolean crit = damageCalculator.checkIfCrit(caster, 0);
                            double healAmount = damageCalculator.calculateHealing(caster, finalHealPercent, crit);
                            changeResourceHandler.addHealthToEntity(hitEntity, healAmount, caster);
                        }

                        if(hitBySkill.contains(hitEntity)){
                            continue;
                        }

                        hitBySkill.add(hitEntity);

                        buffAndDebuffManager.getGenericShield().applyOrAddShield(hitEntity, shieldAmount);

                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(hitEntity, shieldAmount);
                            }
                        }.runTaskLater(main, 200);
                    }
                }


                if(ran >= 200){
                    this.cancel();
                }

                angle += 10;
                ran++;

            }


        }.runTaskTimer(main, 0, 1);

    }

    public double getCost(){
        return 40;
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
                if(pvpManager.pvpLogic(caster, (Player) target)){
                    target = caster;
                }
            }

            if(pveChecker.pveLogic(target)){
                target = caster;
            }
        }

        if(target == null){
            target = caster;
        }

        double distance = caster.getLocation().distance(target.getLocation());

        if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
            return false;
        }

        if (getCooldown(caster) > 0) {
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return false;
        }


        if(profileManager.getAnyProfile(caster).getCurrentMana()<getCost()){
            return false;
        }

        return true;
    }

}
