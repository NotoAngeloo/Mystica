package me.angeloo.mystica.Utility.Hud;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import static me.angeloo.mystica.Mystica.*;

public class DpsBar {

    private final ProfileManager profileManager;

    public DpsBar(Mystica main){
        profileManager = main.getProfileManager();
    }

    public String getBar(LivingEntity entity, double top, double dps){

        StringBuilder bar = new StringBuilder();

        String playerClass = profileManager.getAnyProfile(entity).getPlayerClass();

        switch (playerClass.toLowerCase()){
            case "assassin":{
                bar.append(ChatColor.of(assassinColor));
                break;
            }
            case "elementalist":{
                bar.append(ChatColor.of(elementalistColor));
                break;
            }
            case "mystic":{
                bar.append(ChatColor.of(mysticColor));
                break;
            }
            case "paladin":{
                bar.append(ChatColor.of(paladinColor));
                break;
            }
            case "ranger":{
                bar.append(ChatColor.of(rangerColor));
                break;
            }
            case "shadow knight":{
                bar.append(ChatColor.of(shadowKnightColor));
                break;
            }
            case "warrior":{
                bar.append(ChatColor.of(warriorColor));
                break;
            }
        }


        double ratio =  dps /  top;
        int amount = (int) Math.ceil(ratio * 10);

        if(amount <= 0){
            amount = 1;
        }

        if(amount >= 10){
            amount = 10;
        }

        //Bukkit.getLogger().info(entity.getName() + "top: " + top + " dps: " + dps +" ratio: "+ratio +  " amount: " + amount);

        switch (amount){
            case 10:{
                bar.append("\uE04F");
                break;
            }
            case 9:{
                bar.append("\uE050");
                break;
            }
            case 8:{
                bar.append("\uE051");
                break;
            }
            case 7:{
                bar.append("\uE052");
                break;
            }
            case 6:{
                bar.append("\uE053");
                break;
            }
            case 5:{
                bar.append("\uE055");
                break;
            }
            case 4:{
                bar.append("\uE056");
                break;
            }
            case 3:{
                bar.append("\uE057");
                break;
            }
            case 2:{
                bar.append("\uE05A");
                break;
            }
            case 1:{
                bar.append("\uE05B");
                break;
            }
        }



        return String.valueOf(bar);

    }


}
