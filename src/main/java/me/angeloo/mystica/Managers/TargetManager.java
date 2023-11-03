package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TargetManager {

    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final Map<UUID, LivingEntity> playerTarget = new HashMap<>();
    private final Map<UUID, BossBar> playerTargetBar = new HashMap<>();
    private final ProfileManager profileManager;

    public TargetManager(Mystica main){
        pveChecker = main.getPveChecker();
        pvpManager = main.getPvpManager();
        profileManager = main.getProfileManager();
    }


    public LivingEntity getPlayerTarget(Player player){
        return playerTarget.get(player.getUniqueId());
    }

    public void updateTargetBar(Player player){

        LivingEntity target = playerTarget.get(player.getUniqueId());

        if(target == null){
            return;
        }

        setPlayerTarget(player, target);

    }

    public void setPlayerTarget(Player player, LivingEntity entity){

        if(entity instanceof ArmorStand){
            return;
        }

        playerTarget.put(player.getUniqueId(), entity);
        if(playerTargetBar.containsKey(player.getUniqueId())){
            removeAllBars(player);
        }

        if(entity != null){
            playerTargetBar.put(player.getUniqueId(), startTargetBar(player, entity));

            if(entity.isDead()){
                removeAllBars(player);
            }
        }

    }

    public void setTargetToNearestValid(Player player, double radius){

        LivingEntity target = getPlayerTarget(player);

        if(target != null){
            return;
        }


        BoundingBox boundingBox = new BoundingBox(
                player.getLocation().getX() - radius,
                player.getLocation().getY() - 2,
                player.getLocation().getZ() - radius,
                player.getLocation().getX() + radius,
                player.getLocation().getY() + 3,
                player.getLocation().getZ() + radius
        );


        double closestDistanceSquared = Double.MAX_VALUE;
        LivingEntity theClosestEntity = null;

        for(Entity entity : player.getWorld().getNearbyEntities(boundingBox)){

            if(entity == player){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;

            if(entity.isDead()){
                continue;
            }

            if(entity instanceof Player){
                double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());

                Player entityPlayer = (Player) entity;

                boolean deathStatus = profileManager.getAnyProfile(entityPlayer).getIfDead();

                if(deathStatus){
                    continue;
                }

                if(pvpManager.pvpLogic(player, entityPlayer)){
                    if(distanceSquared < closestDistanceSquared){
                        theClosestEntity = entityPlayer;
                        closestDistanceSquared = distanceSquared;
                    }
                }
                continue;
            }

            if (pveChecker.pveLogic(livingEntity)) {

                double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());

                if(distanceSquared < closestDistanceSquared){
                    theClosestEntity = livingEntity;
                    closestDistanceSquared = distanceSquared;
                }
            }

        }

        if(theClosestEntity != null){
            setPlayerTarget(player, theClosestEntity);
        }

    }

    private BossBar startTargetBar(Player player, LivingEntity entity){

        BossBar bossBar;
        int level;

        if(entity instanceof Player){

            level = profileManager.getAnyProfile(entity).getStats().getLevel();

            if(pvpManager.pvpLogic(player, (Player) entity)){
                bossBar = Bukkit.createBossBar(entity.getName() + " (" + level + ")", BarColor.RED, BarStyle.SOLID);
            }
            else{
                bossBar = Bukkit.createBossBar(entity.getName() + " (" + level + ")", BarColor.GREEN, BarStyle.SOLID);
            }

        }
        else{

            level = profileManager.getAnyProfile(entity).getStats().getLevel();

            if(pveChecker.pveLogic(entity)){
                bossBar = Bukkit.createBossBar(entity.getName() + " (" + level + ")", BarColor.RED, BarStyle.SOLID);
            }
            else {
                bossBar = Bukkit.createBossBar(entity.getName() + " (" + level + ")", BarColor.GREEN, BarStyle.SOLID);
            }

        }


        Double maxHealth = (double) profileManager.getAnyProfile(entity).getTotalHealth();
        Double currentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();
        bossBar.setProgress(currentHealth/maxHealth);

        //ok this should work
        if(entity instanceof Player){

            boolean deathStatus = profileManager.getAnyProfile(entity).getIfDead();

            if (deathStatus){
                bossBar.setProgress(0);
            }
        }

        bossBar.addPlayer(player);
        bossBar.setVisible(true);

        return bossBar;
    }

    public void removeAllBars(Player player){
        BossBar bossBar = playerTargetBar.get(player.getUniqueId());

        if(bossBar == null){
            return;
        }

        bossBar.removePlayer(player);
        playerTargetBar.remove(player.getUniqueId());
    }

    public Map<UUID, LivingEntity> getTargetMap(){
        return playerTarget;
    }
}
