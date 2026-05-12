package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusApplicationResult;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.CustomEvents.ApplyStealthEffectEvent;
import me.angeloo.mystica.CustomEvents.RemoveStealthEffectEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class StealthEffect implements StatusEffect {

    @Override
    public String getId(){
        return "stealth";
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance, CombatContext combatContext, StatusApplicationResult statusApplicationResult){
        Bukkit.getServer().getPluginManager().callEvent(new ApplyStealthEffectEvent(entity));
    }

    @Override
    public void onRemove(LivingEntity entity, StatusInstance instance){
        Bukkit.getServer().getPluginManager().callEvent(new RemoveStealthEffectEvent(entity));
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue437";
    }
}
