package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class FlamingSigilHealth implements StatusEffect {

    @Override
    public String getId() {
        return "flaming_sigil_health";
    }


    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.REPLACE_SMALLER;
    }

    @Override
    public int getDuration(){
        return 8 * 20;
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue42e";
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
