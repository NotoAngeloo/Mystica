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



        setObjectiveData(player, meter);

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

        meterObjective.unregister();
        setObjectiveData(player, meter);

        //player.setScoreboard(meter);
    }

    private void setObjectiveData(Player player, Scoreboard meter){

        Objective objective = meter.registerNewObjective("dpsMeter_" + player.getName(), Criteria.DUMMY, "Damage Statistics");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps));
        List<LivingEntity> top5 = mParty.subList(0, Math.min(5, mParty.size()));
        int topDps = dpsManager.getRoundedDps(top5.get(0));


        int slot = 0;
        for(int i = 0; i< 10; i++){

            String entry = ChatColor.values()[i].toString();

            Team team;

            if(meter.getTeam("line" + i + "_" + player.getUniqueId()) == null){
                team = meter.registerNewTeam("line" + i + "_" + player.getUniqueId());
                team.addEntry(entry);
            }
            else{
                team = meter.getTeam("line" + i + "_" + player.getUniqueId());

            }

            assert team != null;

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
        Team bottomTeam;

        if(meter.getTeam("bottom_" + player.getUniqueId()) == null){
            bottomTeam = meter.registerNewTeam("bottom_" + player.getUniqueId());
            bottomTeam.addEntry(bottomEntry);
        }
        else{
            bottomTeam = meter.getTeam("bottom_" + player.getUniqueId());
        }



        //space -17
        assert bottomTeam != null;
        bottomTeam.setPrefix("\uF809\uF801");
        //offset + 15, uE04E png in middle
        bottomTeam.setSuffix("\uF828\uF827\uE04E\uF808\uF807");
        objective.getScore(bottomEntry).setScore(0);

    }


}
