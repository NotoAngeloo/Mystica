package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SanctityShield extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    public SanctityShield(Mystica main, AbilityManager manager){
        super("sanctity_shield");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 12;
    private final int baseShield = 10;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){


        double healAmount = getHealAmount(caster);
        double shield = getShieldAmount(caster);

        statusEffectManager.applyEffect(caster, new GenericShield(), null, shield, caster);


        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(!statusEffectManager.hasShield(caster)){
                    this.cancel();
                    return;
                }

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        statusEffectManager.reduceShield(caster, shield);
                        return;
                    }
                }

                if(profileManager.getAnyProfile(caster).getIfDead()){
                    this.cancel();
                    statusEffectManager.reduceShield(caster, shield);
                    return;
                }

                changeResourceHandler.addHealthToEntity(caster, healAmount, caster);

                if(count>=5){
                    this.cancel();
                    statusEffectManager.reduceShield(caster, shield);
                }
                count++;
            }
        }.runTaskTimer(main, 0, 20);

    }

    public double getShieldAmount(LivingEntity caster){
        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster);
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return  (level + baseShield / maxHealth) * 100;
    }

    public double getHealAmount(LivingEntity caster){
        double maxHealth = profileManager.getAnyProfile(caster).getTotalHealth() + statusEffectManager.getHealthBuffAmount(caster);
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return maxHealth * ((level + 5) /100);
    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), -1, statusEffectManager.getHastePercent(caster));
    }


}
