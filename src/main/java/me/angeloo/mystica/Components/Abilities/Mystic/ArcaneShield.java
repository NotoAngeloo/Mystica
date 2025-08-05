package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ArcaneShield {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PveChecker pveChecker;
    private final PvpManager pvpManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Mana mana;
    private final Consolation consolation;

    private final Map<UUID, Boolean> needToRemove = new HashMap<>();
    private final Map<UUID, BukkitTask> shieldTaskMap = new HashMap<>();
    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ArcaneShield(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pveChecker = main.getPveChecker();
        pvpManager = main.getPvpManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        consolation = mysticAbilities.getConsolation();
        mana = mysticAbilities.getMana();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    private final double range = 20;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
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

        mana.subTractManaFromEntity(caster, getCost());

        execute(caster, target);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 5);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 1);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 1);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster, LivingEntity target){

        Set<LivingEntity> targetList = new HashSet<>();
        targetList.add(target);


        boolean shepard = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Shepard);

        if(shepard){

            if(target instanceof Player){
                if(consolation.getTargets(caster).contains(target)){
                    targetList.addAll(consolation.getTargets(caster));
                    consolation.removeTargets(caster);
                }
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

            double fivePercent = (profileManager.getAnyProfile(thisTarget).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(thisTarget)) / 20;
            double shieldAmount = fivePercent + (((double) profileManager.getAnyProfile(caster).getTotalAttack() / 3) + skillLevel);

            buffAndDebuffManager.getGenericShield().applyOrAddShield(thisTarget, shieldAmount);

            int shieldDurationInTicks = 20*60;

            new BukkitRunnable(){
                @Override
                public void run(){
                    buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(thisTarget, shieldAmount);
                    needToRemove.put(thisTarget.getUniqueId(), true);
                }
            }.runTaskLater(main, shieldDurationInTicks);


            if(shepard){
                //task to heal them for as long as they have a shield
                double thirtyPercent = (profileManager.getAnyProfile(thisTarget).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(thisTarget)) * .3;

                if(shieldTaskMap.containsKey(thisTarget.getUniqueId())){
                    shieldTaskMap.get(thisTarget.getUniqueId()).cancel();
                }

                BukkitTask task = new BukkitRunnable(){
                    @Override
                    public void run(){


                        boolean stillHasAShield = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(thisTarget) > 0;

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

    public int getCost(){return 25;}

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

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

        if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
            return false;
        }

        if(getCooldown(caster) > 0){
            return false;
        }


        if(mana.getCurrentMana(caster)<getCost()){
            return false;
        }

        return true;
    }

    public int returnWhichItem(Player player){

        if(mana.getCurrentMana(player)<getCost()){
            return 10;
        }

        return 0;
    }

}
