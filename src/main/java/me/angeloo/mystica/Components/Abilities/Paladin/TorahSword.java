package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TorahSword {

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

    private final Purity purity;
    private final Decision decision;
    private final Judgement judgement;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public TorahSword(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
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
        decision = paladinAbilities.getDecision();
        judgement = paladinAbilities.getJudgement();
        purity = paladinAbilities.getPurity();
    }

    private final double range = 10;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        targetManager.setTargetToNearestValid(caster, range + buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }


        combatManager.startCombatTimer(caster);

        execute(caster);

        if(profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Dawn)){
            purity.add(caster, 1);
        }


        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 1);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 1);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        boolean dawn = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Dawn);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

        Location start = target.getLocation().clone();
        start.add(0, 5, 0);

        ArmorStand sword1 = caster.getWorld().spawn(start, ArmorStand.class);
        sword1.setInvisible(true);
        sword1.setGravity(false);
        sword1.setCollidable(false);
        sword1.setInvulnerable(true);
        sword1.setMarker(true);

        EntityEquipment entityEquipment = sword1.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(3);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);

        sword1.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        Location start2 = start.clone().add(crossProduct.multiply(2));
        ArmorStand sword2 = caster.getWorld().spawn(start2, ArmorStand.class);
        sword2.setInvisible(true);
        sword2.setGravity(false);
        sword2.setCollidable(false);
        sword2.setInvulnerable(true);
        sword2.setMarker(true);

        EntityEquipment entityEquipment2 = sword2.getEquipment();
        assert entityEquipment2 != null;
        entityEquipment2.setItemInMainHand(item);

        sword2.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(-30)));

        Location start3 = start.clone().subtract(crossProduct.multiply(2));
        ArmorStand sword3 = caster.getWorld().spawn(start3, ArmorStand.class);
        sword3.setInvisible(true);
        sword3.setGravity(false);
        sword3.setCollidable(false);
        sword3.setInvulnerable(true);
        sword3.setMarker(true);

        EntityEquipment entityEquipment3 = sword3.getEquipment();
        assert entityEquipment3 != null;
        entityEquipment3.setItemInMainHand(item);

        sword3.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(30)));

        Location end = target.getLocation().clone().subtract(0,2,0);



        int critValue = 0;

        if(dawn){
            critValue = 15;
        }


        int finalCritValue = critValue;
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                Location current1 = sword1.getLocation();

                Vector direction1 = end.toVector().subtract(current1.toVector());
                double distance1 = current1.distance(end);
                double distanceThisTick1 = Math.min(distance1, 1);

                if(distanceThisTick1!=0){
                    current1.add(direction1.normalize().multiply(distanceThisTick1));
                }

                if(distance1 > 1){
                    sword1.teleport(current1);
                }

                Location current2 = sword2.getLocation();

                Vector direction2 = end.toVector().subtract(current2.toVector());
                double distance2 = current2.distance(end);
                double distanceThisTick2 = Math.min(distance2, 1);

                if(distanceThisTick2!=0){
                    current2.add(direction2.normalize().multiply(distanceThisTick2));
                }

                if(distance2 > 1 && count>=3){
                    sword2.teleport(current2);
                }

                Location current3 = sword3.getLocation();

                Vector direction3 = end.toVector().subtract(current3.toVector());
                double distance3 = current3.distance(end);
                double distanceThisTick3 = Math.min(distance3, 1);

                if(distanceThisTick3!=0){
                    current3.add(direction3.normalize().multiply(distanceThisTick3));
                }

                if(distance3 > 1 && count>=5){
                    sword3.teleport(current3);
                }


                if (distance1 <= 1) {

                    boolean crit = damageCalculator.checkIfCrit(caster, finalCritValue);

                    if(crit&&dawn){
                        judgement.resetCooldownDawn(caster);
                        decision.applyDecision(caster);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                    if(target instanceof Player){
                        buffAndDebuffManager.getGenericShield().removeShields(target);
                    }

                }

                if (distance2 <= 1) {

                    boolean crit = damageCalculator.checkIfCrit(caster, finalCritValue);

                    if(crit&&dawn){
                        judgement.resetCooldownDawn(caster);
                        decision.applyDecision(caster);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                    if(target instanceof Player){
                        buffAndDebuffManager.getGenericShield().removeShields(target);
                    }

                }

                if (distance3 <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, finalCritValue);

                    if(crit&&dawn){
                        judgement.resetCooldownDawn(caster);
                        decision.applyDecision(caster);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                    if(target instanceof Player){
                        buffAndDebuffManager.getGenericShield().removeShields(target);
                    }

                }

                if(count>100){
                    cancelTask();
                }

                count++;
            }

            private void cancelTask() {
                this.cancel();
                sword1.remove();
                sword2.remove();
                sword3.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_1_Level_Bonus();

        double damage = 7 + ((int)(skillLevel/3));

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


        return true;
    }

}
