package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class Fear implements StatusEffect {

    @Override
    public String getId() {
        return "fear";
    }

    @Override
    public boolean requireDurationDeclaration(){
        return true;
    }

    @Override
    public StatusInstance createInstance(int duration, double magnitude, LivingEntity source) {
        return new FearInstance(this, getDuration(), getMagnitude(), source);
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public boolean isHardCC() {
        return true;
    }
}
