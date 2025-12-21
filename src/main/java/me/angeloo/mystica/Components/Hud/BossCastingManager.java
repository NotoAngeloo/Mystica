package me.angeloo.mystica.Components.Hud;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossCastingManager {

    private final Mystica main;

    private final Map<UUID, BukkitTask> castTaskMap = new HashMap<>();
    private final Map<UUID, Double> castMax = new HashMap<>();
    private final Map<UUID, Double> castPercent = new HashMap<>();
    private final Map<UUID, Boolean> shouldInterrupt = new HashMap<>();

    public BossCastingManager(Mystica main){
        this.main = main;
    }

    public void startCastBar(LivingEntity entity, double speedPerTick, double maxAmount){

        castMax.put(entity.getUniqueId(), maxAmount);
        castPercent.put(entity.getUniqueId(), 1.0);
        shouldInterrupt.put(entity.getUniqueId(), false);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getIfShouldInterrupt(entity)){
                    interrupt();
                    return;
                }

                double currentPercent = getCastPercent(entity);

                if(currentPercent >= maxAmount){
                    interrupt_fail();
                    return;
                }


                castPercent.put(entity.getUniqueId(), currentPercent + speedPerTick);

                // send the data to all nearby players

                Bukkit.getScheduler().runTask(main,()->{
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(entity, BarType.WhomeverTarget));
                });



            }

            private void interrupt_fail(){
                this.cancel();
                castPercent.remove(entity.getUniqueId());
                shouldInterrupt.remove(entity.getUniqueId());
                castMax.remove(entity.getUniqueId());
                castTaskMap.remove(entity.getUniqueId());

                Bukkit.getScheduler().runTask(main,()->{
                    if(MythicBukkit.inst().getAPIHelper().isMythicMob(entity)){
                        AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getEntity();
                        MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).signalMob(abstractEntity, "interrupt_failed");
                    }
                });


            }

            private void interrupt(){
                this.cancel();
                castPercent.remove(entity.getUniqueId());
                shouldInterrupt.remove(entity.getUniqueId());
                castMax.remove(entity.getUniqueId());
                castTaskMap.remove(entity.getUniqueId());

                Bukkit.getScheduler().runTask(main, ()->{
                    if(MythicBukkit.inst().getAPIHelper().isMythicMob(entity)){
                        AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getEntity();
                        MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).signalMob(abstractEntity, "interrupted");
                    }
                });


            }



        }.runTaskTimerAsynchronously(main, 0,1);
        castTaskMap.put(entity.getUniqueId(), task);

    }

    public double getCastMax(LivingEntity entity){
        return castMax.getOrDefault(entity.getUniqueId(), 0.0);
    }

    public double getCastPercent(LivingEntity entity){
        return castPercent.getOrDefault(entity.getUniqueId(), 0.0);
    }

    public boolean getIfShouldInterrupt(LivingEntity entity){
        return shouldInterrupt.getOrDefault(entity.getUniqueId(), false);
    }

    public boolean bossIsCasting(LivingEntity entity){
        return castTaskMap.containsKey(entity.getUniqueId());
    }

    public void interrupt(LivingEntity caster, LivingEntity target){

        if(!bossIsCasting(target)){
            return;
        }

        BoundingBox hitBox = new BoundingBox(
                caster.getLocation().getX() - 20,
                caster.getLocation().getY() - 20,
                caster.getLocation().getZ() - 20,
                caster.getLocation().getX() + 20,
                caster.getLocation().getY() + 20,
                caster.getLocation().getZ() + 20
        );

        for (Entity thisEntity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(!(thisEntity instanceof Player player)){
                continue;
            }

            player.sendMessage(caster.getName() + " has successfully interrupted the ability!");
        }

        shouldInterrupt.put(target.getUniqueId(), true);

    }

}
