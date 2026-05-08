package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.KnockUp;
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
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class AnvilDrop extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownManager cooldownManager;

    private final Rage rage;


    public AnvilDrop(Mystica main, AbilityManager manager){
        super("anvil_drop");
        this.main = main;
        targetManager = main.getTargetManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownManager = main.getCooldownManager();
        rage = manager.getRage();
    }

    private final int baseCooldown = 20;
    private final int baseDamage = 35;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 5, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        double baseRange = 15;

        targetManager.setTargetToNearestValid(caster, baseRange);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        boolean targeted = false;

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(caster, (Player) target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }

            if(!(target instanceof Player)){
                if(pveChecker.pveLogic(target)){

                    double distance = caster.getLocation().distance(target.getLocation());

                    if(distance < baseRange){
                        targeted = true;
                    }

                }
            }


        }

        if(targeted){
            direction = target.getLocation().toVector().subtract(caster.getLocation().toVector()).setY(0).normalize();
        }

        Location start = caster.getLocation().clone();
        //Location end = start.clone().add(direction.multiply(baseRange));
        Location end = start.clone();

        while (baseRange > 0) {
            end.add(direction);
            if (!end.getBlock().isPassable()) {
                end.subtract(direction.multiply(2));
                break;
            }
            baseRange -= 1;
        }

        if(targeted){
            end = target.getLocation().clone();
        }

        //if distance too short no leap
        double distance = start.distance(end);

        if(distance<5){
            knockUp(caster);
            return;
        }

        //abilityManager.setSkillRunning(player, true);
        Location finalEnd = end;
        new BukkitRunnable(){
            final double length = start.distance(finalEnd);
            final double half = length/2;
            double traveled = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        return;
                    }
                }

                if(caster.isDead()){
                    this.cancel();
                    return;
                }

                if(profileManager.getAnyProfile(caster).getIfDead()){
                    this.cancel();
                    //abilityManager.setSkillRunning(player, false);
                    return;
                }

                Location current = caster.getLocation();
                double distance = current.distance(finalEnd);
                double distanceThisTick = Math.min(distance, 1);

                Vector direction = finalEnd.toVector().subtract(current.toVector());

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                    traveled = traveled + distanceThisTick;
                }

                if(traveled<half){
                    current.add(0,distanceThisTick,0);
                }

                caster.teleport(current);

                if(distance<=1){
                    this.cancel();
                    knockUp(caster);
                    //abilityManager.setSkillRunning(player, false);
                }
            }
        }.runTaskTimer(main, 0, 1);
    }

    private void knockUp(LivingEntity caster){

        boolean executioner = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Executioner);

        double skillDamage = getSkillDamage(caster);

        if(executioner){
            skillDamage *= 2;
        }


        BoundingBox hitBox = new BoundingBox(
                caster.getLocation().getX() - 4,
                caster.getLocation().getY() - 2,
                caster.getLocation().getZ() - 4,
                caster.getLocation().getX() + 4,
                caster.getLocation().getY() + 4,
                caster.getLocation().getZ() + 4
        );

        double increment = (2 * Math.PI) / 16; // angle between particles

        for (int i = 0; i < 16; i++) {
            double angle = i * increment;
            double x = caster.getLocation().getX() + (4 * Math.cos(angle));
            double y = caster.getLocation().getY() + 1;
            double z = caster.getLocation().getZ() + (4 * Math.sin(angle));
            Location loc = new Location(caster.getWorld(), x, y, z);
            caster.getWorld().spawnParticle(Particle.CRIT, loc, 1,0, 0, 0, 0);
        }

        LivingEntity targetToHit = null;
        LivingEntity target = targetManager.getPlayerTarget(caster);
        LivingEntity firstHit = null;
        boolean targetHit = false;

        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(entity == caster){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity livingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) entity)){
                    continue;
                }
            }

            if(entity instanceof ArmorStand){
                continue;
            }

            if(!(entity instanceof Player)){
                if(!pveChecker.pveLogic(livingEntity)){
                    continue;
                }
            }

            if(firstHit == null){
                firstHit = livingEntity;
            }

            if(target != null){
                if(livingEntity == target){
                    targetHit = true;
                    targetToHit = livingEntity;
                    break;
                }
            }
        }

        if(!targetHit && firstHit!= null){
            targetToHit = firstHit;
        }

        if(targetToHit != null){
            if(caster instanceof Player){
                targetManager.setPlayerTarget((Player)caster, targetToHit);
            }
            else{
                fakePlayerTargetManager.setFakePlayerTarget(caster, targetToHit);
            }
            Location playerLoc = caster.getLocation().clone();
            Vector targetDir = targetToHit.getLocation().toVector().subtract(playerLoc.toVector());
            playerLoc.setDirection(targetDir);
            caster.teleport(playerLoc);

            int bonus = 0;
            if(executioner){
                bonus = 15;
            }

            boolean crit = damageCalculator.checkIfCrit(caster, bonus);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, DamageType.Physical, skillDamage, crit, 0);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster, crit);
            rage.addRageToEntity(caster, 10);

            //also knockup
            if(profileManager.getAnyProfile(targetToHit).getIsMovable()){
                Vector velocity = (new Vector(0, .75, 0));
                targetToHit.setVelocity(velocity);
                statusEffectManager.applyEffect(targetToHit, new KnockUp(), null, null, caster);
            }
        }
    }



    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();
        return baseDamage + ((int)(skillLevel/3));
    }


    @Override
    public boolean usable(LivingEntity caster){
        if(!cooldownManager.isReady(caster.getUniqueId(), 5, statusEffectManager.getHastePercent(caster))){
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        return block.getType() != Material.AIR;
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue410";
    }
}
