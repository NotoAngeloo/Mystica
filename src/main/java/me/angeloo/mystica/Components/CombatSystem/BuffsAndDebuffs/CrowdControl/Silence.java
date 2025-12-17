package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;
import org.bukkit.entity.LivingEntity;

public class Silence implements StatusEffect {

    @Override
    public String getId() {
        return "silence";
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
