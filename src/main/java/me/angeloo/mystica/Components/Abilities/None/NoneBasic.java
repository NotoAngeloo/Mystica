package me.angeloo.mystica.Components.Abilities.None;

import me.angeloo.mystica.Components.Abilities.NoneAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoneBasic {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    private final Adrenaline adrenaline;

    private final Map<UUID, Boolean> basicReadyMap = new HashMap<>();


    public NoneBasic(Mystica main, AbilityManager manager, NoneAbilities noneAbilities){
        this.main = main;

        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();

        adrenaline = noneAbilities.getAdrenaline();
    }

    public void useBasic(LivingEntity caster){

        if(!basicReadyMap.containsKey(caster.getUniqueId())){
            basicReadyMap.put(caster.getUniqueId(), true);
        }


        if(!usable(caster)){
            return;
        }

        combatManager.startCombatTimer(caster);
        executeBasic(caster);

    }

    private void executeBasic(LivingEntity caster){

        basicReadyMap.put(caster.getUniqueId(), false);

        new BukkitRunnable(){
            @Override
            public void run(){

                basicReadyMap.put(caster.getUniqueId(), true);
            }
        }.runTaskLater(main, 5);


        Location start = caster.getLocation().clone();
        Vector direction = caster.getLocation().getDirection().setY(0).normalize();
        Location center = start.clone().add(direction.clone().multiply(3));

        BoundingBox hitBox = new BoundingBox(
                center.getX() - 2,
                center.getY() - 2,
                center.getZ() - 2,
                center.getX() + 2,
                center.getY() + 4,
                center.getZ() + 2
        );

        LivingEntity targetToHit = null;
        LivingEntity target = targetManager.getPlayerTarget(caster);
        LivingEntity firstHit = null;

        boolean targetHit = false;

        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(entity == caster){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) entity)){
                    continue;
                }
            }

            if(entity instanceof ArmorStand){
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;

            if(!(entity instanceof Player)){
                if(!pveChecker.pveLogic(livingEntity)){
                    continue;
                }
            }

            if(firstHit == null){
                firstHit = livingEntity;
            }

            if(target != null){
                if(livingEntity == target){
                    targetHit = true;
                    targetToHit = livingEntity;
                    break;
                }
            }
        }

        if(!targetHit && firstHit!= null){
            targetToHit = firstHit;
        }

        double skillDamage = 7;

        if(adrenaline.getIfBuffTime(caster)>0){
            skillDamage = 17;
        }

        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        skillDamage = skillDamage + ((int)(level/10));

        if(targetToHit != null){
            targetManager.setPlayerTarget(caster, targetToHit);


            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", skillDamage, crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster);

        }

    }

    public boolean usable(LivingEntity caster){
        if(!basicReadyMap.get(caster.getUniqueId())){
            return false;
        }

        return true;
    }

}
