package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageOverTime;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class Infection_Standard implements StatusEffect {


    @Override
    public String getId() {
        return "infection";
    }

    @Override
    public int getDuration() {
        return 11 * 20;
    }

    @Override
    public boolean requireMagnitudeDeclaration() {
        return true;
    }

    @Override
    public ApplicationBehavior applicationBehavior() {
        return ApplicationBehavior.REPLACE_SMALLER;
    }

    @Override
    public void onTick(LivingEntity entity, StatusInstance instance, CombatContext combatContext) {

        if(instance.getLivedTicks()%20==0){
            LivingEntity caster = instance.getSource();
            boolean crit = combatContext.getDamageCalculator().checkIfCrit(caster, 0);
            double damage = combatContext.getDamageCalculator().calculateDamage(caster, entity, "Physical", getMagnitude(), crit);
            combatContext.getChangeResourceHandler().subtractHealthFromEntity(entity, damage, caster, crit);
            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(entity, caster));

        }

    }
}
