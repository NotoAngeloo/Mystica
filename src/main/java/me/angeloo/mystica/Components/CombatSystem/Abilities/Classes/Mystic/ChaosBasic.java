package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
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

import java.util.HashSet;
import java.util.Set;

public class ChaosBasic implements BasicAttackDefinition {

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

    public ChaosBasic(Mystica main, AbilityManager manager){
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

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();

        return 5 + ((int)(skillLevel/3));
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

    private void basicStage(LivingEntity caster){


        LivingEntity target = targetManager.getPlayerTarget(caster);

        //coming soon
        boolean evilSpirit = false;

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

        if(!evilSpirit){
            meta.setCustomModelData(2);
        }
        else{
            meta.setCustomModelData(3);
        }

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


                if(evilSpirit){
                    caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, current, 1, 0, 0, 0, 0);
                }

                if (distance <= 1) {

                    cancelTask();


                    if(evilSpirit){
                        aoeAttack();
                        return;
                    }

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, DamageType.Magical, finalSkillDamage, crit, 0);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                }

            }

            private void aoeAttack(){

                Set<LivingEntity> hitBySkill = new HashSet<>();

                BoundingBox hitBox = new BoundingBox(
                        targetWasLoc.getX() - 4,
                        targetWasLoc.getY() - 2,
                        targetWasLoc.getZ() - 4,
                        targetWasLoc.getX() + 4,
                        targetWasLoc.getY() + 4,
                        targetWasLoc.getZ() + 4
                );

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = targetWasLoc.getX() + (4 * Math.cos(angle));
                    double z = targetWasLoc.getZ() + (4 * Math.sin(angle));
                    Location loc = new Location(targetWasLoc.getWorld(), x, targetWasLoc.clone().add(0,1,0).getY(), z);

                    caster.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, loc, 1, 0, 0, 0, 0);
                }

                for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                    if(entity == caster){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity livingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    if(hitBySkill.contains(livingEntity)){
                        continue;
                    }

                    hitBySkill.add(livingEntity);

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = (damageCalculator.calculateDamage(caster, livingEntity, DamageType.Magical, getSkillDamage(caster), crit, 0));

                    //pvp logic
                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(caster, (Player) entity)){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                        }
                        continue;
                    }

                    if(pveChecker.pveLogic(livingEntity)){
                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
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


    public double getEvilSpiritDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 40 + ((int)(skillLevel/3));
    }

}
