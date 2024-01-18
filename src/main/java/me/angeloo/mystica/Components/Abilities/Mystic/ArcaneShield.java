package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
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
    private final PvpManager pvpManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Boolean> needToRemove = new HashMap<>();
    private final Map<UUID, BukkitTask> shieldTaskMap = new HashMap<>();

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ArcaneShield(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
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

            if(!(target instanceof Player)){
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

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player, target);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 1);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 1);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player, LivingEntity target){

        boolean shepard = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("shepard");

        if(!needToRemove.containsKey(target.getUniqueId())){
            needToRemove.put(target.getUniqueId(), false);
        }

        if(needToRemove.get(target.getUniqueId())){
            needToRemove.put(target.getUniqueId(), false);
        }

        int skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();

        double fivePercent = (profileManager.getAnyProfile(target).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(target)) / 20;
        double shieldAmount = fivePercent + (((double) profileManager.getAnyProfile(player).getTotalMagic() / 3) + skillLevel);

        buffAndDebuffManager.getGenericShield().applyOrAddShield(target, shieldAmount);


        int shieldDurationInTicks = 20*60;

        new BukkitRunnable(){
            @Override
            public void run(){
                buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(target, shieldAmount);
                needToRemove.put(target.getUniqueId(), true);
            }
        }.runTaskLater(main, shieldDurationInTicks);


        //TODO task for shield particles

        if(shepard){
            //task to heal them for as long as they have a shield
            double thirtyPercent = (profileManager.getAnyProfile(target).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(target)) * .3;

            if(shieldTaskMap.containsKey(target.getUniqueId())){
                shieldTaskMap.get(target.getUniqueId()).cancel();
            }

            BukkitTask task = new BukkitRunnable(){
                @Override
                public void run(){

                    boolean stillHasAShield = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(target) > 0;

                    if(!stillHasAShield || needToRemove.get(target.getUniqueId())){
                        this.cancel();
                        return;
                    }

                    changeResourceHandler.addHealthToEntity(target, thirtyPercent);

                    Location center = target.getLocation().clone().add(0,1,0);

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = center.getX() + (1 * Math.cos(angle));
                        double z = center.getZ() + (1 * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                        target.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                    }



                }
            }.runTaskTimer(main, 0, 20 * 20);

            shieldTaskMap.put(target.getUniqueId(), task);

        }

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
