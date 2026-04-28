package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.None;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class NoneRoll extends BaseAbility {

    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    public NoneRoll(Mystica main, AbilityManager manager){
        super("none_roll");
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 9;

    @Override
    public boolean use(LivingEntity caster){


        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 3, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        Location start = caster.getLocation();


        Vector direction = start.getDirection().normalize();


        double forwardPower = 2;
        double jumpPower = .1;
        Vector dashVector = direction.multiply(forwardPower).setY(jumpPower);
        caster.setVelocity(dashVector);


    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 3, statusEffectManager.getHastePercent(caster));
    }

}
