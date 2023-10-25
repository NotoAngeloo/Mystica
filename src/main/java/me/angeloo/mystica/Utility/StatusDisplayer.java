package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import net.md_5.bungee.api.ChatColor;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StatusDisplayer {

    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final AbilityManager abilityManager;

    private final Map<Player, Integer> statusAmount = new HashMap<>();

    public StatusDisplayer(Mystica main, AbilityManager manager) {
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        abilityManager = manager;
    }

    public void displayStatus(Player player) {

        statusAmount.put(player, 0);


        StringBuilder statusString = new StringBuilder();



        String bigStatus = getBigStatus(player);

        String colorlessClassStatus = getClassStatus(player).replaceAll("§.", "");
        String colorlessBonusStatus = getBonusStatus(player).replaceAll("§.", "");

        int amountStatusChar = colorlessClassStatus.length() + colorlessBonusStatus.length();

        StringBuilder centeringStatus = new StringBuilder();

        for(int i = 0; i<amountStatusChar; i++){
            centeringStatus.append(" ");
        }

        statusString.append(centeringStatus).append(bigStatus).append(getClassStatus(player)).append(getBonusStatus(player));


        player.sendTitle("", String.valueOf(statusString), 0, 4, 0);
    }

    private String getBigStatus(Player player){

        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        StringBuilder statusString = new StringBuilder();

        statusString.append("   ");

        switch (clazz.toLowerCase()){
            case "ranger":{
                if(!abilityManager.getRangerAbilities().getIfCasting(player)){
                    break;
                }


                //how do i keep big status centered
                statusString.append(ChatColor.of(new Color(34, 111, 80))).append("[");

                double percent = abilityManager.getRangerAbilities().getCastPercent(player);

                for(int i = 0; i<10; i++){
                    if(percent >= (i*10)){
                        statusString.append("||");
                    }
                    else{
                        statusString.append(" ");
                    }
                }

                statusString.append("]");
                break;
            }
            default:{
                statusString.append("    ");
                break;
            }
        }

        statusString.append("   ");

        return String.valueOf(statusString);
    }

    private String getClassStatus(Player player){

        StringBuilder statusString = new StringBuilder();

        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                statusString.append(applyElementalistStatuses(player));
                break;
            }
            case "ranger":{
                statusString.append(applyRangerStatus(player));
                break;
            }
        }

        return String.valueOf(statusString);
    }

    private String applyElementalistStatuses(Player player) {

        StringBuilder statusString = new StringBuilder();

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if (statusAmount.get(player) >= 4) {
            return String.valueOf(statusString);
        }

        //TODO: replace these with unicode
        if (subclass.equalsIgnoreCase("pyromancer")) {
            int inflame = abilityManager.getElementalistAbilities().getFieryWing().getInflame(player);

            if (inflame > 0) {
                statusString.append(ChatColor.of(new Color(250, 102, 0))).append(inflame);
                statusAmount.put(player, statusAmount.get(player) + 1);
            }

        }

        if (statusAmount.get(player) >= 4) {
            return String.valueOf(statusString);
        }

        int breathTime = abilityManager.getElementalistAbilities().getElementalBreath().getIfBuffTime(player);

        if (breathTime > 0) {
            statusString.append(ChatColor.of(new Color(153, 204, 255))).append(breathTime);
            statusAmount.put(player, statusAmount.get(player) + 1);
        }

        return String.valueOf(statusString);
    }

    private String applyRangerStatus(Player player){
        StringBuilder statusString = new StringBuilder();

        if (statusAmount.get(player) >= 4) {
            return String.valueOf(statusString);
        }

        int cry = abilityManager.getRangerAbilities().getRallyingCry().getIfBuffTime(player);

        if(cry > 0){
            statusString.append(ChatColor.of(new Color(34,111,80))).append(cry);
            statusAmount.put(player, statusAmount.get(player) + 1);
        }

        return String.valueOf(statusString);
    }

    private String getBonusStatus(Player player){

        StringBuilder statusString = new StringBuilder();

        //wild roar
        if(buffAndDebuffManager.getWildRoarBuff().getIfWildRoarBuff(player) && statusAmount.get(player) < 4){
            statusAmount.put(player, statusAmount.get(player) + 1);
            //icon later
            statusString.append("R");
        }

        //speed up
        if(buffAndDebuffManager.getSpeedUp().getIfSpeedUp(player) && statusAmount.get(player) < 4){
            statusAmount.put(player, statusAmount.get(player) + 1);
            statusString.append("s");
        }

        return String.valueOf(statusString);

    }

}
