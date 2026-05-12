package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CombatContext;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusApplicationResult;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class Stun implements StatusEffect {

    @Override
    public String getId() {
        return "stun";
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance, CombatContext combatContext, StatusApplicationResult statusApplicationResult){
        //interrupt casting
    }

    @Override
    public boolean requireDurationDeclaration(){
        return true;
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
