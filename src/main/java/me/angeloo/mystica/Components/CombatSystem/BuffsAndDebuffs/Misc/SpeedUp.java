package me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.*;
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

        double magnitude = instance.getInstanceMagnitude();

        float speed = (float) magnitude;

        player.setWalkSpeed(speed);

    }

    @Override
    public void onRemoveEffects(LivingEntity entity, StatusInstance instance, CombatContext combatContext){

        if(!(entity instanceof Player player)){
            return;
        }

        player.setWalkSpeed(.3f);
    }

    @Override
    public ApplicationBehavior applicationBehavior(){
        return ApplicationBehavior.REPLACE_SMALLER;
    }

    @Override
    public boolean requireMagnitudeDeclaration() {
        return true;
    }

    //duration not required since divine infusion and relentless work a bit different

    @Override
    public String getIcon(LivingEntity entity, StatusInstance instance) {
        return "\ue421";
    }



    @Override
    public int getPriority() {
        return 4;
    }
}
