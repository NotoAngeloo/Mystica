package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin;

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
        cooldownManager = manager.getCooldownManager();
    }

    private final int baseCooldown = 33;

    @Override
    public void use(LivingEntity caster){

        //figure something else later
        if(!(caster instanceof Player player)){
            return;
        }

        if(!usable(caster)){
            return;
        }

        execute(player);

        cooldownManager.start(caster.getUniqueId(), 5, (long) (baseCooldown * 1000));
    }

    private void execute(Player player){


        statusEffectManager.applyEffect(player, new SpeedUp(), null, 0.7);

        new BukkitRunnable(){
            @Override
            public void run(){
                statusEffectManager.removeEffect(player, "speed_up");
            }
        }.runTaskLater(main, 100);

    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 5, statusEffectManager.getHastePercent(caster));
    }

}
