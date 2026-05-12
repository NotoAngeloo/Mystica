package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageOverTime;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Utility.Enums.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class Infection_Enhanced implements StatusEffect {

    @Override
    public String getId() {
        return "infection_enhanced";
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
            boolean crit = combatContext.damageCalculator().checkIfCrit(caster, 0);
            double damage = combatContext.damageCalculator().calculateDamage(caster, entity, DamageType.Physical, getMagnitude(), crit, 0);
            combatContext.changeResourceHandler().subtractHealthFromEntity(entity, damage, caster, crit);
            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(entity, caster));

        }

    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue435";
    }
}
