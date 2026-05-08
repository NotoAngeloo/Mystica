package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific.Glory_Of_Paladins;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GloryOfPaladins extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    private final Purity purity;

    public GloryOfPaladins(Mystica main, AbilityManager manager){
        super("glory_of_paladins");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
        purity = manager.getPurity();
    }

    private final int baseCooldown = 12;
    private final int baseDamage = 20;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);

        if(profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Dawn)){
            purity.add(caster, 6);
        }



        cooldownManager.start(caster.getUniqueId(), 6, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        //TODO:increase max hp as well

        statusEffectManager.applyEffect(caster, new Glory_Of_Paladins(), null, null, caster);


        new BukkitRunnable(){
            double height = 0;
            boolean up = true;
            final double radius = 1;
            double angle = 0;
            Vector initialDirection;
            @Override
            public void run(){

                if(caster.isDead()){
                    this.cancel();
                    return;
                }

                if(!statusEffectManager.hasEffect(caster, "glory_of_paladins")){
                    this.cancel();
                    return;
                }

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        return;
                    }
                }



                Location loc = caster.getLocation();

                if(initialDirection == null) {
                    initialDirection = loc.getDirection().setY(0).normalize();
                    initialDirection.rotateAroundY(Math.toRadians(-45));
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);

                direction.rotateAroundY(radians);

                double x = loc.getX() + direction.getX() * radius;
                double z = loc.getZ() + direction.getZ() * radius;

                double x2 = loc.getX() - direction.getX() * radius;
                double z2 = loc.getZ() - direction.getZ() * radius;

                Location particleLoc = new Location(loc.getWorld(), x, loc.getY() + height, z);
                Location particleLoc2 = new Location(loc.getWorld(), x2, loc.getY() + height, z2);

                caster.getWorld().spawnParticle(Particle.WAX_OFF, particleLoc, 1, 0, 0, 0, 0);
                caster.getWorld().spawnParticle(Particle.WAX_OFF, particleLoc2, 1, 0, 0, 0, 0);

                if(up){
                    height += .1;
                }
                else{
                    height -= .1;
                }

                angle += 5;

                if(height >= 2){
                    up = false;
                }

                if(height < 0){
                    up = true;
                }

            }
        }.runTaskTimer(main, 0, 2);

    }

    public void onExternalTrigger(LivingEntity caster, LivingEntity target){
        procGlory(caster, target);
    }

    private void procGlory(LivingEntity caster, LivingEntity livingEntity){

        if(!statusEffectManager.hasEffect(caster, "glory_of_paladins")){
            return;
        }


        boolean crit = damageCalculator.checkIfCrit(caster, 0);
        double damage = damageCalculator.calculateDamage(caster, livingEntity, DamageType.Physical, getSkillDamage(caster), crit,0);

        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);

        double healAmount = (profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster)) * .05;
        //chance to restore
        int random = (int) (Math.random() * 100) + 1;
        if(random >= 25){
            changeResourceHandler.addHealthToEntity(caster, healAmount, caster);
        }
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();

        double damage = baseDamage + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }



    @Override
    public boolean usable(LivingEntity caster){
        if(statusEffectManager.hasEffect(caster, "glory_of_paladins")){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), 6, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue3e5";
    }
}
