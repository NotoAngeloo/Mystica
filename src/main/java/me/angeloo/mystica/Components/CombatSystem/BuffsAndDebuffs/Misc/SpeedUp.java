package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SpeedUp implements StatusEffect {

    @Override
    public String getId() {
        return "speed_up";
    }

    @Override
    public void onApply(LivingEntity entity, StatusInstance instance){

        if(!(entity instanceof Player player)){
            return;
        }

        double magnitude = instance.getEffect().getMagnitude();

        float speed = (float) magnitude;

        player.setWalkSpeed(speed);

    }

    @Override
    public void onRemove(LivingEntity entity, StatusInstance instance){

        if(!(entity instanceof Player player)){
            return;
        }

        player.setWalkSpeed(.3f);
    }

    @Override
    public StatusStackType stackType(){
        return StatusStackType.REPLACE_SMALLER;
    }

}
