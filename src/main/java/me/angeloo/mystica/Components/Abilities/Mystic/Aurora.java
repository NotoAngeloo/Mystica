package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class Aurora {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Aurora(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
    }

    public void use(Player player){
        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        LivingEntity target = targetManager.getPlayerTarget(player);

        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

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

        if (abilityReadyInMap.get(player.getUniqueId()) > 0) {
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player, target);

        abilityReadyInMap.put(player.getUniqueId(), 21);
        new BukkitRunnable() {
            @Override
            public void run() {

                if (abilityReadyInMap.get(player.getUniqueId()) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0, 20);
    }

    private void  execute(Player player, LivingEntity target){

        boolean shepard = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("shepard");

        Location center = target.getLocation().clone();

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level_Bonus();

        double shieldAmount = (profileManager.getAnyProfile(player).getTotalHealth() + skillLevel) * .5;

        new BukkitRunnable(){
            final Set<LivingEntity> hitBySkill = new HashSet<>();
            Vector initialDirection;
            int angle = 0;
            int ran = 0;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = center.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);
                center.setDirection(rotation);

                double increment = (2 * Math.PI) / 16;

                for(int i = 0; i<9;i+=2){

                    for (int j = 0; j < 16; j++) {
                        double angle = j * increment;
                        double x = center.getX() + rotation.getX() + (i * Math.cos(angle));
                        double z = center.getZ() + rotation.getZ() + (i * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                        target.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0, 0, 0, 0);
                    }
                }


                if(ran%10==0){
                    BoundingBox hitBox = new BoundingBox(
                            center.getX() - 8,
                            center.getY() - 2,
                            center.getZ() - 8,
                            center.getX() + 8,
                            center.getY() + 4,
                            center.getZ() + 8
                    );

                    for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                        if(!(entity instanceof Player)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        Player thisPlayer = (Player) entity;

                        if (pvpManager.pvpLogic(player, thisPlayer)) {
                            continue;
                        }

                        if(shepard){
                            double healAmount = (profileManager.getAnyProfile(thisPlayer).getTotalHealth() + skillLevel) * .2;

                            if(damageCalculator.checkIfCrit(player, 0)){
                                healAmount = healAmount * 1.5;
                            }

                            changeResourceHandler.addHealthToEntity(thisPlayer, healAmount, player);
                        }

                        if(hitBySkill.contains(thisPlayer)){
                            continue;
                        }

                        hitBySkill.add(thisPlayer);

                        buffAndDebuffManager.getGenericShield().applyOrAddShield(thisPlayer, shieldAmount);

                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(thisPlayer, shieldAmount);
                            }
                        }.runTaskLater(main, 200);
                    }
                }


                if(ran >= 200){
                    this.cancel();
                }

                angle += 10;
                ran++;

            }


        }.runTaskTimer(main, 0, 1);

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}