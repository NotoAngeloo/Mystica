package me.angeloo.mystica.Utility.Hud;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.LivingEntity;

import static me.angeloo.mystica.Mystica.*;

public class DpsBar {

    private final ProfileManager profileManager;

    public DpsBar(Mystica main){
        profileManager = main.getProfileManager();
    }

    public String getBar(LivingEntity entity, int top, int dps){

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

        bar.append("\uE04F");

        return String.valueOf(bar);

    }

}
