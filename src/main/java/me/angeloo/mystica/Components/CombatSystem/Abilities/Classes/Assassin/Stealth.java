package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Misc.StealthEffect;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Logic.StealthTargetBlacklist;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Stealth extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StealthTargetBlacklist stealthTargetBlacklist;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    private final Combo combo;
    //private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, Boolean> stealthed = new HashMap<>();
    private final Map<UUID, Integer> timeInStealth = new HashMap<>();
    //private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();

    //have to have custom logic for stealth cooldown display

    public Stealth(Mystica main, AbilityManager manager){
        super("stealth");
        this.main = main;
        profileManager = main.getProfileManager();
        stealthTargetBlacklist = main.getStealthTargetBlacklist();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
        combo = manager.getCombo();
    }


    private final int baseCooldown = 30;
    private final int baseDamage = 50;

    @Override
    public boolean use(LivingEntity caster){


        if(getIfStealthed(caster)){
            reveal(caster);
            //cooldownDisplayer.displayCooldown(caster, 8);
            return false;
        }


        if(!cooldownManager.isReady(caster.getUniqueId(), 8, statusEffectManager.getHastePercent(caster))){
            return false;
        }

        //cooldownDisplayer.displayCooldown(caster, 8);

        vanish(caster);

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void vanish(LivingEntity caster){

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        this.cancel();
                        return;
                    }
                }


                boolean deathStatus = profileManager.getAnyProfile(caster).getIfDead();

                if(deathStatus || !statusEffectManager.canCast(caster)){
                    this.cancel();
                    return;
                }

                Location particleLocation = caster.getLocation().add(0, 1, 0);
                caster.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, particleLocation, 10, 0.3, 0.5, 0.3, 0.05);

                if(count >= 5){
                    statusEffectManager.applyEffect(caster, new StealthEffect(), null, null, caster);
                    stealthTargetBlacklist.add(caster);
                    stealthed.put(caster.getUniqueId(), true);


                    revealAfterTime(caster);

                    //a new way to remove this player from a pvp bar

                    this.cancel();
                }
                count ++;
            }
        }.runTaskTimer(main, 0, 1);



    }

    private void revealAfterTime(LivingEntity caster){

        //10 secs to be in stealth without triggering cooldown
        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                //cooldownDisplayer.displayCooldown(caster, 8);

                if(!getIfStealthed(caster)){
                    this.cancel();
                    timeInStealth.remove(caster.getUniqueId());
                    return;
                }

                if(count>=10){
                    this.cancel();
                    forceReveal(caster, null);
                    timeInStealth.remove(caster.getUniqueId());
                    return;
                }

                count++;

                timeInStealth.put(caster.getUniqueId(), count);
            }
        }.runTaskTimer(main, 0, 20);

    }

    public void reveal(LivingEntity caster){
        statusEffectManager.removeEffect(caster, "stealth");
        stealthTargetBlacklist.remove(caster);
        stealthed.put(caster.getUniqueId(), false);
    }

    private void forceReveal(LivingEntity caster, LivingEntity victim){
        statusEffectManager.removeEffect(caster, "stealth");
        stealthTargetBlacklist.remove(caster);
        stealthed.put(caster.getUniqueId(), false);

        //cooldown triggers regardless if successful in performing a sneak attack or not
        cooldownManager.start(caster.getUniqueId(), 8, (long) (baseCooldown * 1000));

        if(victim==null){
            return;
        }


        boolean crit = damageCalculator.checkIfCrit(caster, 0);
        double damage = damageCalculator.calculateDamage(caster, victim, "Physical", getSkillDamage(caster), crit);
        combo.addComboPoint(caster);
        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(victim, caster));
        changeResourceHandler.subtractHealthFromEntity(victim, damage, caster, crit);
    }


    @Override
    public void onExternalTrigger(LivingEntity caster, LivingEntity target){
        stealthBonusCheck(caster, target);
    }

    private void stealthBonusCheck(LivingEntity caster, LivingEntity entity){

        if(!getIfStealthed(caster)){
            return;
        }

        forceReveal(caster, entity);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_8_Level_Bonus();
        return baseDamage + ((int)(skillLevel/3));
    }

    public boolean getIfStealthed(LivingEntity caster){
        return stealthed.getOrDefault(caster.getUniqueId(), false);
    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 8, statusEffectManager.getHastePercent(caster));
    }

    //TODO: when making icons, display a different if stealthed

    /*public int returnWhichItem(Player player){

        if(getIfStealthed(player)){
            return 1;
        }

        return 0;
    }*/




}
