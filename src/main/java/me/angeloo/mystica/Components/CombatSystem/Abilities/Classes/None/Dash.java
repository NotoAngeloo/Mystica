package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.None;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc.SpeedUp;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Dash {

    private final Mystica main;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    public Dash(Mystica main, AbilityManager manager){
        this.main = main;
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = manager.getCooldownManager();
    }

    private final int abilityNumber = 2;
    private final int baseCooldown = 20;

    public void use(LivingEntity caster){


        if(!usable(caster)){
            return;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), abilityNumber, (long) (baseCooldown * 1000));

    }

    private void execute(LivingEntity caster){


        if(caster instanceof Player){
            statusEffectManager.applyEffect(caster, new SpeedUp(), null, 0.5);
        }



        new BukkitRunnable(){
            @Override
            public void run(){
                if(caster instanceof Player){
                    statusEffectManager.removeEffect(caster, "speed_up");
                }

            }
        }.runTaskLater(main, 100);

    }


    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), abilityNumber, statusEffectManager.getHastePercent(caster));
    }

}
