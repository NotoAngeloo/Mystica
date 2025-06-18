package me.angeloo.mystica.Utility.Hud;

import me.angeloo.mystica.Managers.DpsManager;
import me.angeloo.mystica.Managers.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;


public class DpsMeter {

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final DpsManager dpsManager;

    private final Map<UUID, Scoreboard> damageMeters = new HashMap<>();

    public DpsMeter(Mystica main){
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        dpsManager = main.getDpsManager();

    }

    public void innitMeter(Player player){


        ScoreboardManager manager = Bukkit.getScoreboardManager();

        assert manager != null;
        Scoreboard meter = manager.getNewScoreboard();

        Objective objective = meter.registerNewObjective("dpsMeter_" + player.getName(), Criteria.DUMMY, "Damage Statistics");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);


        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps));
        List<LivingEntity> top5 = mParty.subList(0, Math.min(5, mParty.size()));

        //TODO:each player gets 2 rows, one for name, another for dps

        int slot = 0;
        for(int i = 0; i<5; i++){

            String entry = ChatColor.values()[i].toString();
            Team team = meter.registerNewTeam("line" + i + "_" + player.getUniqueId());
            team.addEntry(entry);


            if(top5.size()-1<i){

                team.setPrefix("test");
                team.setSuffix("");
                objective.getScore(entry).setScore(5-i);

                continue;
            }

            team.setPrefix("");
            team.setSuffix(top5.get(slot).getName());
            objective.getScore(entry).setScore(5-i);
            slot++;
        }

        String bottomEntry = ChatColor.GOLD.toString();
        Team bottomTeam = meter.registerNewTeam("line 6_" + player.getUniqueId());
        bottomTeam.addEntry(bottomEntry);
        //space -17
        bottomTeam.setPrefix("\uF809\uF801");
        //offset + 15, uE04E png in middle
        bottomTeam.setSuffix("\uF828\uF827\uE04E\uF808\uF807");
        //offset + 16, uE04E png in middle
        //bottomTeam.setPrefix("\uF829\uE04E\uF809");
        //bottomTeam.setSuffix("\uE04E");
        objective.getScore(bottomEntry).setScore(0);

        /*int i = 0;
        for(LivingEntity entity : top5){
            String entry = ChatColor.values()[i].toString();
            Team team = meter.registerNewTeam("line" + i + "_" + player.getUniqueId());
            team.addEntry(entry);


            team.setPrefix("");
            team.setSuffix(top5.get(i).getName());
            objective.getScore(entry).setScore(dpsManager.getRoundedDps(entity));
            i++;
        }*/

        //"\uE04E"

        damageMeters.put(player.getUniqueId(), meter);

    }

    public void updateMeter(Player player){

        if(!damageMeters.containsKey(player.getUniqueId())){
            innitMeter(player);
        }

        Scoreboard meter = damageMeters.get(player.getUniqueId());



        player.setScoreboard(meter);
    }

}
