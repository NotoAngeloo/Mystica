package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.PveChecker;
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
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){

            if(target instanceof Player){
                if(pvpManager.pvpLogic(player, (Player) target)){
                    target = player;
                }
            }

            if(pveChecker.pveLogic(target)){
                target = player;
            }
        }

        if(target == null){
            target = player;
        }

        double distance = player.getLocation().distance(target.getLocation());

        if(distance > totalRange){
            return;
        }

        if(getCooldown(player) > 0){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player, target);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 1);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 1);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private void execute(Player player, LivingEntity target){

        Set<LivingEntity> targetList = new HashSet<>();
        targetList.add(target);


        boolean shepard = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("shepard");

        if(shepard){

            if(target instanceof Player){
                if(consolation.getTargets(player).contains(target)){
                    targetList.addAll(consolation.getTargets(player));
                    consolation.removeTargets(player);
                }
            }


        }

        int skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();


        for(LivingEntity thisTarget : targetList){
            if(!needToRemove.containsKey(thisTarget.getUniqueId())){
                needToRemove.put(thisTarget.getUniqueId(), false);
            }

            if(needToRemove.get(thisTarget.getUniqueId())){
                needToRemove.put(thisTarget.getUniqueId(), false);
            }

            double fivePercent = (profileManager.getAnyProfile(thisTarget).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(thisTarget)) / 20;
            double shieldAmount = fivePercent + (((double) profileManager.getAnyProfile(player).getTotalAttack() / 3) + skillLevel);

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

                        changeResourceHandler.addHealthToEntity(thisTarget, thirtyPercent, player);

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

    public double getCost(){return 5;}

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
