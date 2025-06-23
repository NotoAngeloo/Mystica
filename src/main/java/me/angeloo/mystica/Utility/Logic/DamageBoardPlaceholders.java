package me.angeloo.mystica.Utility.Logic;

import me.angeloo.mystica.Managers.DpsManager;
import me.angeloo.mystica.Managers.MysticaPartyManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Hud.DpsBar;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DamageBoardPlaceholders {

    private final MysticaPartyManager mysticaPartyManager;
    private final DpsManager dpsManager;
    private final DpsBar dpsBar;


    public DamageBoardPlaceholders(Mystica main){
        mysticaPartyManager = main.getMysticaPartyManager();
        dpsManager = main.getDpsManager();
        dpsBar = new DpsBar(main);
    }

    public String getDamage_Bar_1(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDamagePlayer_1(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDps_1(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDamage_Bar_2(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDamagePlayer_2(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDps_2(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDamage_Bar_3(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDamagePlayer_3(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDps_3(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDamage_Bar_4(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDamagePlayer_4(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDps_4(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDamage_Bar_5(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDamagePlayer_5(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

    public String getDps_5(Player player){

        StringBuilder bar = new StringBuilder();

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

        return String.valueOf(bar);
    }

}
