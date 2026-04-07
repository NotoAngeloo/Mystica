package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class Stun implements StatusEffect {

    @Override
    public String getId() {
        return "stun";
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance){
        //interrupt casting
    }

    @Override
    public boolean requireDurationDeclaration(){
        return true;
    }
}
