package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.None;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class NoneRoll {

    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    public NoneRoll(Mystica main, AbilityManager manager){
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = manager.getCooldownManager();;
    }

    private final int abilityNumber = 3;
    private final int baseCooldown = 9;

    public void use(LivingEntity caster){


        if(!usable(caster)){
            return;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), abilityNumber, (long) (baseCooldown * 1000));
    }

    private void execute(LivingEntity caster){

        Location start = caster.getLocation();


        Vector direction = start.getDirection().normalize();


        double forwardPower = 2;
        double jumpPower = .1;
        Vector dashVector = direction.multiply(forwardPower).setY(jumpPower);
        caster.setVelocity(dashVector);


    }


    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), abilityNumber, statusEffectManager.getHastePercent(caster));
    }

}
