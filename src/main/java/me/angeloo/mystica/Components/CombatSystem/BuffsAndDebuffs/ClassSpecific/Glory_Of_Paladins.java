package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import org.bukkit.entity.LivingEntity;

public class Glory_Of_Paladins implements StatusEffect {

    @Override
    public String getId() {
        return "glory_of_paladins";
    }

    @Override
    public int getDuration(){
        return 15 * 20;
    }

    @Override
    public int getPriority(){
        return 2;
    }

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue41d";
    }
}
