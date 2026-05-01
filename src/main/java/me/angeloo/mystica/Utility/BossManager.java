package me.angeloo.mystica.Utility;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.angeloo.mystica.Utility.Enums.BarType.SelfInfo;
import static me.angeloo.mystica.Utility.Enums.BarType.Target;

public class BossManager {

    private final Mystica main;
    private final ProfileManager profileManager;

    private final Map<UUID, BukkitTask> furyTasks = new HashMap<>();

    private final Map<UUID, Integer> bossFuryMaxDuration = new HashMap<>();
    private final Map<UUID, Integer> bossFuryCurrentCount = new HashMap<>();

    private final Map<UUID, Location> bossHomes = new HashMap<>();
    private final Map<UUID, Boolean> resetProcessing = new HashMap<>();

    public BossManager(Mystica main, ProfileManager profileManager){
        this.main = main;
        this.profileManager = profileManager;
    }

    public void startFuryTimer(UUID uuid, int time){

        if(furyTasks.containsKey(uuid)){
            furyTasks.get(uuid).cancel();
        }

        LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);

        if(entity == null){
            return;
        }

        bossFuryMaxDuration.put(uuid, time);
        bossFuryCurrentCount.put(uuid, 0);

        BukkitTask furyTask = new BukkitRunnable(){
            @Override
            public void run(){

                //check death here too
                if(entity.isDead()){
                    this.cancel();
                    return;
                }

                int current = bossFuryCurrentCount.get(uuid);

                if(current>=bossFuryMaxDuration.get(uuid)){
                    bossFuryCurrentCount.put(uuid, bossFuryMaxDuration.get(uuid));

                    //maybe i dont need this??
                    this.cancel();

                    Bukkit.getScheduler().runTask(main, ()->{
                        if(MythicBukkit.inst().getAPIHelper().isMythicMob(uuid)){
                            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(Bukkit.getEntity(uuid)).getEntity();
                            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(Bukkit.getEntity(uuid)).signalMob(abstractEntity, "fury");
                        }
                    });
                    return;
                }

                bossFuryCurrentCount.put(uuid, current + 1);

                Bukkit.getScheduler().runTask(main, ()->{
                    Bukkit.getPluginManager().callEvent(new HudUpdateEvent(entity, BarType.SelfInfo));
                });

            }
        }.runTaskTimerAsynchronously(main, 20, 20);

        furyTasks.put(uuid, furyTask);

    }



    public void resetBoss(UUID uuid){

        boolean isBoss = bossHomes.containsKey(uuid);

        if(!isBoss){
            return;
        }

        //Bukkit.getLogger().info("reseting boss");

        Entity entity = Bukkit.getEntity(uuid);

        LivingEntity boss = (LivingEntity) entity;

        Location home = bossHomes.get(uuid);

        assert boss != null;

        processReset(boss);

        if(furyTasks.containsKey(boss.getUniqueId())){
            furyTasks.get(boss.getUniqueId()).cancel();
        }

        furyTasks.remove(boss.getUniqueId());
        bossFuryMaxDuration.remove(boss.getUniqueId());
        bossFuryCurrentCount.remove(boss.getUniqueId());

        Profile profile = profileManager.getNonPlayerProfileFromUUID(uuid);
        double maxHealth = profile.getTotalHealth();
        profile.setCurrentHealth(maxHealth);
        AttributeInstance maxHealthAttribute = boss.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttribute != null;
        boss.setHealth(maxHealthAttribute.getBaseValue());

        boss.setAI(false);
        boss.teleport(home);

        if(MythicBukkit.inst().getAPIHelper().isMythicMob(uuid)){

            new BukkitRunnable(){
                @Override
                public void run(){

                    if(!getIfResetProcessing(boss)){
                        this.cancel();
                        return;
                    }

                    Entity entity = Bukkit.getEntity(uuid);

                    if(entity == null){
                        this.cancel();
                        return;
                    }

                    LivingEntity boss = (LivingEntity) entity;

                    if(home.getWorld() == null){
                        this.cancel();
                        return;
                    }

                    AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).getEntity();
                    boss.teleport(home);
                    MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).signalMob(abstractEntity, "reset");


                }
            }.runTaskTimer(main, 0, 2);

        }

        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(boss, SelfInfo));

    }

    public void setBossHome(UUID uuid){
        Entity entity = Bukkit.getEntity(uuid);

        LivingEntity boss = (LivingEntity) entity;

        assert boss != null;
        Location home = boss.getLocation();

        bossHomes.put(uuid, home);

    }

    public boolean getIfEntityIsBoss(UUID uuid){return bossHomes.containsKey(uuid);}

    private void processReset(LivingEntity entity){
        resetProcessing.put(entity.getUniqueId(), true);

        new BukkitRunnable(){
            @Override
            public void run(){
                resetProcessing.remove(entity.getUniqueId());
            }
        }.runTaskLaterAsynchronously(main, 80);
    }

    public boolean getIfResetProcessing(LivingEntity entity){
        return resetProcessing.getOrDefault(entity.getUniqueId(), false);
    }


    public int getMaxFuryDuration(UUID uuid){
        return bossFuryMaxDuration.getOrDefault(uuid, 0);
    }

    public int getCurrentFuryCount(UUID uuid){
        return bossFuryCurrentCount.getOrDefault(uuid, 0);
    }

    public boolean hasFuryTimer(UUID uuid){
        return bossFuryMaxDuration.containsKey(uuid);
    }

}
