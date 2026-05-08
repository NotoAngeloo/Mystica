package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class ReigningSword extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownManager cooldownManager;

    private final Purity purity;


    public ReigningSword(Mystica main, AbilityManager manager){
        super("reigning_sword");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownManager = main.getCooldownManager();
        purity = manager.getPurity();
    }

    private final int baseCooldown = 10;
    private final int baseDamage = 25;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);

        if(profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Dawn)){
            purity.add(caster, 3);
        }


        cooldownManager.start(caster.getUniqueId(), 3, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        boolean templar = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Templar);

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();

        Location start = caster.getLocation().clone().add(direction.multiply(1));
        start.setDirection(direction);

        ArmorStand sword = caster.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
        sword.setInvisible(true);
        sword.setGravity(false);
        sword.setCollidable(false);
        sword.setInvulnerable(true);
        sword.setMarker(true);

        EntityEquipment entityEquipment = sword.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(6);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);

        sword.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        sword.teleport(start);

        Set<LivingEntity> hitBySkill = new HashSet<>();



        double shield = (profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster)) * 0.1;

        if(templar){
            shield = shield * 1.2;
        }

        statusEffectManager.applyEffect(caster, new GenericShield(), null, shield, caster);

        double finalShield = shield;
        new BukkitRunnable(){
            @Override
            public void run(){
                statusEffectManager.reduceShield(caster, finalShield);
            }
        }.runTaskLater(main, 20*5);

        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if (initialDirection == null) {
                    initialDirection = caster.getLocation().getDirection().setY(0).normalize();
                }

                Location center = caster.getLocation();

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                Location loc = center.clone().add(direction.clone().multiply(1)).setDirection(direction);
                sword.teleport(loc);

                if(angle <= 360){
                    caster.teleport(caster.getLocation().setDirection(direction));
                }

                BoundingBox hitBox = new BoundingBox(
                        center.getX() - 5,
                        center.getY() - 2,
                        center.getZ() - 5,
                        center.getX() + 5,
                        center.getY() + 5,
                        center.getZ() + 5
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

                    double bonus = 1;

                    if(templar){
                        bonus = 2.2;
                    }

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = (damageCalculator.calculateDamage(caster, livingEntity, DamageType.Physical, finalSkillDamage
                            * bonus * decisionMultiplier(caster), crit, 0));


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

                if(angle >= 460){
                    cancelTask();
                }

                angle+=30;
            }

            private void cancelTask(){
                this.cancel();
                sword.remove();
                statusEffectManager.removeEffect(caster, "decision");
            }

        }.runTaskTimer(main, 0, 1);

    }

    private double decisionMultiplier(LivingEntity caster){

        if(statusEffectManager.hasEffect(caster, "decision")){
            return 1.8;
        }

        return 1;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_3_Level_Bonus();

        double damage = baseDamage + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 3, statusEffectManager.getHastePercent(caster));
    }

}
