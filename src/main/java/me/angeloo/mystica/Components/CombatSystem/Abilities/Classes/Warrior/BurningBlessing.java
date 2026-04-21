package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.BurningBlessingBuff;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BurningBlessing extends BaseAbility {

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    public BurningBlessing(Mystica main, AbilityManager manager){
        super("burning_blessing");
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
    }

    private final int baseCooldown = 16;
    private final int buffAmount = 5;

    @Override
    public void use(LivingEntity caster){

        if(!usable(caster)){
            return;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 8, (long) (baseCooldown * 1000));
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        statusEffectManager.applyEffect(caster, new BurningBlessingBuff(), null, getBuffAmount(caster));

        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster);
        double fourth = maxHealth * .25;
        changeResourceHandler.addHealthToEntity(caster, fourth, caster);

        //TODO: add icon above head when i have it

    }



    public double getBuffAmount(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_8_Level_Bonus();
        return buffAmount + ((int)(skillLevel/3));
    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 8, statusEffectManager.getHastePercent(caster));
    }

}
