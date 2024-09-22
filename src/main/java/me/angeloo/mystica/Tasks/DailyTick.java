package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.Managers.DailyEventManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DailyData;


import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;

public class DailyTick extends BukkitRunnable {

    private static final LocalTime DAILY_RESET = LocalTime.of(0, 0);

    private final DailyEventManager dailyEventManager;
    private final DailyData dailyData;

    public DailyTick(Mystica main){
        dailyEventManager = main.getDailyEventManager();
        dailyData = main.getDailyData();
    }

    @Override
    public void run(){
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);

        //once a day
        if(currentTime.equals(DAILY_RESET)){

            dailyData.decreaseDays();
            int daysTilIncrease = dailyData.getDaysTilIncrease();

            if(daysTilIncrease < 0 ){

                int oldMax = dailyData.getMaxLevel();
                dailyData.increaseMaxLevel();
                dailyData.resetDayClock(oldMax);

            }

            Bukkit.getServer().broadcastMessage(dailyData.getLevelAnnouncement());

        }

        //every even hour
        /*if(currentTime.getMinute()==0 && currentTime.getHour()%2==0){
            //demon portal spawn reset
            dailyEventManager.getDemonInvasion().spawnDemonPortals();
        }*/

        //other stuff on the hour
    }

}
