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

    public StatusDisplayer(Mystica main, AbilityManager manager) {
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        abilityManager = manager;
    }

    public void displayStatus(Player player) {

        if(!profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        StringBuilder statusString = new StringBuilder();

        String bigStatus = getCastStatus(player);

        String colorlessClassStatus = getClassStatus(player).replaceAll("§.", "");
        String colorlessBonusStatus = getBonusStatus(player).replaceAll("§.", "");

        int amountStatusChar = colorlessClassStatus.length() + colorlessBonusStatus.length();

        StringBuilder centeringStatus = new StringBuilder();

        for(int i = 0; i<amountStatusChar; i++){
            centeringStatus.append(" ");
        }

        statusString.append(centeringStatus).append(bigStatus).append(getClassStatus(player)).append(getBonusStatus(player));



        player.sendTitle(getBigClassStatus(player), String.valueOf(statusString), 0, 200, 0);
    }

    public void clearPlayerStatus(Player player){
        player.sendTitle("", "", 0, 2, 0);
    }

    private String getBigClassStatus(Player player){

        String clazz = profileManager.getAnyProfile(player).getPlayerClass();
        String subClass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (clazz.toLowerCase()){
            case "assassin":{

                StringBuilder comboString = new StringBuilder();

                int maxCombo = 5;

                if(subClass.equalsIgnoreCase("duelist")){
                    maxCombo = 6;
                }

                int combo = abilityManager.getAssassinAbilities().getCombo().getComboPoints(player);

                for(int i=0;i<maxCombo;i++){

                    if(i>=combo){
                        comboString.append("\uE008");
                    }
                    else{
                        comboString.append("\uE009");
                    }

                }

                return String.valueOf(comboString);
            }
            case "mystic":{

                if(!subClass.equalsIgnoreCase("chaos")){
                    return "";
                }

                StringBuilder shardString = new StringBuilder();

                int shards = abilityManager.getMysticAbilities().getEvilSpirit().getChaosShards(player);

                switch (shards){
                    case 0:{
                        shardString.append("\uE00A").append("\uE00B").append("\uE00C").append("\uE00D").append("\uE00E").append("\uE00F");
                        break;
                    }
                    case 1:{
                        shardString.append("\uE010").append("\uE00B").append("\uE00C").append("\uE00D").append("\uE00E").append("\uE00F");
                        break;
                    }
                    case 2:{
                        shardString.append("\uE010").append("\uE011").append("\uE00C").append("\uE00D").append("\uE00E").append("\uE00F");
                        break;
                    }
                    case 3:{
                        shardString.append("\uE010").append("\uE011").append("\uE012").append("\uE00D").append("\uE00E").append("\uE00F");
                        break;
                    }
                    case 4:{
                        shardString.append("\uE010").append("\uE011").append("\uE012").append("\uE013").append("\uE00E").append("\uE00F");
                        break;
                    }
                    case 5:{
                        shardString.append("\uE010").append("\uE011").append("\uE012").append("\uE013").append("\uE014").append("\uE00F");
                        break;
                    }
                    case 6:{
                        shardString.append("\uE010").append("\uE011").append("\uE012").append("\uE013").append("\uE014").append("\uE015");
                        break;
                    }
                }

                return String.valueOf(shardString);
            }
        }

        return "";
    }

    private String getCastStatus(Player player){

        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        StringBuilder statusString = new StringBuilder();

        statusString.append("   ");


        if(abilityManager.getIfCasting(player)){
            Color color = new Color(0,0,0);

            switch (clazz.toLowerCase()){
                case "ranger":{
                    color = new Color(34, 111, 80);
                    break;
                }
                case "mystic":{
                    color = new Color(155, 120, 197);
                    break;
                }
                case "shadow knight":{
                    color = new Color(213, 33, 3);
                    break;
                }
                case "paladin":{
                    color = new Color(207, 214, 61);
                    break;
                }case "warrior":{
                    color = new Color(214, 126, 61);
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

        if (subclass.equalsIgnoreCase("pyromancer")) {
            int inflame = abilityManager.getElementalistAbilities().getFieryWing().getInflame(player);

            if (inflame > 0) {

                switch (inflame){
                    case 1:{
                        statusString.append("\uE000");
                        break;
                    }
                    case 2:{
                        statusString.append("\uE001");
                        break;
                    }
                    case 3:{
                        statusString.append("\uE002");
                        break;
                    }
                }

            }

        }

        int breathTime = abilityManager.getElementalistAbilities().getElementalBreath().getIfBuffTime(player);

        if (breathTime > 0) {
            statusString.append("\uE004");
        }

        return String.valueOf(statusString);
    }

    private String applyRangerStatus(Player player){
        StringBuilder statusString = new StringBuilder();

        int cry = abilityManager.getRangerAbilities().getRallyingCry().getIfBuffTime(player);

        if(cry > 0){
            statusString.append("\uE006");
        }

        return String.valueOf(statusString);
    }

    private String getBonusStatus(Player player){

        StringBuilder statusString = new StringBuilder();

        //wild roar
        if(buffAndDebuffManager.getWildRoarBuff().getIfWildRoarBuff(player)){
            statusString.append("\uE007");
        }

        if(buffAndDebuffManager.getConjuringForceBuff().getIfConjForceBuff(player)){
            statusString.append("\uE005");
        }

        //speed up
        if(buffAndDebuffManager.getSpeedUp().getIfSpeedUp(player)){
            statusString.append("\uE003");
        }

        return String.valueOf(statusString);

    }

}
