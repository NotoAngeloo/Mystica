package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusApplicationResult;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class Sleep implements StatusEffect {

    @Override
    public String getId() {
        return "sleep";
    }

    @Override
    public boolean requireDurationDeclaration(){
        return true;
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance, CombatContext combatContext, StatusApplicationResult statusApplicationResult){
        //interrupt casting
    }

    @Override
    public StatusInstance createInstance(int duration, double magnitude, LivingEntity source) {
        // Use our custom SleepInstance
        return new SleepInstance(this, getDuration(), source);
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue42a";
    }

    @Override
    public boolean isHardCC() {
        return true;
    }
}
