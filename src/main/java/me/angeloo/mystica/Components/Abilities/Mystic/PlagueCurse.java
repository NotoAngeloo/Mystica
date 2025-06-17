package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
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

public class PlagueCurse {

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

    private final EvilSpirit evilSpirit;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, Boolean> curseMap = new HashMap<>();

    public PlagueCurse(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
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

        evilSpirit = mysticAbilities.getEvilSpirit();
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


        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 6);
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

        evilSpirit.addChaosShard(caster, 1);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location spawnLoc = target.getLocation().subtract(0,1.5,0);

        ArmorStand armorStand = caster.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack curseItem = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = curseItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(6);
        curseItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(curseItem);

        double skillDamage = getSkillDamage(caster);

        skillDamage = skillDamage / 10;

        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone().subtract(0,.75,0);
            int ran = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation().clone().subtract(0,.75,0);
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

                if(ran%20 == 0){
                    BoundingBox hitBox = new BoundingBox(
                            targetWasLoc.getX() - 4,
                            targetWasLoc.getY() - 2,
                            targetWasLoc.getZ() - 4,
                            targetWasLoc.getX() + 4,
                            targetWasLoc.getY() + 4,
                            targetWasLoc.getZ() + 4
                    );

                    for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == caster){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        LivingEntity livingEntity = (LivingEntity) entity;

                        if(!getIfCursed(livingEntity)){
                            applyCurse(livingEntity);
                        }

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);
                        double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Magical", finalSkillDamage, crit));

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

                angle += 2; // adjust the rotation speed here
                if (angle >= 360) {
                    angle = 0;
                }

                ran++;

                if(ran >= 10 * 20){
                    cancelTask();
                }
            }

            private void applyCurse(LivingEntity entity){
                curseMap.put(entity.getUniqueId(), true);

                new BukkitRunnable(){
                    int count = 0;
                    @Override
                    public void run(){

                        if(!targetStillValid(entity)){
                            this.cancel();
                            curseMap.remove(entity.getUniqueId());
                            return;
                        }

                        Location center = entity.getLocation().clone().add(0,1,0);

                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double j = center.getX() + (1 * Math.cos(angle));
                            double k = center.getZ() + (1 * Math.sin(angle));
                            Location loc = new Location(center.getWorld(), j, (center.getY()), k);

                            entity.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, loc, 1, 0, 0, 0, 0);
                        }

                        if(count >= 10){
                            this.cancel();
                            curseMap.remove(entity.getUniqueId());
                        }

                        count++;

                    }
                }.runTaskTimer(main, 0, 20);
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_2_Level_Bonus();

        return 30 + ((int)(skillLevel/3));
    }

    public boolean getIfCursed(LivingEntity entity){
        return curseMap.getOrDefault(entity.getUniqueId(), false);
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

        return getCooldown(caster) <= 0;
    }

}
