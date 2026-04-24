package me.angeloo.mystica.Components.Hud.StatusEffects;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.ShieldInstance;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusStackType;
import me.angeloo.mystica.Components.Hud.Abilties.AbilityRenderState;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StatusEffectRenderer {

    private final StatusEffectManager manager;

    //0-16
    private final String[] RADIAL = new String[] {"\ue21a","\ue21b","\ue21c","\ue21d","\ue21e","\ue21f","\ue220","\ue221","\ue222","\ue223","\ue224","\ue225","\ue226","\ue227","\ue228","\ue229","\ue22a"};

    private final String[] STACKS = new String[] {"\uE22B", "\uE22C", "\uE22D", "\uE22E", "\uE22F",
            "\uE230", "\uE231", "\uE232", "\uE233", "\uE234",
            "\uE235", "\uE236", "\uE237", "\uE238", "\uE239",
            "\uE23A", "\uE23B", "\uE23C", "\uE23D"};

    public StatusEffectRenderer(StatusEffectManager manager){
        this.manager = manager;
    }

    //do NOT append negative space before here, as i would like to also display boss effects
    public String render(LivingEntity entity){
        StringBuilder builder = new StringBuilder();

        //but first, get self contained ability buffs



        Map<String, StatusInstance> statusInstanceMap = manager.getInstanceMap(entity);

        if(statusInstanceMap == null){
            return "";
        }

        List<StatusInstance> instances = new ArrayList<>(statusInstanceMap.values());

        instances.sort(Comparator.comparingInt(
                instance -> instance.getEffect().getPriority()
        ));


        for(StatusInstance instance : instances){

            if(instance instanceof ShieldInstance){
                continue;
            }

            StatusEffectRenderState state = new StatusEffectRenderState(instance);

            builder.append(state.getIcon());

            if(state.shouldShowRadial()){
                //-17
                builder.append("\uF809\uF801");
                builder.append(getRadialGlyph(state));
            }

            //put stack number on top
            if(instance.getEffect().stackType().equals(StatusStackType.ADDITIVE)){

                //get effect mag returns applied mag
                int stackAmount = (int) instance.getInstanceMagnitude();

                //don't display more than 20
                if(stackAmount>20){
                    stackAmount = 20;
                }

                if(stackAmount>1){
                    //-17
                    builder.append("\uF809\uF801");
                    builder.append(STACKS[stackAmount-2]);
                }

            }

        }


        return String.valueOf(builder);
    }

    private String getRadialGlyph(StatusEffectRenderState state) {

        double pct = state.getPercent(); // 0 → 1

        int maxIndex = RADIAL.length - 1;

        int index = (int) Math.round(pct * maxIndex);
        index = Math.max(0, Math.min(maxIndex, index));

        return RADIAL[index];
    }

    public int getStatusWidth(LivingEntity entity){
        int width = 0;

        Map<String, StatusInstance> statusInstanceMap = manager.getInstanceMap(entity);

        if(statusInstanceMap == null){
            return width;
        }

        for(StatusInstance instance : statusInstanceMap.values()) {

            if (instance instanceof ShieldInstance) {
                continue;
            }
            // base icon
            width += 17;
        }

        return width;
    }

}
