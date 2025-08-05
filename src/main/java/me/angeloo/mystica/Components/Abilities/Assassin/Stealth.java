package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.StealthTargetBlacklist;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Stealth {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StealthTargetBlacklist stealthTargetBlacklist;
    private final PvpManager pvpManager;
    private final TargetManager targetManager;
    private final DamageCalculator damageCalculator;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Combo combo;
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();
    private final Map<UUID, Boolean> stealthed = new HashMap<>();
    private final Map<UUID, Integer> timeInStealth = new HashMap<>();
    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();

    public Stealth(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        stealthTargetBlacklist = main.getStealthTargetBlacklist();
        pvpManager = main.getPvpManager();
        targetManager = main.getTargetManager();
        damageCalculator = main.getDamageCalculator();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        combo = assassinAbilities.getCombo();
    }

    public void toggle(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(getIfStealthed(caster)){
            reveal(caster);
            cooldownDisplayer.displayCooldown(caster, 8);
            return;
        }

        if(getCooldown(caster) > 0){
            return;
        }

        cooldownDisplayer.displayCooldown(caster, 8);

        vanish(caster);

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

                if(deathStatus || buffAndDebuffManager.getIfInterrupt(caster)){
                    this.cancel();
                    return;
                }

                Location particleLocation = caster.getLocation().add(0, 1, 0);
                caster.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, particleLocation, 10, 0.3, 0.5, 0.3, 0.05);

                if(count >= 5){
                    buffAndDebuffManager.getHidden().hidePlayer(caster, true);
                    stealthTargetBlacklist.add(caster);
                    stealthed.put(caster.getUniqueId(), true);
                    if (caster instanceof Player) {
                        Player player = (Player) caster;
                        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status, false));
                    }

                    revealAfterTime(caster);

                    //a new way to remove this player from a pvp bar

                    this.cancel();
                }
                count ++;
            }
        }.runTaskTimer(main, 0, 1);



    }

    private void revealAfterTime(LivingEntity caster){

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                cooldownDisplayer.displayCooldown(caster, 8);

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
        cooldownTask.put(caster.getUniqueId(), task);

    }

    public void reveal(LivingEntity caster){

        buffAndDebuffManager.getHidden().unhidePlayer(caster);
        stealthTargetBlacklist.remove(caster);
        stealthed.put(caster.getUniqueId(), false);
        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status, false));
        }

    }

    private void forceReveal(LivingEntity caster, LivingEntity victim){

        buffAndDebuffManager.getHidden().unhidePlayer(caster);
        stealthTargetBlacklist.remove(caster);
        stealthed.put(caster.getUniqueId(), false);
        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status, false));
        }

        abilityReadyInMap.put(caster.getUniqueId(), 30);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 8);
            }
        }.runTaskTimer(main, 0,20);

        if(victim==null){
            return;
        }


        boolean crit = damageCalculator.checkIfCrit(caster, 0);
        double damage = damageCalculator.calculateDamage(caster, victim, "Physical", getSkillDamage(caster), crit);
        combo.addComboPoint(caster);
        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(victim, caster));
        changeResourceHandler.subtractHealthFromEntity(victim, damage, caster, crit);
    }

    public void stealthBonusCheck(LivingEntity caster, LivingEntity entity){

        if(!getIfStealthed(caster)){
            return;
        }

        forceReveal(caster, entity);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_8_Level_Bonus();
        return 50 + ((int)(skillLevel/3));
    }

    public boolean getIfStealthed(LivingEntity caster){
        return stealthed.getOrDefault(caster.getUniqueId(), false);
    }

    public int getCooldown(LivingEntity caster){

        if(getIfStealthed(caster)){
            int time = timeInStealth.getOrDefault(caster.getUniqueId(), 0);
            return 10 - time;
        }

        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int returnWhichItem(Player player){

        if(getIfStealthed(player)){
            return 1;
        }

        return 0;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }


}
