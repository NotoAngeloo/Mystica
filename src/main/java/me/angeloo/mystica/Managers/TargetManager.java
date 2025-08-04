package me.angeloo.mystica.Managers;

import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Logic.StealthTargetBlacklist;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class TargetManager {

    private final GravestoneManager gravestoneManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final StealthTargetBlacklist stealthTargetBlacklist;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final Map<UUID, LivingEntity> playerTarget = new HashMap<>();


    private final ProfileManager profileManager;

    private final Map<UUID, UUID> bossTarget = new HashMap<>();

    public TargetManager(Mystica main){
        gravestoneManager = main.getGravestoneManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
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

    public void setPlayerTarget(Player player, LivingEntity entity){

        if(entity instanceof ArmorStand){

            if(!gravestoneManager.isGravestone(entity)){
                return;
            }
        }

        playerTarget.put(player.getUniqueId(), entity);

        if(entity != null){

            //if they are an enemy
            if(pveChecker.pveLogic(entity)){
                setBossTarget(player, entity);
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

        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Target, true));

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

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingDouble(p -> profileManager.getAnyProfile(p).getCurrentHealth()/(double)profileManager.getAnyProfile(p).getTotalHealth()));
        setPlayerTarget(player, mParty.get(0));


    }



    private void startInterruptBar(Player player, LivingEntity entity){

        /*BossBar interruptBar = Bukkit.createBossBar("",BarColor.PURPLE, BarStyle.SOLID);

        double castMax = bossCastingManager.getCastMax(entity);
        double castAmount = bossCastingManager.getCastPercent(entity);

        if(castAmount > castMax){
            castAmount = castMax;
        }

        interruptBar.setProgress(castAmount/castMax);
        interruptBar.addPlayer(player);
        interruptBar.setVisible(true);

        this.interruptBar.put(player.getUniqueId(), interruptBar);*/
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
        return bossTarget.getOrDefault(player.getUniqueId(), player.getUniqueId());
    }




}
