package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc.SpeedUp;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Roll extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;


    public Roll(Mystica main, AbilityManager manager){
        super("roll");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = manager.getCooldownManager();;
    }

    private final int baseCooldown = 13;

    @Override
    public boolean use(LivingEntity caster){


        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 8, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        Location start = caster.getLocation();

        Vector direction = start.getDirection().normalize();

        double shieldAmount = (profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster)) / 4;

        if(caster instanceof Player){
            if(((Player)caster).isSneaking()){
                direction.multiply(-1);
                shieldAmount*=2;
            }
        }

        if(profileManager.getAnyProfile(caster).fakePlayer()){
            direction.multiply(-1);
            shieldAmount*=2;
        }

        statusEffectManager.applyEffect(caster, new GenericShield(), null, shieldAmount);

        double forwardPower = 3;
        double jumpPower = .2;
        Vector dashVector = direction.multiply(forwardPower).setY(jumpPower);
        caster.setVelocity(dashVector);

        //also give a shield and increase move speed
        if(caster instanceof Player){
            statusEffectManager.applyEffect(caster, new SpeedUp(), null, 0.6);
        }



        double finalShieldAmount = shieldAmount;
        new BukkitRunnable(){
            @Override
            public void run(){
                if(caster instanceof Player){
                    statusEffectManager.removeEffect(caster, "speed_up");
                }

                statusEffectManager.reduceShield(caster, finalShieldAmount);
            }
        }.runTaskLater(main, 100);

    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 8, statusEffectManager.getHastePercent(caster));
    }

}
