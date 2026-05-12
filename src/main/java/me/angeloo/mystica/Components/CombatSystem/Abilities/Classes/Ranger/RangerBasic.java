package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityLookup;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.KnockUp;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
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

public class RangerBasic implements BasicAttackDefinition {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final Focus focus;

    public RangerBasic(Mystica main, AbilityManager manager, AbilityLookup lookup){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        focus = manager.getFocus();
    }

    @Override
    public boolean performStage(LivingEntity caster, int stage) {

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(target == null){
            double totalRange = getRange(caster);
            targetManager.setTargetToNearestValid(caster, totalRange);
            target = targetManager.getPlayerTarget(caster);
        }

        if(!usable(caster, target)){
            return false;
        }

        //triggers animations on companions
        if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "basic");
        }


        switch (stage) {
            case 1 -> {
                basicStage1(caster);
            }
            case 2 -> {
                basicStage1(caster);
            }
            case 3 ->{
                basicStage2(caster);
            }
        }

        return true;
    }

    @Override
    public int getMaxStages(LivingEntity caster) {
        return 3;
    }

    @Override
    public int getStageDelay(LivingEntity caster, int stage) {

        if(stage==3){
            return 20;
        }

        return 10;
    }

    @Override
    public boolean canStart(LivingEntity caster) {
        return statusEffectManager.canBasic(caster);
    }

    @Override
    public boolean canContinue(LivingEntity caster, int nextStage) {
        return statusEffectManager.canBasic(caster);
    }

    private double getRange(LivingEntity caster){
        double baseRange = 20;
        double extraRange = statusEffectManager.getAdditionalRange(caster);
        return  baseRange + extraRange;
    }


    private void basicStage1(LivingEntity caster){

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

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
        arrow.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(arrow);


        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation().clone().subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location current = armorStand.getLocation();

                if (!sameWorld(current, targetWasLoc)) {
                    cancelTask();
                    return;
                }

                Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                double distance = current.distance(targetWasLoc);
                double distanceThisTick = Math.min(distance, 1);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, DamageType.Physical, finalSkillDamage, crit, 0);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                }

            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
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

    private void basicStage2(LivingEntity caster){


        LivingEntity target = targetManager.getPlayerTarget(caster);

        boolean scout = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Scout);
        boolean active = statusEffectManager.hasEffect(caster, "rallying_cry");

        double crit_bonus = 0;

        if(scout && active){
            crit_bonus = 1.2;
        }

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);
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



        double finalSkillDamage = getSkillDamage(caster);
        double finalCrit_bonus = crit_bonus;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation().clone().subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location current = armorStand.getLocation();

                if (!sameWorld(current, targetWasLoc)) {
                    cancelTask();
                    return;
                }

                Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                double distance = current.distance(targetWasLoc);
                double distanceThisTick = Math.min(distance, 1);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, DamageType.Physical, finalSkillDamage, crit, finalCrit_bonus);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                    if(active) {
                        statusEffectManager.applyEffect(target, new KnockUp(), null, .5, caster);
                    }

                }

            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
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


    public double getSkillDamage(LivingEntity caster){
        double skillDamage = 10;
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();

        if(statusEffectManager.hasEffect(caster, "rallying_cry")) {
            skillDamage = skillDamage * 1.25;
        }

        return focus.calculateFocusMultipliedDamage(caster, skillDamage) + ((int)(skillLevel/3));
    }

    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target == null){
            return false;
        }

        if(profileManager.getAnyProfile(caster).getIfDead()){
            return false;
        }

        if(profileManager.getAnyProfile(target).getIfDead()){
            return false;
        }

        if(!statusEffectManager.canBasic(caster)){
            return false;
        }

        if (target instanceof Player) {
            if (!pvpManager.pvpLogic(caster, (Player) target)) {
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

        return !(distance < 1);
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3f7";
    }
}
