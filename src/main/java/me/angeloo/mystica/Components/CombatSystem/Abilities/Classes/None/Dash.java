package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.None;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc.SpeedUp;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Dash extends BaseAbility {

    private final Mystica main;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    public Dash(Mystica main, AbilityManager manager){
        super("dash");
        this.main = main;
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 20;

    @Override
    public boolean use(LivingEntity caster){


        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 2, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){


        if(caster instanceof Player){
            statusEffectManager.applyEffect(caster, new SpeedUp(), 100, 0.5, caster);
        }

    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 2, statusEffectManager.getHastePercent(caster));
    }

}
