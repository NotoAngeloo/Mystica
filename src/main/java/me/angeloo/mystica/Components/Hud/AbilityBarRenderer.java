package me.angeloo.mystica.Components.Hud;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class AbilityBarRenderer {

    private final AbilityManager abilityManager;
    private final CooldownManager cooldownManager;

    public AbilityBarRenderer(AbilityManager manager){
        this.abilityManager = manager;
        this.cooldownManager = manager.getCooldownManager();
    }

    public String render(Player player, EquipSkills equipSkills, double haste, long now){

        StringBuilder bar = new StringBuilder();

        //place the ultimate ability first

        for(int slot = 0;slot<equipSkills.size();slot++){

            int abilityNumber = equipSkills.getSkill(slot);

            if(abilityNumber == EquipSkills.EMPTY){
                continue;
            }

            long remaining = cooldownManager.getRemaining(
                    player.getUniqueId(),
                    abilityNumber,
                    haste,
                    now
            );

            /*TODO: assign a unicode to every ability, depending on class get that unicode.
                Also create a unicode to display numbers. place number unicode on top of ability unicode. apply negative space
            */
            //abilityManager.

        }


        return String.valueOf(bar);
    }

}
