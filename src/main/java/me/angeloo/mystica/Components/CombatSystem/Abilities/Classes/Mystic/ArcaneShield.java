package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityMarkManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.Shields.GenericShield;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ArcaneShield extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PveChecker pveChecker;
    private final PvpManager pvpManager;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;
    private final AbilityMarkManager abilityMarkManager;

    private final Mana mana;

    private final Map<UUID, Boolean> needToRemove = new HashMap<>();
    private final Map<UUID, BukkitTask> shieldTaskMap = new HashMap<>();

    public ArcaneShield(Mystica main, AbilityManager manager){
        super("arcane_shield");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pveChecker = main.getPveChecker();
        pvpManager = main.getPvpManager();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        mana = manager.getMana();
        cooldownManager = main.getCooldownManager();
        abilityMarkManager = manager.getAbilityMarkManager();
    }

    private final double range = 20;
    private final int baseCooldown = 5;
    private final int cost = 25;

    @Override
    public boolean use(LivingEntity caster){


        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        if(target == null){
            target = caster;
        }

        if(target instanceof Player){
            if(pvpManager.pvpLogic(caster, (Player) target)){
                target = caster;
            }
        }

        if(pveChecker.pveLogic(target)){
            target = caster;
        }

        mana.subTractManaFromEntity(caster, cost);

        execute(caster, target);

        cooldownManager.start(caster.getUniqueId(), 1, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        Set<LivingEntity> targetList = new HashSet<>();
        targetList.add(target);


        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Shepard);

        if(shepard){

            if(abilityMarkManager.getTargets(caster).contains(target)){
                targetList.addAll(abilityMarkManager.getTargets(caster));
                abilityMarkManager.removeTargets(caster);
            }


        }

        int skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_1_Level_Bonus();


        for(LivingEntity thisTarget : targetList){
            if(!needToRemove.containsKey(thisTarget.getUniqueId())){
                needToRemove.put(thisTarget.getUniqueId(), false);
            }

            if(needToRemove.get(thisTarget.getUniqueId())){
                needToRemove.put(thisTarget.getUniqueId(), false);
            }

            double fivePercent = (profileManager.getAnyProfile(thisTarget).getTotalHealth() + statusEffectManager.getHealthBuffAmount(thisTarget)) / 20;
            double shieldAmount = fivePercent + (((double) profileManager.getAnyProfile(caster).getTotalAttack() / 3) + skillLevel);

            statusEffectManager.applyEffect(thisTarget, new GenericShield(), null, shieldAmount, caster);


            int shieldDurationInTicks = 20*60;

            new BukkitRunnable(){
                @Override
                public void run(){
                    statusEffectManager.reduceShield(thisTarget, shieldAmount);
                    needToRemove.put(thisTarget.getUniqueId(), true);
                }
            }.runTaskLater(main, shieldDurationInTicks);


            if(shepard){
                //task to heal them for as long as they have a shield
                double thirtyPercent = (profileManager.getAnyProfile(thisTarget).getTotalHealth() + statusEffectManager.getHealthBuffAmount(thisTarget)) * .3;

                if(shieldTaskMap.containsKey(thisTarget.getUniqueId())){
                    shieldTaskMap.get(thisTarget.getUniqueId()).cancel();
                }

                BukkitTask task = new BukkitRunnable(){
                    @Override
                    public void run(){


                        boolean stillHasAShield = statusEffectManager.hasShield(thisTarget);

                        if(!stillHasAShield || needToRemove.get(thisTarget.getUniqueId())){
                            this.cancel();
                            return;
                        }

                        changeResourceHandler.addHealthToEntity(thisTarget, thirtyPercent, caster);

                        Location center = thisTarget.getLocation().clone().add(0,1,0);

                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double x = center.getX() + (1 * Math.cos(angle));
                            double z = center.getZ() + (1 * Math.sin(angle));
                            Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                            thisTarget.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                        }



                    }
                }.runTaskTimer(main, 0, 20 * 20);

                shieldTaskMap.put(thisTarget.getUniqueId(), task);

            }
        }




        //TODO task for shield particles

    }

    @Override
    public boolean usable(LivingEntity caster, LivingEntity target){

        if(caster == null){
            return false;
        }

        if(target != null){

            if(target instanceof Player){

                if(pvpManager.pvpLogic(caster, (Player) target)){
                    target = caster;
                }
            }

            if(pveChecker.pveLogic(target)){
                target = caster;
            }
        }

        if(target == null){
            target = caster;
        }

        if(caster.getLocation().getWorld() != target.getLocation().getWorld()){
            return false;
        }

        double distance = caster.getLocation().distance(target.getLocation());

        if(distance > range + statusEffectManager.getAdditionalRange(caster)){
            return false;
        }

        if(mana.getCurrentMana(caster) < cost){
            return false;
        }


        return cooldownManager.isReady(caster.getUniqueId(), 1, statusEffectManager.getHastePercent(caster));
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {

        if(mana.getCurrentMana(entity) < cost){
            return "\ue3d3";
        }

        return "\ue3d2";
    }
}
