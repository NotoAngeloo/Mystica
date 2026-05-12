package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ApplicationBehavior;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class FlamingSigilAttack implements StatusEffect {

    @Override
    public String getId() {
        return "flaming_sigil_attack";
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

    //increase attack by magnitude
    @Override
    public boolean requireMagnitudeDeclaration(){
        return true;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue42e";
    }
}
