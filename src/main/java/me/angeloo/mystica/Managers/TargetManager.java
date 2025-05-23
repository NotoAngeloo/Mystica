package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.PveChecker;
import me.angeloo.mystica.Utility.StealthTargetBlacklist;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class TargetManager {

    private final MysticaPartyManager mysticaPartyManager;
    private final BossCastingManager bossCastingManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final StealthTargetBlacklist stealthTargetBlacklist;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final Map<UUID, LivingEntity> playerTarget = new HashMap<>();
    private final Map<UUID, BossBar> playerTargetBar = new HashMap<>();
    private final Map<UUID, BossBar> targetShieldBar = new HashMap<>();
    private final Map<UUID, BossBar> interruptBar = new HashMap<>();
    private final ProfileManager profileManager;

    private final Map<UUID, UUID> bossTarget = new HashMap<>();

    public TargetManager(Mystica main){
        bossCastingManager = main.getBossCastingManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        stealthTargetBlacklist = main.getStealthTargetBlacklist();
        pveChecker = main.getPveChecker();
        pvpManager = main.getPvpManager();
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
    }


    public LivingEntity getPlayerTarget(LivingEntity caster){

        if(!(caster instanceof Player)){
            return fakePlayerTargetManager.getTarget(caster);
        }

        return playerTarget.get(caster.getUniqueId());
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

            //if they are an enemy
            if(pveChecker.pveLogic(entity)){
                setBossTarget(player, entity);
            }

            if(entity.isDead()){
                removeAllBars(player);
            }

            if(!profileManager.getCompanions(player).isEmpty()){
                for(UUID companion : profileManager.getCompanions(player)){
                    //Bukkit.getLogger().info("suggesting");

                    LivingEntity livingEntity = (LivingEntity) Bukkit.getEntity(companion);

                    if(livingEntity == null){
                        continue;
                    }

                    fakePlayerTargetManager.suggestTarget(livingEntity, entity);
                }
            }
        }


    }

    public void setTargetToNearestValid(LivingEntity caster, double radius){


        if(!(caster instanceof Player)){
            fakePlayerTargetManager.setTargetToNearestValid(caster, radius);
            return;
        }

        Player player = (Player) caster;

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

                if(stealthTargetBlacklist.get(entityPlayer)){
                    continue;
                }

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

    public void setTeamTarget(Player player){

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMPartyMemberList(player));
        mParty.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth()/(double)profileManager.getAnyProfile(p).getTotalHealth()));
        setPlayerTarget(player, mParty.get(0));


    }

    private BossBar startTargetBar(Player player, LivingEntity entity){

        //Bukkit.getLogger().info(entity.getName());

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


        double maxHealth =  profileManager.getAnyProfile(entity).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(entity);
        double currentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();

        if(maxHealth<currentHealth){
            maxHealth = currentHealth;
        }

        bossBar.setProgress(currentHealth/maxHealth);

        //ok this should work
        if(entity instanceof Player){

            boolean deathStatus = profileManager.getAnyProfile(entity).getIfDead();

            if (deathStatus){
                bossBar.setProgress(0);
            }
        }

        if(!(entity instanceof Player)){
            if(profileManager.getAnyProfile(entity).fakePlayer()){
                boolean deathStatus = profileManager.getAnyProfile(entity).getIfDead();

                if (deathStatus){
                    bossBar.setProgress(0);
                }
            }
        }

        bossBar.addPlayer(player);
        bossBar.setVisible(true);

        if(buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(entity) > 0){
            startShieldBar(player, entity);
        }

        //check if entity is casting a skill
        if(bossCastingManager.getCastPercent(entity) > 0){
            startInterruptBar(player, entity);
        }

        return bossBar;
    }

    private void startInterruptBar(Player player, LivingEntity entity){

        BossBar interruptBar = Bukkit.createBossBar("",BarColor.PURPLE, BarStyle.SOLID);

        double castMax = bossCastingManager.getCastMax(entity);
        double castAmount = bossCastingManager.getCastPercent(entity);

        if(castAmount > castMax){
            castAmount = castMax;
        }

        interruptBar.setProgress(castAmount/castMax);
        interruptBar.addPlayer(player);
        interruptBar.setVisible(true);

        this.interruptBar.put(player.getUniqueId(), interruptBar);
    }

    private void startShieldBar(Player player, LivingEntity entity){

        BossBar shieldBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID);
        double maxHealth = profileManager.getAnyProfile(entity).getTotalHealth();
        double shieldAmount = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(entity);
        if(shieldAmount > maxHealth){
            shieldAmount = maxHealth;
        }

        shieldBar.setProgress(shieldAmount/maxHealth);
        shieldBar.addPlayer(player);
        shieldBar.setVisible(true);

        targetShieldBar.put(player.getUniqueId(), shieldBar);

    }

    public void removeAllBars(Player player){
        BossBar bossBar = playerTargetBar.get(player.getUniqueId());
        BossBar shieldBar = targetShieldBar.get(player.getUniqueId());
        BossBar interruptBar = this.interruptBar.get(player.getUniqueId());

        if(bossBar != null){
            bossBar.removePlayer(player);
            playerTargetBar.remove(player.getUniqueId());
        }

        if(shieldBar != null){
            shieldBar.removePlayer(player);
            targetShieldBar.remove(player.getUniqueId());
        }

        if(interruptBar != null){
            interruptBar.removePlayer(player);
            this.interruptBar.remove(player.getUniqueId());
        }

    }

    public Map<UUID, LivingEntity> getTargetMap(){
        return playerTarget;
    }

    public boolean isTargeting(LivingEntity caster, LivingEntity target){

        if(!(caster instanceof Player)){

            return false;
        }

        if(!playerTarget.containsKey(caster.getUniqueId())){
            return false;
        }

        return playerTarget.get(caster.getUniqueId()) == target;
    }

    private void setBossTarget(Player player, LivingEntity target){
        bossTarget.put(player.getUniqueId(), target.getUniqueId());
    }

    public UUID getBossTarget(Player player){

        /*if(bossTarget.containsKey(player.getUniqueId())){
            Entity entity = Bukkit.getEntity(bossTarget.get(player.getUniqueId()));

            if(entity == null || entity.isDead()){
                bossTarget.remove(player.getUniqueId());

                Bukkit.getLogger().info("removing boss target");

            }*/

        return bossTarget.getOrDefault(player.getUniqueId(), player.getUniqueId());
    }


}
