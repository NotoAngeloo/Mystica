package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class ConjuringForceBuff implements StatusEffect {

    @Override
    public String getId(){
        return "conjuring_force";
    }

    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.REPLACE_SMALLER;
    }


    //increase damage dealt by flat amount
    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue42d";
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance) {
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
    }

    @Override
    public void onRemoveEffects(LivingEntity entity, StatusInstance instance, CombatContext combatContext) {
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
    }
}
