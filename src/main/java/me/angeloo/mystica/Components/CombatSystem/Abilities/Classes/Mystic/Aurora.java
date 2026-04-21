package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.MysticAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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

import java.util.*;

public class Aurora extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PveChecker pveChecker;
    private final PvpManager pvpManager;
    private final CooldownManager cooldownManager;
    private final Mana mana;


    public Aurora(Mystica main, AbilityManager manager){
        super("aurora");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pveChecker = main.getPveChecker();
        pvpManager = main.getPvpManager();
        cooldownManager = manager.getCooldownManager();
        mana = manager.getMana();
    }

    private final int baseCooldown = 15;
    private final double range = 20;
    private final int cost = 100;

    @Override
    public void use(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target == null){
            target = caster;
        }

        mana.subTractManaFromEntity(caster, cost);

        execute(caster, target);

        cooldownManager.start(caster.getUniqueId(), 6, (long) (baseCooldown * 1000));
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void  execute(LivingEntity caster, LivingEntity target){

        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Shepard);

        Location center = target.getLocation().clone();

        double healPercent = 10;
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();
        healPercent = healPercent +  ((int)(skillLevel/3));

        double shieldAmount = (profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster) + skillLevel) * .5;

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

                        if(!(entity instanceof LivingEntity hitEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

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

                        statusEffectManager.applyEffect(hitEntity, new GenericShield(), null, shieldAmount);

                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                statusEffectManager.reduceShield(hitEntity, shieldAmount);
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

    @Override
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

        if(distance > range + statusEffectManager.getAdditionalRange(caster)){
            return false;
        }

        if (mana.getCurrentMana(caster)<cost) {
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return false;
        }


        return cooldownManager.isReady(caster.getUniqueId(), 6, statusEffectManager.getHastePercent(caster));
    }

    /*public int returnWhichItem(Player player){

        if(mana.getCurrentMana(player)<getCost()){
            return 7;
        }

        return 0;
    }*/

}
