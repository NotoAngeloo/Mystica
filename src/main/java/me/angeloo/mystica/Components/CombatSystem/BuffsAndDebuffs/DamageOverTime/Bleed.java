package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageOverTime;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Utility.Enums.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class Bleed implements StatusEffect {

    @Override
    public String getId() {
        return "bleed";
    }

    //perhaps change this later
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
            //can't crit
            double damage = combatContext.damageCalculator().calculateDamage(caster, entity, DamageType.Physical, getMagnitude(), false, 0);
            combatContext.changeResourceHandler().subtractHealthFromEntity(entity, damage, caster, false);
            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(entity, caster));

        }

    }

    //priority 5

}
