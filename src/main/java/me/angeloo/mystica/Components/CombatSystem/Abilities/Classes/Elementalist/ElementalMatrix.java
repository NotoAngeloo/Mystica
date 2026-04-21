package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.BossManager;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class ElementalMatrix extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final BossManager bossManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;


    public ElementalMatrix(Mystica main, AbilityManager manager){
        super("elemental_matrix");
        this.main = main;
        profileManager = main.getProfileManager();
        bossManager = main.getBossManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();

    }

    private final double range = 20;
    private final int baseCooldown = 10;
    private final int baseDamage = 10;

    @Override
    public void use(LivingEntity caster){


        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 8, (long) (baseCooldown * 1000));

    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        boolean conjurer = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Conjurer);

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(caster));

        for(LivingEntity member : mParty){

            if(member instanceof Player){
                if(!((Player)member).isOnline()){
                    continue;
                }

                boolean deathStatus = profileManager.getAnyProfile(member).getIfDead();

                if(deathStatus){
                    continue;
                }

                double maxHp = profileManager.getAnyProfile(member).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster);

                changeResourceHandler.addHealthToEntity(member, maxHp * .05, caster);
            }
        }

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location spawnLoc = target.getLocation().subtract(0,1.9,0);

        ArmorStand armorStand = caster.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack matrixItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = matrixItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(6);
        matrixItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(matrixItem);

        int ticks = 5;

        double skillDamage = getSkillDamage(caster);


        if(conjurer){

            double currentHealth = profileManager.getAnyProfile(caster).getCurrentHealth();

            double percent = (profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster)/currentHealth);

            skillDamage = skillDamage * (1 + percent);
        }




        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            int ran = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){


                Location targetLoc = target.getLocation();

                if (initialDirection == null) {
                    initialDirection = targetLoc.getDirection().setY(0).normalize();
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);


                if(!targetStillValid(target)){
                    cancelTask();
                    return;
                }

                targetLoc = targetLoc.subtract(0,1.5,0);

                targetLoc.setDirection(direction);

                armorStand.teleport(targetLoc);

                //happens 5 times
                if(ran%20 == 0){
                    //tick damage

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = (damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage / ticks, crit));
                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                }


                angle += 10; // adjust the rotation speed here
                if (angle >= 360) {
                    angle = 0;
                }

                ran++;

                if(ran >= 100){
                    cancelTask();

                    Set<LivingEntity> hitBySkill = new HashSet<>();

                    BoundingBox hitBox = new BoundingBox(
                            target.getLocation().getX() - 4,
                            target.getLocation().getY() - 2,
                            target.getLocation().getZ() - 4,
                            target.getLocation().getX() + 4,
                            target.getLocation().getY() + 4,
                            target.getLocation().getZ() + 4
                    );

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
                        double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Magical", finalSkillDamage, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                        }

                    }

                }
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                if(bossManager.getIfResetProcessing(target)){
                    return false;
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
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_8_Level_Bonus();
        return baseDamage + ((int)(skillLevel/3));
    }


    @Override
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

        return cooldownManager.isReady(caster.getUniqueId(), 8, statusEffectManager.getHastePercent(caster));
    }

}
