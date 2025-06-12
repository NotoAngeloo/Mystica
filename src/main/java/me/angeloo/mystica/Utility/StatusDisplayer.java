package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Managers.TargetManager;
import net.md_5.bungee.api.ChatColor;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.awt.*;

import static me.angeloo.mystica.Mystica.*;

public class StatusDisplayer {

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;

    public StatusDisplayer(Mystica main, AbilityManager manager) {
        profileManager = main.getProfileManager();
        abilityManager = manager;
    }


    private String getCastStatus(Player player){

        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        StringBuilder statusString = new StringBuilder();

        statusString.append("   ");


        if(abilityManager.getIfCasting(player)){
            Color color = new Color(0,0,0);

            switch (clazz.toLowerCase()){
                case "ranger":{
                    color = rangerColor;
                    break;
                }
                case "mystic":{
                    color = mysticColor;
                    break;
                }
                case "shadow knight":{
                    color = shadowKnightColor;
                    break;
                }
                case "paladin":{
                    color = paladinColor;
                    break;
                }case "warrior":{
                    color = warriorColor;
                    break;
                }
                case "none":{
                    color = Color.WHITE;
                    break;
                }
            }

            statusString.append(ChatColor.of(color)).append("[");

            double percent = abilityManager.getCastPercent(player);

            for(int i = 0; i<10; i++){
                if(percent >= (i*10)){
                    statusString.append("||");
                }
                else{
                    statusString.append(" ");
                }
            }

            statusString.append("]");
        }
        else{
            statusString.append("            ");
        }

        statusString.append(ChatColor.RESET).append("   ");

        return String.valueOf(statusString);
    }




}
