package me.angeloo.mystica.Components.Abilities.None;

import io.lumine.shadow.ShadowingStrategy;
import me.angeloo.mystica.Components.Abilities.Assassin.Combo;
import me.angeloo.mystica.Components.Abilities.Assassin.DuelistsFrenzy;
import me.angeloo.mystica.Components.Abilities.Assassin.Stealth;
import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
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

    public void useBasic(Player player){

        if(!basicReadyMap.containsKey(player.getUniqueId())){
            basicReadyMap.put(player.getUniqueId(), true);
        }


        if(!basicReadyMap.get(player.getUniqueId())){
            return;
        }

        combatManager.startCombatTimer(player);
        executeBasic(player);

    }

    private void executeBasic(Player player){

        basicReadyMap.put(player.getUniqueId(), false);

        new BukkitRunnable(){
            @Override
            public void run(){

                basicReadyMap.put(player.getUniqueId(), true);
            }
        }.runTaskLater(main, 5);


        Location start = player.getLocation().clone();
        Vector direction = player.getLocation().getDirection().setY(0).normalize();
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
        LivingEntity target = targetManager.getPlayerTarget(player);
        LivingEntity firstHit = null;

        boolean targetHit = false;

        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

            if(entity == player){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) entity)){
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

        if(adrenaline.getIfBuffTime(player)>0){
            skillDamage = 17;
        }

        double level = profileManager.getAnyProfile(player).getStats().getLevel();
        skillDamage = skillDamage + ((int)(level/10));

        if(targetToHit != null){
            targetManager.setPlayerTarget(player, targetToHit);


            boolean crit = damageCalculator.checkIfCrit(player, 0);
            double damage = damageCalculator.calculateDamage(player, targetToHit, "Physical", skillDamage, crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, player));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, player);

        }


    }

}
