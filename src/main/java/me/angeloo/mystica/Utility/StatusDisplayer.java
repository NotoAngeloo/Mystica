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
import java.util.HashMap;
import java.util.Map;

import static me.angeloo.mystica.Mystica.*;

public class StatusDisplayer {

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final AbilityManager abilityManager;
    private final IconCalculator iconCalculator;

    public StatusDisplayer(Mystica main, AbilityManager manager) {
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        abilityManager = manager;
        iconCalculator = new IconCalculator();
    }

    public void displayStatus(Player player) {

        if(!profileManager.getAnyProfile(player).getIfInCombat()){
            clearPlayerStatus(player);
            return;
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            clearPlayerStatus(player);
            return;
        }

        StringBuilder statusString = new StringBuilder();

        String bigStatus = getCastStatus(player);

        String colorlessClassStatus = getClassStatus(player).replaceAll("§.", "");
        String colorlessBonusStatus = getBonusStatus(player).replaceAll("§.", "");

        int amountStatusChar = colorlessClassStatus.length() + colorlessBonusStatus.length();

        int amountDebuffChar = getDebuffClassStatus(player).replaceAll("§.", "").length();


        StringBuilder leftSide = new StringBuilder();
        StringBuilder rightSide = new StringBuilder();

        if(amountStatusChar > amountDebuffChar){
            for(int i = 0; i<amountStatusChar-amountDebuffChar; i++){
                leftSide.append(" ");
            }
        }

        if(amountStatusChar < amountDebuffChar){
            for(int i = 0; i<amountDebuffChar-amountStatusChar; i++){
                rightSide.append(" ");
            }
        }


        statusString.append(leftSide).append(getDebuffClassStatus(player)).append(bigStatus).append(getClassStatus(player)).append(getBonusStatus(player)).append(rightSide);


        player.sendTitle(getBigClassStatus(player), String.valueOf(statusString), 0, 200, 0);
    }

    private void clearPlayerStatus(Player player){
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

    private String getClassStatus(Player player){

        StringBuilder statusString = new StringBuilder();

        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        switch (clazz.toLowerCase()) {
            case "elementalist": {
                statusString.append(applyElementalistStatuses(player));
                break;
            }
            case "ranger": {
                statusString.append(applyRangerStatus(player));
                break;
            }
            case "shadow knight": {
                statusString.append(applyShadowKnightStatus(player));
                break;
            }
            case "mystic": {
                statusString.append(applyMysticStatus(player));
                break;
            }
            case "assassin":{
                statusString.append(applyAssassinStatus(player));
                break;
            }
            case "warrior":{
                statusString.append(applyWarriorStatus(player));
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
            //calculate which icon to display

            int maxDuration = abilityManager.getElementalistAbilities().getElementalBreath().getDuration(player);

            int icon = iconCalculator.calculate(breathTime, maxDuration);

            switch (icon){
                case 8:{
                    statusString.append("\uE004");
                    break;
                }
                case 7:{
                    statusString.append("\uE01B");
                    break;
                }
                case 6:{
                    statusString.append("\uE01C");
                    break;
                }
                case 5:{
                    statusString.append("\uE01D");
                    break;
                }
                case 4:{
                    statusString.append("\uE01E");
                    break;
                }
                case 3:{
                    statusString.append("\uE01F");
                    break;
                }
                case 2:{
                    statusString.append("\uE020");
                    break;
                }
                case 1:{
                    statusString.append("\uE021");
                    break;
                }
            }


        }

        return String.valueOf(statusString);
    }

    private String applyRangerStatus(Player player){
        StringBuilder statusString = new StringBuilder();

        int cry = abilityManager.getRangerAbilities().getRallyingCry().getIfBuffTime(player);

        if(cry > 0){
            int icon =iconCalculator.calculate(cry, 10);

            switch (icon){
                case 8:{
                    statusString.append("\uE006");
                    break;
                }
                case 7:{
                    statusString.append("\uE029");
                    break;
                }
                case 6:{
                    statusString.append("\uE02A");
                    break;
                }
                case 5:{
                    statusString.append("\uE02B");
                    break;
                }
                case 4:{
                    statusString.append("\uE02C");
                    break;
                }
                case 3:{
                    statusString.append("\uE02D");
                    break;
                }
                case 2:{
                    statusString.append("\uE02E");
                    break;
                }
                case 1:{
                    statusString.append("\uE02F");
                    break;
                }
            }

        }

        return String.valueOf(statusString);
    }

    private String applyShadowKnightStatus(Player player){
        StringBuilder statusString = new StringBuilder();

        int marks = abilityManager.getShadowKnightAbilities().getSoulReap().getSoulMarks(player);

        switch (marks){
            case 1:{
                statusString.append("\uE016");
                break;
            }
            case 2:{
                statusString.append("\uE017");
                break;
            }
            case 3:{
                statusString.append("\uE018");
                break;
            }
            case 4:{
                statusString.append("\uE019");
                break;
            }
            case 5:{
                statusString.append("\uE01A");
                break;
            }
        }

        return String.valueOf(statusString);
    }

    private String applyMysticStatus(Player player){
        StringBuilder statusString = new StringBuilder();

        boolean instantCast = abilityManager.getMysticAbilities().getPurifyingBlast().getInstantCast(player);

        if(instantCast){
            statusString.append("\uE048");
        }

        return String.valueOf(statusString);
    }

    private String applyAssassinStatus(Player player){
        StringBuilder statusString = new StringBuilder();

        if(buffAndDebuffManager.getBladeTempestCrit().getTempestCrit(player) !=0 ){
            statusString.append("\uE056");
        }

        if(abilityManager.getAssassinAbilities().getStealth().getIfStealthed(player)){
            statusString.append("\uE057");
        }


        return String.valueOf(statusString);
    }

    private String applyWarriorStatus(Player player){
        StringBuilder statusString = new StringBuilder();

        if(buffAndDebuffManager.getBurningBlessingBuff().getIfHealthBuff(player)){
            statusString.append("\uE05B");
        }


        return String.valueOf(statusString);
    }

    private String getDebuffClassStatus(Player player){
        StringBuilder statusString = new StringBuilder();


        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        switch (clazz.toLowerCase()){
            case "shadow knight":{
                //get target and if they are infected, get the time left
                LivingEntity target = targetManager.getPlayerTarget(player);
                if(target == null){
                    break;
                }

                int timeLeft = abilityManager.getShadowKnightAbilities().getInfection().getPlayerInfectionTime(player);

                if(timeLeft>0){
                    boolean enhanced = abilityManager.getShadowKnightAbilities().getInfection().getIfEnhanced(player);

                    int icon = iconCalculator.calculate(timeLeft, 10);

                    if(enhanced){
                        switch (icon){
                            case 8:{
                                statusString.append("\uE038");
                                break;
                            }
                            case 7:{
                                statusString.append("\uE039");
                                break;
                            }
                            case 6:{
                                statusString.append("\uE03A");
                                break;
                            }
                            case 5:{
                                statusString.append("\uE03B");
                                break;
                            }
                            case 4:{
                                statusString.append("\uE03C");
                                break;
                            }
                            case 3:{
                                statusString.append("\uE03D");
                                break;
                            }
                            case 2:{
                                statusString.append("\uE03E");
                                break;
                            }
                            case 1:{
                                statusString.append("\uE03F");
                                break;
                            }
                        }
                    }
                    else{
                        switch (icon){
                            case 8:{
                                statusString.append("\uE030");
                                break;
                            }
                            case 7:{
                                statusString.append("\uE031");
                                break;
                            }
                            case 6:{
                                statusString.append("\uE032");
                                break;
                            }
                            case 5:{
                                statusString.append("\uE033");
                                break;
                            }
                            case 4:{
                                statusString.append("\uE034");
                                break;
                            }
                            case 3:{
                                statusString.append("\uE035");
                                break;
                            }
                            case 2:{
                                statusString.append("\uE036");
                                break;
                            }
                            case 1:{
                                statusString.append("\uE037");
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case "assassin":{
                int timeLeft = buffAndDebuffManager.getPierceBuff().getIfBuffTime(player);

                if(timeLeft>0){
                    int icon = iconCalculator.calculate(timeLeft, 10);

                    switch (icon){
                        case 8:{
                            statusString.append("\uE04C");
                            break;
                        }
                        case 7:{
                            statusString.append("\uE04D");
                            break;
                        }
                        case 6:{
                            statusString.append("\uE04E");
                            break;
                        }
                        case 5:{
                            statusString.append("\uE04F");
                            break;
                        }
                        case 4:{
                            statusString.append("\uE050");
                            break;
                        }
                        case 3:{
                            statusString.append("\uE051");
                            break;
                        }
                        case 2:{
                            statusString.append("\uE052");
                            break;
                        }
                        case 1:{
                            statusString.append("\uE053");
                            break;
                        }
                    }
                }

                break;
            }
        }

        return String.valueOf(statusString);
    }

    private String getBonusStatus(Player player){

        StringBuilder statusString = new StringBuilder();

        //melt
        if(buffAndDebuffManager.getArmorMelt().getStacks(player) >= 3){
            if(buffAndDebuffManager.getArmorMelt().getTimeLeft(player) > 0){
                int icon = iconCalculator.calculate(buffAndDebuffManager.getArmorMelt().getTimeLeft(player), 10);

                switch (icon){
                    case 8:{
                        statusString.append("\uE05C");
                        break;
                    }
                    case 7:{
                        statusString.append("\uE05D");
                        break;
                    }
                    case 6:{
                        statusString.append("\uE05E");
                        break;
                    }
                    case 5:{
                        statusString.append("\uE05F");
                        break;
                    }
                    case 4:{
                        statusString.append("\uE060");
                        break;
                    }
                    case 3:{
                        statusString.append("\uE061");
                        break;
                    }
                    case 2:{
                        statusString.append("\uE062");
                        break;
                    }
                    case 1:{
                        statusString.append("\uE063");
                        break;
                    }
                }
            }
        }



        //wild roar
        if(buffAndDebuffManager.getWildRoarBuff().getBuffTime(player) > 0){
            int icon = iconCalculator.calculate(buffAndDebuffManager.getWildRoarBuff().getBuffTime(player), 10);

            switch (icon){
                case 8:{
                    statusString.append("\uE007");
                    break;
                }
                case 7:{
                    statusString.append("\uE022");
                    break;
                }
                case 6:{
                    statusString.append("\uE023");
                    break;
                }
                case 5:{
                    statusString.append("\uE024");
                    break;
                }
                case 4:{
                    statusString.append("\uE025");
                    break;
                }
                case 3:{
                    statusString.append("\uE026");
                    break;
                }
                case 2:{
                    statusString.append("\uE027");
                    break;
                }
                case 1:{
                    statusString.append("\uE028");
                    break;
                }
            }


        }

        if(buffAndDebuffManager.getConjuringForceBuff().getIfConjForceBuff(player)){
            statusString.append("\uE005");
        }

        if(buffAndDebuffManager.getFlamingSigilBuff().getIfAttackBuff(player) || buffAndDebuffManager.getFlamingSigilBuff().getIfHealthBuff(player)){
            statusString.append("\uE05A");
        }

        //speed up
        if(buffAndDebuffManager.getSpeedUp().getIfSpeedUp(player)){
            statusString.append("\uE003");
        }

        return String.valueOf(statusString);

    }

}
