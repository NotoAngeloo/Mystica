package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.PaladinAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class TorahSword {

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
    private final Decision decision;
    private final Judgement judgement;


    public TorahSword(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
        decision = paladinAbilities.getDecision();
        judgement = paladinAbilities.getJudgement();
        purity = paladinAbilities.getPurity();
    }

    private final int abilityNumber = 1;
    private final int baseCooldown = 10;
    private final int baseDamage = 7;
    private final double range = 10;

    public void use(LivingEntity caster){


        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        execute(caster);

        if(profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Dawn)){
            purity.add(caster, abilityNumber);
        }


        cooldownManager.start(caster.getUniqueId(), abilityNumber, (long) (baseCooldown * 1000));

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
                        cooldownManager.clear(caster.getUniqueId(), 8);
                        decision.applyDecision(caster);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                    if(target instanceof Player){
                        statusEffectManager.removeEffect(target, "shield");
                    }

                }

                if (distance2 <= 1) {

                    boolean crit = damageCalculator.checkIfCrit(caster, finalCritValue);

                    if(crit&&dawn){
                        cooldownManager.clear(caster.getUniqueId(), 8);
                        decision.applyDecision(caster);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                    if(target instanceof Player){
                        statusEffectManager.removeEffect(target, "shield");
                    }

                }

                if (distance3 <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, finalCritValue);

                    if(crit&&dawn){
                        cooldownManager.clear(caster.getUniqueId(), 8);
                        decision.applyDecision(caster);
                    }

                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                    if(target instanceof Player){
                        statusEffectManager.removeEffect(target, "shield");
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

        double damage = baseDamage + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
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
        }

        if(target == null){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), abilityNumber, statusEffectManager.getHastePercent(caster));
    }

}
