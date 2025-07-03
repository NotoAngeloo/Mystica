package me.angeloo.mystica.Utility.Logic;

import me.angeloo.mystica.Managers.DpsManager;
import me.angeloo.mystica.Managers.MysticaPartyManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Hud.DpsBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DamageBoardPlaceholders {

    private final Mystica main;
    private final MysticaPartyManager mysticaPartyManager;
    private final DpsManager dpsManager;
    private final DpsBar dpsBar;

    private final Map<UUID, Long> lastUpdate = new HashMap<>();

    private final Map<UUID, String> damageBar1 = new HashMap<>();
    private final Map<UUID, String> damagePlayer1 = new HashMap<>();
    private final Map<UUID, String> dps1 = new HashMap<>();
    private final Map<UUID, String> damageBar2 = new HashMap<>();
    private final Map<UUID, String> damagePlayer2 = new HashMap<>();
    private final Map<UUID, String> dps2 = new HashMap<>();
    private final Map<UUID, String> damageBar3 = new HashMap<>();
    private final Map<UUID, String> damagePlayer3 = new HashMap<>();
    private final Map<UUID, String> dps3 = new HashMap<>();
    private final Map<UUID, String> damageBar4 = new HashMap<>();
    private final Map<UUID, String> damagePlayer4 = new HashMap<>();
    private final Map<UUID, String> dps4 = new HashMap<>();
    private final Map<UUID, String> damageBar5 = new HashMap<>();
    private final Map<UUID, String> damagePlayer5 = new HashMap<>();
    private final Map<UUID, String> dps5 = new HashMap<>();

    public DamageBoardPlaceholders(Mystica main){
        this.main = main;
        mysticaPartyManager = main.getMysticaPartyManager();
        dpsManager = main.getDpsManager();
        dpsBar = new DpsBar(main);
    }

    private long timeSinceUpdate(Player player){
        long now = System.currentTimeMillis();
        if(!lastUpdate.containsKey(player.getUniqueId())){
            lastUpdate.put(player.getUniqueId(), now);
        }
        long last = lastUpdate.get(player.getUniqueId());

        return ((now-last) / 1000);
    }

    public void clearPlaceholders(Player player){
        damageBar1.put(player.getUniqueId(), " ");
        damageBar2.put(player.getUniqueId(), " ");
        damageBar2.put(player.getUniqueId(), " ");
        damageBar2.put(player.getUniqueId(), " ");
        damageBar2.put(player.getUniqueId(), " ");

        damagePlayer1.put(player.getUniqueId(), " ");
        damagePlayer2.put(player.getUniqueId(), " ");
        damagePlayer3.put(player.getUniqueId(), " ");
        damagePlayer4.put(player.getUniqueId(), " ");
        damagePlayer5.put(player.getUniqueId(), " ");

        dps1.put(player.getUniqueId(), " ");
        dps2.put(player.getUniqueId(), " ");
        dps3.put(player.getUniqueId(), " ");
        dps4.put(player.getUniqueId(), " ");
        dps5.put(player.getUniqueId(), " ");
    }

    public void updateDamageBoardValues(Player player){

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

        new BukkitRunnable(){
            @Override
            public void run(){
                if(mParty.size() < 1){

                    damageBar1.put(player.getUniqueId(), " ");
                    damageBar2.put(player.getUniqueId(), " ");
                    damageBar2.put(player.getUniqueId(), " ");
                    damageBar2.put(player.getUniqueId(), " ");
                    damageBar2.put(player.getUniqueId(), " ");

                    damagePlayer1.put(player.getUniqueId(), " ");
                    damagePlayer2.put(player.getUniqueId(), " ");
                    damagePlayer3.put(player.getUniqueId(), " ");
                    damagePlayer4.put(player.getUniqueId(), " ");
                    damagePlayer5.put(player.getUniqueId(), " ");

                    dps1.put(player.getUniqueId(), " ");
                    dps2.put(player.getUniqueId(), " ");
                    dps3.put(player.getUniqueId(), " ");
                    dps4.put(player.getUniqueId(), " ");
                    dps5.put(player.getUniqueId(), " ");

                    return;
                }

                mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

                if(mParty.size() > 0){
                    double damage1 = dpsManager.getRoundedDps(mParty.get(0));
                    if(damage1 == 0){
                        damageBar1.put(player.getUniqueId(), " ");
                        damagePlayer1.put(player.getUniqueId(), " ");
                        dps1.put(player.getUniqueId(), " ");
                    }
                    else{
                        damageBar1.put(player.getUniqueId(), dpsBar.getBar(mParty.get(0), dpsManager.getRoundedDps(mParty.get(0)), dpsManager.getRoundedDps(mParty.get(0))));
                        damagePlayer1.put(player.getUniqueId(), mParty.get(0).getName());
                        dps1.put(player.getUniqueId(), String.valueOf(dpsManager.getRoundedDps(mParty.get(0))));
                    }
                }

                if(mParty.size() > 1){
                    double damage2 = dpsManager.getRoundedDps(mParty.get(1));
                    if(damage2 == 0){
                        damageBar2.put(player.getUniqueId(), " ");
                        damagePlayer2.put(player.getUniqueId(), " ");
                        dps2.put(player.getUniqueId(), " ");
                    }
                    else{
                        damageBar2.put(player.getUniqueId(), dpsBar.getBar(mParty.get(1), dpsManager.getRoundedDps(mParty.get(0)), dpsManager.getRoundedDps(mParty.get(1))));
                        damagePlayer2.put(player.getUniqueId(), mParty.get(1).getName());
                        dps2.put(player.getUniqueId(), String.valueOf(dpsManager.getRoundedDps(mParty.get(1))));
                    }
                }

                if(mParty.size() > 2){
                    double damage3 = dpsManager.getRoundedDps(mParty.get(2));
                    if(damage3 == 0){
                        damageBar3.put(player.getUniqueId(), " ");
                        damagePlayer3.put(player.getUniqueId(), " ");
                        dps3.put(player.getUniqueId(), " ");
                    }
                    else{
                        damageBar3.put(player.getUniqueId(), dpsBar.getBar(mParty.get(2), dpsManager.getRoundedDps(mParty.get(0)), dpsManager.getRoundedDps(mParty.get(2))));
                        damagePlayer3.put(player.getUniqueId(), mParty.get(2).getName());
                        dps3.put(player.getUniqueId(), String.valueOf(dpsManager.getRoundedDps(mParty.get(2))));
                    }
                }

                if(mParty.size() > 3){
                    double damage4 = dpsManager.getRoundedDps(mParty.get(3));
                    if(damage4 == 0){
                        damageBar4.put(player.getUniqueId(), " ");
                        damagePlayer4.put(player.getUniqueId(), " ");
                        dps4.put(player.getUniqueId(), " ");
                    }
                    else{
                        damageBar4.put(player.getUniqueId(), dpsBar.getBar(mParty.get(3), dpsManager.getRoundedDps(mParty.get(0)), dpsManager.getRoundedDps(mParty.get(3))));
                        damagePlayer4.put(player.getUniqueId(), mParty.get(3).getName());
                        dps4.put(player.getUniqueId(), String.valueOf(dpsManager.getRoundedDps(mParty.get(3))));
                    }
                }

                if(mParty.size()>4){
                    double damage5 = dpsManager.getRoundedDps(mParty.get(4));
                    if(damage5 == 0){
                        damageBar5.put(player.getUniqueId(), " ");
                        damagePlayer5.put(player.getUniqueId(), " ");
                        dps5.put(player.getUniqueId(), " ");
                    }
                    else{
                        damageBar5.put(player.getUniqueId(), dpsBar.getBar(mParty.get(4), dpsManager.getRoundedDps(mParty.get(0)), dpsManager.getRoundedDps(mParty.get(4))));
                        damagePlayer5.put(player.getUniqueId(), mParty.get(4).getName());
                        dps5.put(player.getUniqueId(), String.valueOf(dpsManager.getRoundedDps(mParty.get(4))));
                    }
                }
            }
        }.runTaskAsynchronously(main);


    }

    public String getDamage_Bar_1(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 1){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(0));

        if(dps == 0){
            return " ";
        }

        double topDps = dpsManager.getRoundedDps(mParty.get(0));


        bar.append(dpsBar.getBar(mParty.get(0), topDps, dps));


        return String.valueOf(bar);*/

        return damageBar1.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDamagePlayer_1(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 1){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(0));

        if(dps == 0){
            return " ";
        }

        bar.append(mParty.get(0).getName());

        return String.valueOf(bar);*/
        return damagePlayer1.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDps_1(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 1){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(0));

        if(dps == 0){
            return " ";
        }

        bar.append((int)dps);

        return String.valueOf(bar);*/
        return dps1.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDamage_Bar_2(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 2){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(1));

        if(dps == 0){
            return " ";
        }

        double topDps = dpsManager.getRoundedDps(mParty.get(0));


        bar.append(dpsBar.getBar(mParty.get(1), topDps, dps));

        return String.valueOf(bar);*/
        return damageBar2.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDamagePlayer_2(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 2){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(1));

        if(dps == 0){
            return " ";
        }

        bar.append(mParty.get(1).getName());

        return String.valueOf(bar);*/
        return damagePlayer2.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDps_2(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 2){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(1));

        if(dps == 0){
            return " ";
        }

        bar.append((int)dps);

        return String.valueOf(bar);*/
        return dps2.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDamage_Bar_3(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 3){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(2));

        if(dps == 0){
            return " ";
        }

        double topDps = dpsManager.getRoundedDps(mParty.get(0));


        bar.append(dpsBar.getBar(mParty.get(2), topDps, dps));

        return String.valueOf(bar);*/
        return damageBar3.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDamagePlayer_3(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 3){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(2));

        if(dps == 0){
            return " ";
        }

        bar.append(mParty.get(2).getName());

        return String.valueOf(bar);*/
        return damagePlayer3.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDps_3(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 3){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(2));

        if(dps == 0){
            return " ";
        }

        bar.append((int)dps);

        return String.valueOf(bar);*/
        return dps3.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDamage_Bar_4(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 4){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(3));

        if(dps == 0){
            return " ";
        }

        double topDps = dpsManager.getRoundedDps(mParty.get(0));


        bar.append(dpsBar.getBar(mParty.get(3), topDps, dps));

        return String.valueOf(bar);*/

        return damageBar4.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDamagePlayer_4(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 4){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(3));

        if(dps == 0){
            return " ";
        }

        bar.append(mParty.get(3).getName());

        return String.valueOf(bar);*/

        return damagePlayer4.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDps_4(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 4){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(3));

        if(dps == 0){
            return " ";
        }

        bar.append((int)dps);

        return String.valueOf(bar);*/

        return dps4.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDamage_Bar_5(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 5){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(4));

        if(dps == 0){
            return " ";
        }

        double topDps = dpsManager.getRoundedDps(mParty.get(0));


        bar.append(dpsBar.getBar(mParty.get(4), topDps, dps));

        return String.valueOf(bar);*/
        return damageBar5.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDamagePlayer_5(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 5){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(4));

        if(dps == 0){
            return " ";
        }

        bar.append(mParty.get(4).getName());

        return String.valueOf(bar);*/
        return damagePlayer5.getOrDefault(player.getUniqueId(), " ");
    }

    public String getDps_5(Player player){

        /*StringBuilder bar = new StringBuilder();

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));
        mParty.sort(Comparator.comparingInt(dpsManager::getRoundedDps).reversed());

        if(mParty.size() < 5){
            return " ";
        }

        double dps = dpsManager.getRoundedDps(mParty.get(4));

        if(dps == 0){
            return " ";
        }

        bar.append((int)dps);

        return String.valueOf(bar);*/

        return dps5.getOrDefault(player.getUniqueId(), " ");
    }

}
