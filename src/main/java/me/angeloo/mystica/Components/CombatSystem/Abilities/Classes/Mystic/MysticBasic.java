package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityMarkManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.MysticAbilities;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.FakePlayerTargetManager;
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
import org.bukkit.util.Vector;

import java.util.*;

public class MysticBasic implements BasicAttackDefinition {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final AbilityMarkManager abilityMarkManager;

    //private final EvilSpirit evilSpirit;


    public MysticBasic(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        abilityManager = manager;
        abilityMarkManager = manager.getAbilityMarkManager();
        //evilSpirit = mysticAbilities.getEvilSpirit();
    }

    @Override
    public boolean performStage(LivingEntity caster, int stage) {
        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        //triggers animations on companions
        if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "basic");
        }

        basicStage(caster);

        return true;
    }

    @Override
    public int getMaxStages(LivingEntity caster) {
        return 1;
    }

    @Override
    public int getStageDelay(LivingEntity caster, int stage) {
        return 15;
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
        return baseRange + extraRange;
    }


    private void basicStage(LivingEntity caster){

        if(caster == null){
            return;
        }

        LivingEntity target;

        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Shepard);

        boolean healing = false;

        if(targetManager.getPlayerTarget(caster) == null || targetManager.getPlayerTarget(caster) == caster){
            target = caster;
            healing = true;
            //Bukkit.getLogger().info(caster.getName() + " target null");
        }
        else{
            target = targetManager.getPlayerTarget(caster);
        }

        if(target != caster){
            if(target.isDead()){

                if(caster instanceof Player){
                    targetManager.setPlayerTarget((Player)caster, null);
                }
                else{
                    fakePlayerTargetManager.setFakePlayerTarget(caster, null);
                }

                abilityManager.interruptBasic(caster);
                return;
            }

            if(target instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) target)){
                    healing = true;
                }
                else{
                    boolean targetDeathStatus = profileManager.getAnyProfile(target).getIfDead();

                    if(targetDeathStatus){
                        if(caster instanceof Player){
                            targetManager.setPlayerTarget((Player)caster, null);
                        }
                        else{
                            fakePlayerTargetManager.setFakePlayerTarget(caster, null);
                        }

                        return;
                    }
                }


            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    healing = true;
                }
            }
        }

        Location playerLocation = caster.getLocation();
        Location targetLocation = target.getLocation();

        double distance = playerLocation.distance(targetLocation);

        if(target != caster){
            if(distance<1){
                abilityManager.interruptBasic(caster);
                return;
            }
        }



        if(!healing){
            //Bukkit.getLogger().info(caster.getName() + " damaging");
            Location start = caster.getLocation();
            start.subtract(0, 1, 0);
            ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setCollidable(false);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);

            EntityEquipment entityEquipment = armorStand.getEquipment();

            ItemStack bolt = new ItemStack(Material.SPECTRAL_ARROW);
            ItemMeta meta = bolt.getItemMeta();
            assert meta != null;

            meta.setCustomModelData(1);

            bolt.setItemMeta(meta);
            assert entityEquipment != null;
            entityEquipment.setHelmet(bolt);


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
                    double distanceThisTick = Math.min(distance, .75);
                    current.add(direction.normalize().multiply(distanceThisTick));
                    current.setDirection(direction);

                    armorStand.teleport(current);

                    if (distance <= 1) {

                        cancelTask();

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);
                        double damage = damageCalculator.calculateDamage(caster, target, DamageType.Magical, finalSkillDamage, crit, 0);

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
        else{


            double healPower = 3;
            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double healAmount  = damageCalculator.calculateHealing(caster, healPower, crit);

            changeResourceHandler.addHealthToEntity(target, healAmount, caster);
            //Bukkit.getLogger().info("adding " + healAmount + " to " + target);

            if(shepard){
                abilityMarkManager.apply(caster, target);
            }


            Location center = target.getLocation().clone().add(0,1,0);

            double increment = (2 * Math.PI) / 16; // angle between particles

            for (int i = 0; i < 16; i++) {
                double angle = i * increment;
                double x = center.getX() + (1 * Math.cos(angle));
                double z = center.getZ() + (1 * Math.sin(angle));
                Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                target.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
            }



        }
    }




    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();

       return 5 + ((int)(skillLevel/3));
    }

    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target == null){
            target = caster;
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

        if(target==caster){
            return true;
        }

        double distance = caster.getLocation().distance(target.getLocation());

        if(distance > getRange(caster)){
            return false;
        }

        return !(distance < 1);
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3dc";
    }
}
