package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.PaladinAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Haste;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpiritualGift extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final DamageCalculator damageCalculator;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    private final Purity purity;


    public SpiritualGift(Mystica main, AbilityManager manager){
        super("spiritual_gift");
        this.main = main;
        profileManager = main.getProfileManager();
        damageCalculator = main.getDamageCalculator();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
        purity = manager.getPurity();
    }

    private final int baseCooldown = 20;
    private final int healPower = 5;

    @Override
    public void use(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        if(target == null){
            target = caster;
        }

        if (profileManager.getAnyProfile(target).getIfDead()) {
            target = caster;
        }

        if(target instanceof Player){
            if(pvpManager.pvpLogic(caster, (Player) target)){
                target = caster;
            }
        }


        execute(caster, target);

        cooldownManager.start(caster.getUniqueId(), 5, (long) (baseCooldown * 1000));

    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }


    private double getRange(LivingEntity caster){
        double baseRange = 12;
        double extraRange = statusEffectManager.getAdditionalRange(caster);
        return baseRange + extraRange;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        //every 15 levels is a +1

        //50% cdr. might be a bit high but fuck it we ball
        statusEffectManager.applyEffect(target, new Haste(), getDuration(caster), 0.5);

        double finalHealPower = getHealPower(caster);
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(!targetStillValid(target)){
                    this.cancel();
                    return;
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

                if(count>=getDuration(caster)){
                    this.cancel();
                    healTarget(target);
                }

                count++;
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){
                    if(!((Player) target).isOnline()){
                        return false;
                    }
                }
                return !target.isDead();
            }

            private void healTarget(LivingEntity target){

                if(!targetStillValid(target)){
                    return;
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                double healAmount = damageCalculator.calculateHealing(caster, finalHealPower, crit);

                changeResourceHandler.addHealthToEntity(target, healAmount, caster);
            }

        }.runTaskTimer(main, 0, 1);

    }


    public double getHealPower(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();
        double damage = healPower + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }

    public int getDuration(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();
        int bonusDuration = (int)(skillLevel/3);
        return  (5*20) + (bonusDuration*20);
    }


    @Override
    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){

            if(!(target instanceof Player)){

                if(pveChecker.pveLogic(target)){
                    target = caster;
                }

            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > getRange(caster)){
                return false;
            }
        }


        return cooldownManager.isReady(caster.getUniqueId(), 5, statusEffectManager.getHastePercent(caster));
    }


}
