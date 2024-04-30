package me.angeloo.mystica.Components.Abilities.Assassin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssassinBasic {

    private final Mystica main;


    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Boolean> evenOdd = new HashMap<>();
    private final Map<UUID, BukkitTask> basicRunning = new HashMap<>();

    private final Stealth stealth;
    private final Combo combo;
    private final DuelistsFrenzy duelistsFrenzy;

    public AssassinBasic(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        stealth = assassinAbilities.getStealth();
        combo = assassinAbilities.getCombo();
        duelistsFrenzy = assassinAbilities.getDuelistsFrenzy();
    }

    public void useBasic(Player player){

        if(getIfBasicRunning(player)){
            return;
        }

        combatManager.startCombatTimer(player);
        executeBasic(player);

    }

    private void executeBasic(Player player){

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(buffAndDebuffManager.getIfBasicInterrupt(player)){
                    this.cancel();
                    stopBasicRunning(player);
                    return;
                }

                basicStage(player);
                combatManager.startCombatTimer(player);
            }
        }.runTaskTimer(main, 0, 8);
        basicRunning.put(player.getUniqueId(), task);




    }

    private void basicStage(Player player){
        Location start = player.getLocation().clone();
        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        Location center = start.clone().add(direction.clone().multiply(3));

        BoundingBox hitBox = new BoundingBox(
                center.getX() - 4,
                center.getY() - 2,
                center.getZ() - 4,
                center.getX() + 4,
                center.getY() + 6,
                center.getZ() + 4
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


        if(targetToHit != null){
            targetManager.setPlayerTarget(player, targetToHit);

            Location playerLoc = player.getLocation().clone();
            Location targetLoc = targetToHit.getLocation();
            Vector targetDir = targetLoc.toVector().subtract(playerLoc.toVector());

            if(playerLoc!=targetLoc){
                Location warpLoc = targetLoc.add(targetDir.clone().normalize().multiply(-1.5));
                warpLoc.setDirection(targetDir);

                while (!warpLoc.getBlock().isPassable()){
                    warpLoc.add(0,.1,0);
                }

                if(player.isSneaking()){
                    player.teleport(warpLoc);
                }


            }


            boolean crit = damageCalculator.checkIfCrit(player, 0);
            double damage = damageCalculator.calculateDamage(player, targetToHit, "Physical", getSkillDamage(player), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, player));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, player);

            stealth.stealthBonusCheck(player, targetToHit);
            if(duelistsFrenzy.getFrenzy(player)){
                combo.addComboPoint(player);
            }
        }
        else{
            stopBasicRunning(player);
        }
    }

    private boolean getIfEvenOdd(Player player){
        return evenOdd.getOrDefault(player.getUniqueId(), false);
    }

    private boolean getIfBasicRunning(Player player){
        return basicRunning.containsKey(player.getUniqueId());
    }

    public void stopBasicRunning(Player player){
        if(basicRunning.containsKey(player.getUniqueId())){
            basicRunning.get(player.getUniqueId()).cancel();
            basicRunning.remove(player.getUniqueId());
        }
    }

    public double getSkillDamage(Player player){
        double level = profileManager.getAnyProfile(player).getStats().getLevel();
        return 14 + ((int)(level/3));
    }

}
