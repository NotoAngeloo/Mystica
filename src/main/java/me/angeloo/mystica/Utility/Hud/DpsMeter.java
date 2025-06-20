package me.angeloo.mystica.Utility.Hud;

import me.angeloo.mystica.Managers.DpsManager;
import me.angeloo.mystica.Managers.MysticaPartyManager;
import me.angeloo.mystica.Mystica;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;


public class DpsMeter {

    private final DpsBar dpsBar;
    private final MysticaPartyManager mysticaPartyManager;
    private final DpsManager dpsManager;

    private final Map<UUID, Scoreboard> damageMeters = new HashMap<>();

    public DpsMeter(Mystica main){
        dpsBar = new DpsBar(main);
        mysticaPartyManager = main.getMysticaPartyManager();
        dpsManager = main.getDpsManager();

    }

    public void innitMeter(Player player){


        ScoreboardManager manager = Bukkit.getScoreboardManager();

        assert manager != null;
        Scoreboard meter = manager.getNewScoreboard();

        innitObjectiveData(player, meter);

        damageMeters.put(player.getUniqueId(), meter);

        player.setScoreboard(meter);
    }

    public void updateMeter(Player player){

        if(!damageMeters.containsKey(player.getUniqueId())){
            innitMeter(player);
        }

        Scoreboard meter = damageMeters.get(player.getUniqueId());

        Set<Objective> objectives = meter.getObjectives();
        Objective meterObjective = null;

        for(Objective objective : objectives){
            meterObjective = objective;
            break;
        }

        if(meterObjective == null){
            return;
        }

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());
        List<LivingEntity> top5 = mParty.subList(0, Math.min(5, mParty.size()));
        double topDps = dpsManager.getRoundedDps(top5.get(0));


        int slot = 0;
        for(int i = 0; i<10; i++){

            Team team = meter.getTeam("line" + i + "_" + player.getUniqueId());
            String entry = ChatColor.values()[i].toString();
            assert team != null;

            if(top5.size()-1<slot){
                team.setPrefix("");
                team.setSuffix("");
                meterObjective.getScore(entry).setScore(10-i);
            }
            else{
                int dps = dpsManager.getRoundedDps(top5.get(slot));
                if(dps==0){
                    team.setPrefix("");
                    team.setSuffix("");
                    meterObjective.getScore(entry).setScore(10-i);
                }
                else{
                    if(i%2==0){
                        team.setPrefix("");
                        team.setSuffix(dpsBar.getBar(top5.get(slot), topDps, dps));
                        meterObjective.getScore(entry).setScore(10-i);
                    }
                    else{
                        team.setPrefix(" ");
                        team.setSuffix(top5.get(slot).getName() + " " + dps);
                        meterObjective.getScore(entry).setScore(10-i);
                    }
                }
            }

            if(i%2!=0){
                slot++;
            }

        }

        String bottomEntry = ChatColor.GREEN.toString();
        Team bottomTeam = meter.getTeam("bottom_" + player.getUniqueId());
        assert bottomTeam != null;
        bottomTeam.addEntry(bottomEntry);


        //space -17
        bottomTeam.setPrefix("\uF809\uF801");
        //offset + 15, uE04E png in middle
        bottomTeam.setSuffix("\uF828\uF827\uE04E\uF808\uF807");
        meterObjective.getScore(bottomEntry).setScore(0);

    }

    private void innitObjectiveData(Player player, Scoreboard meter){

        Objective objective = meter.registerNewObjective("dpsMeter_" + player.getName(), Criteria.DUMMY, "Damage Statistics");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());
        List<LivingEntity> top5 = mParty.subList(0, Math.min(5, mParty.size()));
        double topDps = dpsManager.getRoundedDps(top5.get(0));


        int slot = 0;
        for(int i = 0; i< 10; i++){

            String entry = ChatColor.values()[i].toString();
            Team team = meter.registerNewTeam("line" + i + "_" + player.getUniqueId());
            team.addEntry(entry);


            if(top5.size()-1< slot){

                team.setPrefix("");
                team.setSuffix("");
                objective.getScore(entry).setScore(10-i);
            }
            else{

                int dps = dpsManager.getRoundedDps(top5.get(slot));

                if(dps==0){
                    team.setPrefix("");
                    team.setSuffix("");
                    objective.getScore(entry).setScore(10-i);
                }
                else{
                    if(i%2==0){
                        team.setPrefix("");
                        team.setSuffix(dpsBar.getBar(top5.get(slot), topDps, dps));
                        objective.getScore(entry).setScore(10-i);
                    }
                    else{
                        team.setPrefix(" ");
                        team.setSuffix(top5.get(slot).getName() + " " + dps);
                        objective.getScore(entry).setScore(10-i);

                    }
                }

            }

            if(i%2!=0){
                slot++;
            }

        }

        String bottomEntry = ChatColor.GREEN.toString();
        Team bottomTeam = meter.registerNewTeam("bottom_" + player.getUniqueId());
        bottomTeam.addEntry(bottomEntry);


        //space -17
        bottomTeam.setPrefix("\uF809\uF801");
        //offset + 15, uE04E png in middle
        bottomTeam.setSuffix("\uF828\uF827\uE04E\uF808\uF807");
        objective.getScore(bottomEntry).setScore(0);

    }


}
