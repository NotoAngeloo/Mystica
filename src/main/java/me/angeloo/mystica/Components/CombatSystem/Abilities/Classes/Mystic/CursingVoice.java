package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Sleep;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CursingVoice extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;

    public CursingVoice(Mystica main, AbilityManager manager){
        super("cursing_voice");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 45;
    private final double range = 15;

    @Override
    public boolean use(LivingEntity caster){


        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        cooldownManager.start(caster.getUniqueId(), 7, (long) (baseCooldown * 1000));


        new BukkitRunnable(){
            final Location current = caster.getLocation().clone().add(0,1,0);
            Location targetWasLoc = target.getLocation().clone().subtract(0,1,0);
            @Override
            public void run(){

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation().clone().add(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                if (!sameWorld(current, targetWasLoc)) {
                    cancelTask();
                    return;
                }

                Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                double distance = current.distance(targetWasLoc);
                double distanceThisTick = Math.min(distance, 3);
                current.add(direction.normalize().multiply(distanceThisTick));

                caster.getWorld().spawnParticle(Particle.SONIC_BOOM, current, 1, 0, 0, 0, 0);

                if (distance <= 1) {
                    cancelTask();

                    statusEffectManager.applyEffect(target, new Sleep(), 20*10, null, caster);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                }

            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private boolean sameWorld(Location loc1, Location loc2) {
                return loc1.getWorld().equals(loc2.getWorld());
            }

            private void cancelTask() {
                this.cancel();
            }
        }.runTaskTimer(main, 0, 3);


        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    @Override
    public boolean usable(LivingEntity caster, LivingEntity target){

        if(target == null){
            return false;
        }

        double distance = caster.getLocation().distance(target.getLocation());
        if(distance > range + statusEffectManager.getAdditionalRange(caster)){
            return false;
        }

        if(target instanceof Player){
            if(!pvpManager.pvpLogic(caster, (Player) target)){
                return false;
            }
        }
        else{
            if(!pveChecker.pveLogic(target)){
                return false;
            }

            if(!profileManager.getAnyProfile(target).canBeHardCCed()){
                return false;
            }
        }


        return cooldownManager.isReady(caster.getUniqueId(), 7, statusEffectManager.getHastePercent(caster));
    }

}
