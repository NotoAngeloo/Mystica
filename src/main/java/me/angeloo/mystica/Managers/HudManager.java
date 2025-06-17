package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.IconCalculator;
import me.angeloo.mystica.Utility.SkinGrabber;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.ArrayList;
import java.util.List;

import static me.angeloo.mystica.Mystica.*;


public class HudManager {

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final AllSkillItems allSkillItems;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final TargetManager targetManager;
    private final BossCastingManager bossCastingManager;
    private final IconCalculator iconCalculator;

    private final SkinGrabber skinGrabber;


    public HudManager(Mystica main){
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        abilityManager = main.getAbilityManager();
        allSkillItems = main.getAllSkillItems();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        targetManager = main.getTargetManager();
        bossCastingManager = main.getBossCastingManager();
        iconCalculator = new IconCalculator();

        skinGrabber = new SkinGrabber();
    }

    public void innitHud(Player player){


        //bar 1, player resources
        BossBar resourceBar = Bukkit.createBossBar(createPlayerDataString(player), BarColor.WHITE, BarStyle.SOLID);
        resourceBar.addPlayer(player);
        resourceBar.setVisible(true);
        profileManager.setPlayerResourceBar(player, resourceBar);

        //bar 2, target bar
        BossBar targetBar = Bukkit.createBossBar(createTargetDataString(player), BarColor.WHITE, BarStyle.SOLID);
        targetBar.addPlayer(player);
        targetBar.setVisible(true);
        profileManager.setPlayerTargetBar(player, targetBar);


        BossBar teamBar = Bukkit.createBossBar(createTeamDataString(player), BarColor.WHITE, BarStyle.SOLID);
        teamBar.addPlayer(player);
        teamBar.setVisible(true);
        profileManager.setPlayerTeamBar(player, teamBar);


        BossBar statusBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
        statusBar.addPlayer(player);
        statusBar.setVisible(true);
        profileManager.setPlayerStatusBar(player, statusBar);


    }

    public void displayUltimate(Player player){

        StringBuilder hotBar = new StringBuilder();

        String statusString = getUltimateStatus(player);

        hotBar.append(statusString);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(hotBar)));
    }

    public void displayCastBar(Player player){

        if(!abilityManager.getIfCasting(player)){
            //Bukkit.getLogger().info("player stop casting");
            player.sendTitle("", "", 0, 1, 0);
            return;
        }

        if(abilityManager.getCastPercent(player) == 0){
            //Bukkit.getLogger().info("player stop casting");
            player.sendTitle("", "", 0, 1, 0);
            return;
        }

        StringBuilder castBar = new StringBuilder();

        double percent =  abilityManager.getCastPercent(player);

        double ratio = percent / 100;

        int amount = (int) Math.ceil(ratio * 20);

        //Bukkit.getLogger().info(String.valueOf(amount));

        switch (amount){
            case 20:{
                castBar.append("\uE0CD");
                break;
            }
            case 19:{
                castBar.append("\uE0CE");
                break;
            }
            case 18:{
                castBar.append("\uE0CF");
                break;
            }
            case 17:{
                castBar.append("\uE0D0");
                break;
            }
            case 16:{
                castBar.append("\uE0D1");
                break;
            }
            case 15:{
                castBar.append("\uE0D2");
                break;
            }
            case 14:{
                castBar.append("\uE0D3");
                break;
            }
            case 13:{
                castBar.append("\uE0D4");
                break;
            }
            case 12:{
                castBar.append("\uE0D5");
                break;
            }
            case 11:{
                castBar.append("\uE0D6");
                break;
            }
            case 10:{
                castBar.append("\uE0D7");
                break;
            }
            case 9:{
                castBar.append("\uE0D8");
                break;
            }
            case 8:{
                castBar.append("\uE0D9");
                break;
            }
            case 7:{
                castBar.append("\uE0DA");
                break;
            }
            case 6:{
                castBar.append("\uE0DB");
                break;
            }
            case 5:{
                castBar.append("\uE0DC");
                break;
            }
            case 4:{
                castBar.append("\uE0DD");
                break;
            }
            case 3:{
                castBar.append("\uE0DE");
                break;
            }
            case 2:{
                castBar.append("\uE0DF");
                break;
            }
            case 1:{
                castBar.append("\uE0E0");
                break;
            }
        }

        //Bukkit.getLogger().info(String.valueOf(percent));



        player.sendTitle(" ", String.valueOf(castBar), 0, 2, 0);

    }

    public void editResourceBar(Player player){
        BossBar resourceBar = profileManager.getPlayerResourceBar(player);
        resourceBar.setTitle(createPlayerDataString(player));
    }
    public void editTargetBar(Player player){
        BossBar targetBar = profileManager.getPlayerTargetBar(player);
        targetBar.setTitle(createTargetDataString(player));
    }
    public void editTeamBar(Player player){
        BossBar teamBar = profileManager.getPlayerTeamBar(player);
        teamBar.setTitle(createTeamDataString(player));
    }
    public void editStatusBar(Player player){
        BossBar statusBar = profileManager.getPlayerStatusBar(player);
        statusBar.setTitle(createStatusString(player));
    }

    private String createPlayerDataString(Player player){

        StringBuilder playerResources = new StringBuilder();

        //-512space
        playerResources.append("\uF80E");

        playerResources.append(createEntityDataString(player));


        return String.valueOf(playerResources);

    }

    private String createTargetDataString(Player player){

        StringBuilder targetData = new StringBuilder();

        if(targetManager.getPlayerTarget(player) != null){
            LivingEntity target = targetManager.getPlayerTarget(player);
            targetData.append(createEntityDataString(target));
        }

        return String.valueOf(targetData);
    }

    private String createTeamDataString(Player player){


        StringBuilder teamData = new StringBuilder();


        List<LivingEntity> mysticaParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

        if(mysticaParty.size() == 1){
            return String.valueOf(teamData);
        }



        if(mysticaParty.size() <= 5){

            //-512 space
            teamData.append("\uF80E");

            //-115
            teamData.append("\uF80B\uF80A\uF809\uF803");

            int slot = 0;
            for(LivingEntity member : mysticaParty){

                if(member == player){
                    continue;
                }

                teamData.append(getTeamMemberDataString(member, slot));

                slot++;

                if(slot== mysticaParty.size()){
                    break;
                }

                //-115
                teamData.append("\uF80B\uF80A\uF809\uF803");
            }

            return String.valueOf(teamData);
        }



        StringBuilder offset = new StringBuilder();


        //-512 space
        teamData.append("\uF80E");

        //-100space
        teamData.append("\uF80B\uF80A\uF804");


        //loop based on party members

        int slot = 0;
        for(LivingEntity member : mysticaParty){

            if(member == player){
                continue;
            }

            if(!(member instanceof Player)){
                //companions not allowed in squads
                continue;
            }

            Player memberPlayer = (Player) member;

            teamData.append(getSquadMemberDataString(member, slot));

            //-32
            teamData.append("\uF80A");

            teamData.append(skinGrabber.getSquadFace(memberPlayer, slot));

            // +36
            teamData.append("\uF82A\uF824");

            //+36
            offset.append("\uF82A\uF824");



            //-64
            //teamData.append("\uF80B");


            /*if(slot == mysticaParty.size()){
                break;
            }*/

            slot ++;

            //this code is bad look away
            if(slot >= 3){
                //+1
                offset.append("\uF821");
            }

            if(slot == 3 || slot == 6){

                //-111
                teamData.append("\uF80B\uF80A\uF808\uF807");

                //+111
                //offset.append("\uF82B\uF82A\uF828\uF827");

                //-111
                offset.append("\uF80B\uF80A\uF808\uF807");

                //append ofdset later
            }


            //+64
            //teamData.append("\uF82B");

            //move it back if larger that 3
        }


        offset.append(teamData);

        return String.valueOf(offset);
    }

    private String createStatusString(Player player){
        StringBuilder status = new StringBuilder();

        if(!profileManager.getAnyProfile(player).getIfInCombat()){
            return String.valueOf(status);
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return String.valueOf(status);
        }

        StringBuilder offset = new StringBuilder();

        //TODO: player status on right, enemy status (bleeds etc) on left

        String playerClass = profileManager.getAnyProfile(player).getPlayerClass();
        String subClass = profileManager.getAnyProfile(player).getPlayerSubclass();

        //-128
        status.append("\uF80C");

        //class specific buffs
        switch (playerClass.toLowerCase()){
            case "elementalist":{

                if(subClass.equalsIgnoreCase("pyromancer")){

                    int inflame = abilityManager.getElementalistAbilities().getFieryWing().getInflame(player);

                    if(inflame > 0){
                        //+16
                        offset.append("\uF829");

                        status.append("\uE000");
                        status.append(getStackString(inflame));
                    }

                }

                int breathTime = abilityManager.getElementalistAbilities().getElementalBreath().getIfBuffTime(player);
                int duration = abilityManager.getElementalistAbilities().getElementalBreath().getDuration(player);

                if(breathTime > 0){
                    //+16
                    offset.append("\uF829");

                    status.append("\uE01C");

                    status.append(getDurationString(breathTime, duration));
                }

                break;
            }
            case "ranger":{

                int cry = abilityManager.getRangerAbilities().getRallyingCry().getIfBuffTime(player);
                int duration = abilityManager.getRangerAbilities().getRallyingCry().getDuration();

                if(cry > 0){
                    //+16
                    offset.append("\uF829");

                    status.append("\uE01D");

                    status.append(getDurationString(cry, duration));
                }

                break;
            }
            case "shadow knight":{

                LivingEntity target = targetManager.getPlayerTarget(player);

                if(target != null){
                    int timeLeft = abilityManager.getShadowKnightAbilities().getInfection().getPlayerInfectionTime(player);

                    if(timeLeft > 0){

                        //+16
                        offset.append("\uF829");

                        boolean enhanced = abilityManager.getShadowKnightAbilities().getInfection().getIfEnhanced(player);

                        int duration = abilityManager.getShadowKnightAbilities().getInfection().getDuration();

                        if(enhanced){
                            status.append("\uE021");
                        }
                        else{
                            status.append("\uE020");
                        }

                        status.append(getDurationString(timeLeft, duration));

                    }
                }

                if(subClass.equalsIgnoreCase("doom")){

                    int marks = abilityManager.getShadowKnightAbilities().getSoulReap().getSoulMarks(player);

                    switch (marks){
                        case 1:
                        case 2:
                        case 3:
                        case 4:{

                            //+16
                            offset.append("\uF829");

                            status.append("\uE01E");

                            status.append(getStackString(marks));

                            break;
                        }
                        case 5:{

                            //+16
                            offset.append("\uF829");

                            status.append("\uE01F");

                            break;
                        }
                    }
                }

                break;
            }
            case "mystic":{

                if(abilityManager.getMysticAbilities().getPurifyingBlast().getInstantCast(player)){

                    //+16
                    offset.append("\uF829");

                    status.append("\uE022");
                }

                break;
            }
            case "assassin":{

                int timeLeft = buffAndDebuffManager.getPierceBuff().getIfBuffTime(player);
                int max = buffAndDebuffManager.getPierceBuff().getDuration();

                if(timeLeft > 0){
                    //+16
                    offset.append("\uF829");

                    status.append("\uE025");

                    status.append(getDurationString(timeLeft, max));
                }

                if(buffAndDebuffManager.getBladeTempestCrit().getTempestCrit(player) !=0 ){
                    //+16
                    offset.append("\uF829");

                    status.append("\uE023");
                }

                if(abilityManager.getAssassinAbilities().getStealth().getIfStealthed(player)){
                    //+16
                    offset.append("\uF829");

                    status.append("\uE024");
                }

                break;
            }
            case "warrior":{

                if(buffAndDebuffManager.getBurningBlessingBuff().getIfHealthBuff(player)){

                    //-16
                    offset.append("\uF829");

                    status.append("\uE026");
                }

                break;
            }
            case "paladin":{

                if(abilityManager.getPaladinAbilities().getDecision().getDecision(player)){

                    //-16
                    offset.append("\uF829");

                    status.append("\uE027");

                }

                break;
            }
        }


        //generic buffs/debuffs
        if(buffAndDebuffManager.getArmorBreak().getStacks(player) >= 3){
            int timeLeft = buffAndDebuffManager.getArmorBreak().getTimeLeft(player);
            int max = buffAndDebuffManager.getArmorBreak().getDuration();
            int stacks = buffAndDebuffManager.getArmorBreak().getStacks(player);
            if(timeLeft > 0){

                //-16
                offset.append("\uF829");

                status.append("\uE028");

                status.append(getDurationString(timeLeft, max));
                status.append(getStackString(stacks));

            }
        }

        if(buffAndDebuffManager.getWildRoarBuff().getBuffTime(player) > 0){

            //-16
            offset.append("\uF829");

            status.append("\uE029");

            int max = buffAndDebuffManager.getWildRoarBuff().getDuration();

            status.append(getDurationString(buffAndDebuffManager.getWildRoarBuff().getBuffTime(player), max));
        }

        if(buffAndDebuffManager.getConjuringForceBuff().getIfConjForceBuff(player)){
            //-16
            offset.append("\uF829");

            status.append("\uE02A");
        }

        if(buffAndDebuffManager.getWellCrit().getWellCrit(player) == 10){
            //-16
            offset.append("\uF829");

            status.append("\uE02B");
        }

        if(buffAndDebuffManager.getFlamingSigilBuff().getIfAttackBuff(player) || buffAndDebuffManager.getFlamingSigilBuff().getIfHealthBuff(player)){
            //-16
            offset.append("\uF829");

            status.append("\uE02C");
        }

        if(buffAndDebuffManager.getSpeedUp().getIfSpeedUp(player)){
            //-16
            offset.append("\uF829");

            status.append("\uE02D");
        }

        offset.append(status);

        return String.valueOf(offset);
    }

    private String getStackString(int stacks){

        StringBuilder stacksString = new StringBuilder();

        //-17
        stacksString.append("\uF809\uF801");

        //because i dont care
        if(stacks > 20){
            stacks = 20;
        }

        switch (stacks){
            case 0:
            case 1:{
                stacksString.append("\uE008");
                break;
            }
            case 2:{
                stacksString.append("\uE009");
                break;
            }
            case 3:{
                stacksString.append("\uE00A");
                break;
            }
            case 4:{
                stacksString.append("\uE00B");
                break;
            }
            case 5:{
                stacksString.append("\uE00C");
                break;
            }
            case 6:{
                stacksString.append("\uE00D");
                break;
            }
            case 7:{
                stacksString.append("\uE00E");
                break;
            }
            case 8:{
                stacksString.append("\uE00F");
                break;
            }
            case 9:{
                stacksString.append("\uE010");
                break;
            }
            case 10:{
                stacksString.append("\uE011");
                break;
            }
            case 11:{
                stacksString.append("\uE012");
                break;
            }
            case 12:{
                stacksString.append("\uE013");
                break;
            }
            case 13:{
                stacksString.append("\uE014");
                break;
            }
            case 14:{
                stacksString.append("\uE015");
                break;
            }
            case 15:{
                stacksString.append("\uE016");
                break;
            }
            case 16:{
                stacksString.append("\uE017");
                break;
            }
            case 17:{
                stacksString.append("\uE018");
                break;
            }
            case 18:{
                stacksString.append("\uE019");
                break;
            }
            case 19:{
                stacksString.append("\uE01A");
                break;
            }
            case 20:{
                stacksString.append("\uE01B");
                break;
            }
        }



        return String.valueOf(stacksString);
    }

    private String getDurationString(int time, int max){

        StringBuilder durationString = new StringBuilder();

        int icon = iconCalculator.calculate(time, max);

        //-17
        durationString.append("\uF809\uF801");

        switch (icon){
            case 8:{
                durationString.append("\uE008");
                break;
            }
            case 7:{
                durationString.append("\uE007");
                break;
            }
            case 6:{
                durationString.append("\uE006");
                break;
            }
            case 5:{
                durationString.append("\uE005");
                break;
            }
            case 4:{
                durationString.append("\uE004");
                break;
            }
            case 3:{
                durationString.append("\uE003");
                break;
            }
            case 2:{
                durationString.append("\uE002");
                break;
            }
            case 1:{
                durationString.append("\uE001");
                break;
            }
        }

        return String.valueOf(durationString);
    }

    private String getTeamMemberDataString(LivingEntity entity, int slot){

        StringBuilder entityBar = new StringBuilder();

        String playerClass = profileManager.getAnyProfile(entity).getPlayerClass();

        switch (playerClass.toLowerCase()){
            case "assassin":{
                switch (slot){
                    case 0:{
                        entityBar.append("\uE245");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE246");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE247");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE248");
                        break;
                    }
                }
                break;
            }
            case "elementalist":{
                switch (slot){
                    case 0:{
                        entityBar.append("\uE24A");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE24B");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE24C");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE24D");
                        break;
                    }
                }
                break;
            }
            case "mystic":{
                switch (slot){
                    case 0:{
                        entityBar.append("\uE24F");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE250");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE251");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE252");
                        break;
                    }
                }
                break;
            }
            case "paladin":{
                switch (slot){
                    case 0:{
                        entityBar.append("\uE254");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE255");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE256");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE257");
                        break;
                    }
                }
                break;
            }
            case "ranger":{
                switch (slot){
                    case 0:{
                        entityBar.append("\uE259");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE25A");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE25B");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE25C");
                        break;
                    }
                }
                break;
            }
            case "shadow knight":{
                switch (slot){
                    case 0:{
                        entityBar.append("\uE25E");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE25F");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE260");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE261");
                        break;
                    }
                }
                break;
            }
            case "warrior":{
                switch (slot){
                    case 0:{
                        entityBar.append("\uE263");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE264");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE265");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE266");
                        break;
                    }
                }
                break;
            }
            default:{
                switch (slot){
                    case 0:{
                        entityBar.append("\uE14D");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE178");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE1A3");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE1CE");
                        break;
                    }
                }
                break;
            }
        }




        //-29
        entityBar.append("\uF809\uF808\uF805");

        if(entity instanceof Player){

            Player player = (Player) entity;


            entityBar.append(skinGrabber.getTeamFace(player, slot));

            //+29
            entityBar.append("\uF829\uF828\uF825");



            return String.valueOf(entity);
        }

        if(profileManager.getAnyProfile(entity).fakePlayer()){

            entityBar.append(profileManager.getCompanionTeamFace(entity.getUniqueId(), slot));

            //+3 is only because default steve face.
            entityBar.append("\uF823");
        }

        //change this to get the right value
        entityBar.append(teamHealthBar(entity, slot));

        return String.valueOf(entityBar);
    }

    private String getSquadMemberDataString(LivingEntity entity, int slot){

        StringBuilder entityBar = new StringBuilder();

        //depending on class
        String playerClass = profileManager.getAnyProfile(entity).getPlayerClass();

        switch (playerClass.toLowerCase()){

            case "assassin":{
                entityBar.append(ChatColor.of(assassinColor));
                break;
            }
            case "elementalist":{
                entityBar.append(ChatColor.of(elementalistColor));
                break;
            }
            case "ranger":{
                entityBar.append(ChatColor.of(rangerColor));
                break;
            }
            case "paladin":{
                entityBar.append(ChatColor.of(paladinColor));
                break;
            }
            case "warrior":{
                entityBar.append(ChatColor.of(warriorColor));
                break;
            }
            case "shadow knight":{
                entityBar.append(ChatColor.of(shadowKnightColor));
                break;
            }
            case "mystic":{
                entityBar.append(ChatColor.of(mysticColor));
                break;
            }
            default:{
                entityBar.append(ChatColor.RESET);
                break;
            }
        }


        //depending on health, get a different unicode
        Profile playerProfile = profileManager.getAnyProfile(entity);
        double actualMaxHealth = playerProfile.getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(entity);
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();
        double ratio = actualCurrentHealth / actualMaxHealth;
        int amount = (int) Math.ceil(ratio * 8);

        if(amount < 0){
            amount = 0;
        }

        if(actualCurrentHealth <= 0){
            amount = 0;
        }

        if(amount > 8){
            amount = 8;
        }

        if(playerProfile.getIfDead()){
            amount = 0;
        }

        //slot switch here for height
        switch (slot){
            case 0:
            case 1:
            case 2:{
                switch (amount){
                    case 8:{
                        entityBar.append("\uE212");
                        break;
                    }
                    case 7:{
                        entityBar.append("\uE213");
                        break;
                    }
                    case 6:{
                        entityBar.append("\uE214");
                        break;
                    }
                    case 5:{
                        entityBar.append("\uE215");
                        break;
                    }
                    case 4:{
                        entityBar.append("\uE216");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE217");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE218");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE219");
                        break;
                    }
                    case 0:{
                        entityBar.append("\uE21A");
                        break;
                    }
                }
                break;
            }
            case 3:
            case 4:
            case 5:{
                switch (amount){
                    case 8:{
                        entityBar.append("\uE21B");
                        break;
                    }
                    case 7:{
                        entityBar.append("\uE21C");
                        break;
                    }
                    case 6:{
                        entityBar.append("\uE21D");
                        break;
                    }
                    case 5:{
                        entityBar.append("\uE21E");
                        break;
                    }
                    case 4:{
                        entityBar.append("\uE21F");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE220");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE221");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE222");
                        break;
                    }
                    case 0:{
                        entityBar.append("\uE223");
                        break;
                    }
                }
                break;
            }
            case 6:
            case 7:
            case 8:{
                switch (amount){
                    case 8:{
                        entityBar.append("\uE224");
                        break;
                    }
                    case 7:{
                        entityBar.append("\uE225");
                        break;
                    }
                    case 6:{
                        entityBar.append("\uE226");
                        break;
                    }
                    case 5:{
                        entityBar.append("\uE227");
                        break;
                    }
                    case 4:{
                        entityBar.append("\uE228");
                        break;
                    }
                    case 3:{
                        entityBar.append("\uE229");
                        break;
                    }
                    case 2:{
                        entityBar.append("\uE22A");
                        break;
                    }
                    case 1:{
                        entityBar.append("\uE22B");
                        break;
                    }
                    case 0:{
                        entityBar.append("\uE009");
                        break;
                    }
                }
                break;
            }
        }





        return String.valueOf(entityBar);
    }

    private String createEntityDataString(LivingEntity entity){

        StringBuilder entityData = new StringBuilder();

        entityData.append(playerAndTargetIcon(entity));
        entityData.append(healthBar(entity));
        entityData.append(resourceBar(entity));

        return String.valueOf(entityData);
    }

    private String playerAndTargetIcon(LivingEntity entity){

        StringBuilder icon = new StringBuilder();

        if(entity instanceof Player){

            Player player = (Player) entity;

            String playerClass = profileManager.getAnyProfile(player).getPlayerClass();

            switch (playerClass.toLowerCase()){
                case "assassin":{
                    icon.append("\uE244");
                    break;
                }
                case "elementalist":{
                    icon.append("\uE249");
                    break;
                }
                case "mystic":{
                    icon.append("\uE24E");
                    break;
                }
                case "paladin":{
                    icon.append("\uE253");
                    break;
                }
                case "ranger":{
                    icon.append("\uE258");
                    break;
                }
                case "shadow knight":{
                    icon.append("\uE25D");
                    break;
                }
                case "warrior":{
                    icon.append("\uE262");
                    break;
                }
                default:{
                    icon.append("\uE143");
                    break;
                }
            }



            //-29
            icon.append("\uF809\uF808\uF805");


            icon.append(skinGrabber.getFace(player));

            //+29
            icon.append("\uF829\uF828\uF825");
            return String.valueOf(icon);
        }


        if(profileManager.getAnyProfile(entity).fakePlayer()){

            String playerClass = profileManager.getAnyProfile(entity).getPlayerClass();

            switch (playerClass.toLowerCase()){
                case "assassin":{
                    icon.append("\uE244");
                    break;
                }
                case "elementalist":{
                    icon.append("\uE249");
                    break;
                }
                case "mystic":{
                    icon.append("\uE24E");
                    break;
                }
                case "paladin":{
                    icon.append("\uE253");
                    break;
                }
                case "ranger":{
                    icon.append("\uE258");
                    break;
                }
                case "shadow knight":{
                    icon.append("\uE25D");
                    break;
                }
                case "warrior":{
                    icon.append("\uE262");
                    break;
                }
                default:{
                    icon.append("\uE143");
                    break;
                }
            }

            //-29
            icon.append("\uF809\uF808\uF805");

            //default face, for testing
            String face = profileManager.getCompanionFace(entity.getUniqueId());
            icon.append(face);

            //+3
            icon.append("\uF823");
            return String.valueOf(icon);

        }

        if(profileManager.getAnyProfile(entity).getIsPassive()){
            //default npc
            icon.append(("\uE127"));
            return String.valueOf(icon);
        }



        //default enemy
        icon.append(("\uE128"));

        return String.valueOf(icon);
    }

    private String healthBar(LivingEntity entity){

        StringBuilder healthBar = new StringBuilder();

        Profile playerProfile = profileManager.getAnyProfile(entity);
        double actualMaxHealth = playerProfile.getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(entity);
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();

        double ratio = actualCurrentHealth / actualMaxHealth;

        int amount = (int) Math.ceil(ratio * 20);

        //Bukkit.getLogger().info("player ratio number " + amount);

        if(amount < 0){
            amount = 0;
        }

        if(actualCurrentHealth <= 0){
            amount = 0;
        }

        if(amount > 20){
            amount = 20;
        }

        switch (amount){
            case 20:{
                healthBar.append("\uE0B8");
                break;
            }
            case 19:{
                healthBar.append("\uE0B9");
                break;
            }
            case 18:{
                healthBar.append("\uE0BA");
                break;
            }
            case 17:{
                healthBar.append("\uE0BB");
                break;
            }
            case 16:{
                healthBar.append("\uE0BC");
                break;
            }
            case 15:{
                healthBar.append("\uE0BD");
                break;
            }
            case 14:{
                healthBar.append("\uE0BE");
                break;
            }
            case 13:{
                healthBar.append("\uE0BF");
                break;
            }
            case 12:{
                healthBar.append("\uE0C0");
                break;
            }
            case 11:{
                healthBar.append("\uE0C1");
                break;
            }
            case 10:{
                healthBar.append("\uE0C2");
                break;
            }
            case 9:{
                healthBar.append("\uE0C3");
                break;
            }
            case 8:{
                healthBar.append("\uE0C4");
                break;
            }
            case 7:{
                healthBar.append("\uE0C5");
                break;
            }
            case 6:{
                healthBar.append("\uE0C6");
                break;
            }
            case 5:{
                healthBar.append("\uE0C7");
                break;
            }
            case 4:{
                healthBar.append("\uE0C8");
                break;
            }
            case 3:{
                healthBar.append("\uE0C9");
                break;
            }
            case 2:{
                healthBar.append("\uE0CA");
                break;
            }
            case 1:{
                healthBar.append("\uE0CB");
                break;
            }
            case 0:{
                healthBar.append("\uE0CC");
                break;
            }
        }

        double maxHp = profileManager.getAnyProfile(entity).getTotalHealth();
        double shield = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(entity) +
                buffAndDebuffManager.getWindWallBuff().getWallHealth(entity);
        double shieldRatio = shield/ maxHp;

        int shieldAmount = (int) Math.ceil(shieldRatio * 20);

        if(shieldAmount < 0){
            shieldAmount = 0;
        }

        if(shieldAmount > 20){
            shieldAmount = 20;
        }

        if(shieldAmount != 0){
            //-83 space
            healthBar.append("\uF80B\uF809\uF803");
        }

        switch (shieldAmount){
            case 20:{
                healthBar.append("\uE110");
                break;
            }
            case 19:{
                healthBar.append("\uE111");
                break;
            }
            case 18:{
                healthBar.append("\uE112");
                break;
            }
            case 17:{
                healthBar.append("\uE113");
                break;
            }
            case 16:{
                healthBar.append("\uE114");
                break;
            }
            case 15:{
                healthBar.append("\uE115");
                break;
            }
            case 14:{
                healthBar.append("\uE116");
                break;
            }
            case 13:{
                healthBar.append("\uE117");
                break;
            }
            case 12:{
                healthBar.append("\uE118");
                break;
            }
            case 11:{
                healthBar.append("\uE119");
                break;
            }
            case 10:{
                healthBar.append("\uE11A");
                break;
            }
            case 9:{
                healthBar.append("\uE11B");
                break;
            }
            case 8:{
                healthBar.append("\uE11C");
                break;
            }
            case 7:{
                healthBar.append("\uE11D");
                break;
            }
            case 6:{
                healthBar.append("\uE11E");
                break;
            }
            case 5:{
                healthBar.append("\uE11F");
                break;
            }
            case 4:{
                healthBar.append("\uE120");
                break;
            }
            case 3:{
                healthBar.append("\uE121");
                break;
            }
            case 2:{
                healthBar.append("\uE122");
                break;
            }
            case 1:{
                healthBar.append("\uE123");
                break;
            }
        }


        return String.valueOf(healthBar);
    }

    private String teamHealthBar(LivingEntity entity, int slot){
        StringBuilder healthBar = new StringBuilder();

        Profile playerProfile = profileManager.getAnyProfile(entity);
        double actualMaxHealth = playerProfile.getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(entity);
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();
        double ratio = actualCurrentHealth / actualMaxHealth;
        int amount = (int) Math.ceil(ratio * 20);

        if(amount < 0){
            amount = 0;
        }

        if(actualCurrentHealth <= 0){
            amount = 0;
        }

        if(amount > 20){
            amount = 20;
        }

        if(playerProfile.getIfDead()){
            amount = 0;
        }

        double maxHp = profileManager.getAnyProfile(entity).getTotalHealth();
        double shield = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(entity) +
                buffAndDebuffManager.getWindWallBuff().getWallHealth(entity);
        double shieldRatio = shield/ maxHp;

        int shieldAmount = (int) Math.ceil(shieldRatio * 20);

        if(shieldAmount < 0){
            shieldAmount = 0;
        }

        if(shieldAmount > 20){
            shieldAmount = 20;
        }

        switch (slot){
            case 0:{
                switch (amount){
                    case 20:{
                        healthBar.append("\uE14F");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE150");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE151");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE152");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE153");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE154");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE155");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE156");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE157");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE158");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE159");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE15A");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE15B");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE15C");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE15D");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE15E");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE15F");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE160");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE161");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE162");
                        break;
                    }
                    case 0:{
                        healthBar.append("\uE163");
                        break;
                    }
                }

                if(shieldAmount != 0){
                    //-83 space
                    healthBar.append("\uF80B\uF809\uF803");
                }

                switch (shieldAmount){
                    case 20:{
                        healthBar.append("\uE164");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE165");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE166");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE167");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE168");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE169");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE16A");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE16B");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE16C");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE16D");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE16E");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE16F");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE170");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE171");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE172");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE173");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE174");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE175");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE176");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE177");
                        break;
                    }
                }
                break;
            }
            case 1:{
                switch (amount){
                    case 20:{
                        healthBar.append("\uE17A");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE17B");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE17C");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE17D");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE17E");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE17F");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE180");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE181");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE182");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE183");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE184");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE185");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE186");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE187");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE188");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE189");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE18A");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE18B");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE18C");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE18D");
                        break;
                    }
                    case 0:{
                        healthBar.append("\uE18E");
                        break;
                    }
                }

                if(shieldAmount != 0){
                    //-83 space
                    healthBar.append("\uF80B\uF809\uF803");
                }

                switch (shieldAmount){
                    case 20:{
                        healthBar.append("\uE18F");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE190");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE191");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE192");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE193");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE194");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE195");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE196");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE197");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE198");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE199");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE19A");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE19B");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE19C");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE19D");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE19E");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE19F");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1A0");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1A1");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1A2");
                        break;
                    }
                }
                break;
            }
            case 3:{
                switch (amount){
                    case 20:{
                        healthBar.append("\uE1D0");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE1D1");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE1D2");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE1D3");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE1D4");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE1D5");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE1D6");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE1D7");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE1D8");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE1D9");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE1DA");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE1DB");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE1DC");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE1DD");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE1DE");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE1DF");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE1E0");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1E1");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1E2");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1E3");
                        break;
                    }
                    case 0:{
                        healthBar.append("\uE1E4");
                        break;
                    }
                }

                if(shieldAmount != 0){
                    //-83 space
                    healthBar.append("\uF80B\uF809\uF803");
                }

                switch (shieldAmount){
                    case 20:{
                        healthBar.append("\uE1E5");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE1E6");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE1E7");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE1E8");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE1E9");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE1EA");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE1EB");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE1EC");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE1ED");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE1EF");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE1F0");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE1F1");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE1F2");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE1F3");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE1F4");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE1F5");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE1F6");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1F7");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1F8");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1F9");
                        break;
                    }
                }
                break;
            }
            case 2:{
                switch (amount){
                    case 20:{
                        healthBar.append("\uE1A5");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE1A6");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE1A7");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE1A8");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE1A9");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE1AA");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE1AB");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE1AC");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE1AD");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE1AE");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE1AF");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE1B0");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE1B1");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE1B2");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE1B3");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE1B4");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE1B5");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1B6");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1B7");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1B8");
                        break;
                    }
                    case 0:{
                        healthBar.append("\uE1B9");
                        break;
                    }
                }

                if(shieldAmount != 0){
                    //-83 space
                    healthBar.append("\uF80B\uF809\uF803");
                }

                switch (shieldAmount){
                    case 20:{
                        healthBar.append("\uE1BA");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE1BB");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE1BC");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE1BD");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE1BE");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE1BF");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE1C0");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE1C1");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE1C2");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE1C3");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE1C4");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE1C5");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE1C6");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE1C7");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE1C8");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE1C9");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE1CA");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1CB");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1CC");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1CD");
                        break;
                    }
                }
                break;
            }
        }




        return String.valueOf(healthBar);
    }

    private String resourceBar(LivingEntity entity){

        StringBuilder resourceBar = new StringBuilder();



        if(entity instanceof Player || profileManager.getAnyProfile(entity).fakePlayer()){
            Profile playerProfile = profileManager.getAnyProfile(entity);
            String playerClass = playerProfile.getPlayerClass();
            switch (playerClass.toLowerCase()){
                case "mystic":{

                    //-83 space
                    resourceBar.append("\uF80B\uF809\uF803");

                    double maxMana = 500;
                    double currentMana = abilityManager.getMysticAbilities().getMana().getCurrentMana(entity);

                    double ratio = currentMana / maxMana;

                    int amount = (int) Math.ceil(ratio * 20);

                    if(amount < 0){
                        amount = 0;
                    }

                    if(currentMana <= 0){
                        amount = 0;
                    }

                    switch (amount){
                        case 20:{
                            resourceBar.append("\uE0CD");
                            break;
                        }
                        case 19:{
                            resourceBar.append("\uE0CE");
                            break;
                        }
                        case 18:{
                            resourceBar.append("\uE0CF");
                            break;
                        }
                        case 17:{
                            resourceBar.append("\uE0D0");
                            break;
                        }
                        case 16:{
                            resourceBar.append("\uE0D1");
                            break;
                        }
                        case 15:{
                            resourceBar.append("\uE0D2");
                            break;
                        }
                        case 14:{
                            resourceBar.append("\uE0D3");
                            break;
                        }
                        case 13:{
                            resourceBar.append("\uE0D4");
                            break;
                        }
                        case 12:{
                            resourceBar.append("\uE0D5");
                            break;
                        }
                        case 11:{
                            resourceBar.append("\uE0D6");
                            break;
                        }
                        case 10:{
                            resourceBar.append("\uE0D7");
                            break;
                        }
                        case 9:{
                            resourceBar.append("\uE0D8");
                            break;
                        }
                        case 8:{
                            resourceBar.append("\uE0D9");
                            break;
                        }
                        case 7:{
                            resourceBar.append("\uE0DA");
                            break;
                        }
                        case 6:{
                            resourceBar.append("\uE0DB");
                            break;
                        }
                        case 5:{
                            resourceBar.append("\uE0DC");
                            break;
                        }
                        case 4:{
                            resourceBar.append("\uE0DD");
                            break;
                        }
                        case 3:{
                            resourceBar.append("\uE0DE");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE0DF");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE0E0");
                            break;
                        }
                        case 0:{
                            resourceBar.append("\uE0E1");
                            break;
                        }
                    }

                    break;
                }
                case "warrior":{
                    //-83 space
                    resourceBar.append("\uF80B\uF809\uF803");

                    double maxRage = 500;
                    double currentRage = abilityManager.getWarriorAbilities().getRage().getCurrentRage(entity);

                    double ratio = currentRage / maxRage;

                    int amount = (int) Math.ceil(ratio * 20);

                    if(amount < 0){
                        amount = 0;
                    }

                    if(currentRage <= 0){
                        amount = 0;
                    }

                    switch (amount){
                        case 20:{
                            resourceBar.append("\uE0E2");
                            break;
                        }
                        case 19:{
                            resourceBar.append("\uE0E3");
                            break;
                        }
                        case 18:{
                            resourceBar.append("\uE0E4");
                            break;
                        }
                        case 17:{
                            resourceBar.append("\uE0E5");
                            break;
                        }
                        case 16:{
                            resourceBar.append("\uE0E6");
                            break;
                        }
                        case 15:{
                            resourceBar.append("\uE0E7");
                            break;
                        }
                        case 14:{
                            resourceBar.append("\uE0E8");
                            break;
                        }
                        case 13:{
                            resourceBar.append("\uE0E9");
                            break;
                        }
                        case 12:{
                            resourceBar.append("\uE0EA");
                            break;
                        }
                        case 11:{
                            resourceBar.append("\uE0EB");
                            break;
                        }
                        case 10:{
                            resourceBar.append("\uE0EC");
                            break;
                        }
                        case 9:{
                            resourceBar.append("\uE0ED");
                            break;
                        }
                        case 8:{
                            resourceBar.append("\uE0EE");
                            break;
                        }
                        case 7:{
                            resourceBar.append("\uE0EF");
                            break;
                        }
                        case 6:{
                            resourceBar.append("\uE0F0");
                            break;
                        }
                        case 5:{
                            resourceBar.append("\uE0F1");
                            break;
                        }
                        case 4:{
                            resourceBar.append("\uE0F2");
                            break;
                        }
                        case 3:{
                            resourceBar.append("\uE0F3");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE0F4");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE0F5");
                            break;
                        }
                        case 0:{
                            resourceBar.append("\uE0E1");
                            break;
                        }
                    }

                    break;
                }
                case "shadow knight":{
                    //-83 space
                    resourceBar.append("\uF80B\uF809\uF803");

                    double maxEnergy = 100;
                    double currentEnergy = abilityManager.getShadowKnightAbilities().getEnergy().getCurrentEnergy(entity);

                    double ratio = currentEnergy / maxEnergy;

                    int amount = (int) Math.ceil(ratio * 10);

                    if(amount < 0){
                        amount = 0;
                    }

                    if(currentEnergy <= 0){
                        amount = 0;
                    }

                    switch (amount){
                        case 10:{
                            resourceBar.append("\uE0F6");
                            break;
                        }
                        case 9:{
                            resourceBar.append("\uE0F7");
                            break;
                        }
                        case 8:{
                            resourceBar.append("\uE0F8");
                            break;
                        }
                        case 7:{
                            resourceBar.append("\uE0F9");
                            break;
                        }
                        case 6:{
                            resourceBar.append("\uE0FA");
                            break;
                        }
                        case 5:{
                            resourceBar.append("\uE0FB");
                            break;
                        }
                        case 4:{
                            resourceBar.append("\uE0FC");
                            break;
                        }
                        case 3:{
                            resourceBar.append("\uE0FD");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE0FE");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE0FF");
                            break;
                        }
                        case 0:{
                            resourceBar.append("\uE0E1");
                            break;
                        }
                    }

                    break;
                }
                case "ranger":{

                    //-83 space
                    resourceBar.append("\uF80B\uF809\uF803");

                    double maxFocus = 10;
                    double currentFocus = abilityManager.getRangerAbilities().getFocus().getFocus(entity);

                    double ratio = currentFocus / maxFocus;

                    int amount = (int) Math.ceil(ratio * 3);

                    if(amount < 0){
                        amount = 0;
                    }

                    if(currentFocus <= 0){
                        amount = 0;
                    }

                    switch (amount){
                        case 3:{
                            resourceBar.append("\uE100");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE101");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE102");
                            break;
                        }
                        case 0:{
                            resourceBar.append("\uE0E1");
                            break;
                        }
                    }

                    break;
                }
                case "assassin":{

                    //-83
                    resourceBar.append("\uF80B\uF809\uF803");

                    int combo = abilityManager.getAssassinAbilities().getCombo().getComboPoints(entity);

                    if(profileManager.getAnyProfile(entity).getPlayerSubclass().equalsIgnoreCase("duelist")){



                        switch (combo){
                            case 0:{
                                resourceBar.append("\uE108");
                                break;
                            }
                            case 1:{
                                resourceBar.append("\uE109");
                                break;
                            }
                            case 2:{
                                resourceBar.append("\uE10A");
                                break;
                            }
                            case 3:{
                                resourceBar.append("\uE10B");
                                break;
                            }
                            case 4:{
                                resourceBar.append("\uE10C");
                                break;
                            }
                            case 5:{
                                resourceBar.append("\uE008");
                                break;
                            }
                        }

                        break;
                    }

                    switch (combo){
                        case 0:{
                            resourceBar.append("\uE103");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE104");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE105");
                            break;
                        }
                        case 3:{
                            resourceBar.append("\uE106");
                            break;
                        }
                        case 4:{
                            resourceBar.append("\uE107");
                            break;
                        }
                    }

                    break;
                }
                case "elementalist":{

                    //-83 space
                    resourceBar.append("\uF80B\uF809\uF803");

                    double currentHeat = abilityManager.getElementalistAbilities().getHeat().getHeat(entity);

                    if(currentHeat < 33){
                        resourceBar.append("\uE10D");
                        return String.valueOf(resourceBar);
                    }

                    if(currentHeat < 66){
                        resourceBar.append("\uE10E");
                        return String.valueOf(resourceBar);
                    }

                    resourceBar.append("\uE10F");


                    break;
                }
            }

            return String.valueOf(resourceBar);

        }

        resourceBar.append("\uF80B\uF809\uF803");

        int amount = 0;

        if(bossCastingManager.bossIsCasting(entity)){
            double max = bossCastingManager.getCastMax(entity);
            double current = bossCastingManager.getCastPercent(entity);

            double ratio = current / max;

            amount = (int) Math.ceil(ratio * 20);

            if(amount < 0){
                amount = 0;
            }

            if(current <= 0){
                amount = 0;
            }
        }


        switch (amount){
            case 20:{
                resourceBar.append("\uE0E2");
                break;
            }
            case 19:{
                resourceBar.append("\uE0E3");
                break;
            }
            case 18:{
                resourceBar.append("\uE0E4");
                break;
            }
            case 17:{
                resourceBar.append("\uE0E5");
                break;
            }
            case 16:{
                resourceBar.append("\uE0E6");
                break;
            }
            case 15:{
                resourceBar.append("\uE0E7");
                break;
            }
            case 14:{
                resourceBar.append("\uE0E8");
                break;
            }
            case 13:{
                resourceBar.append("\uE0E9");
                break;
            }
            case 12:{
                resourceBar.append("\uE0EA");
                break;
            }
            case 11:{
                resourceBar.append("\uE0EB");
                break;
            }
            case 10:{
                resourceBar.append("\uE0EC");
                break;
            }
            case 9:{
                resourceBar.append("\uE0ED");
                break;
            }
            case 8:{
                resourceBar.append("\uE0EE");
                break;
            }
            case 7:{
                resourceBar.append("\uE0EF");
                break;
            }
            case 6:{
                resourceBar.append("\uE0F0");
                break;
            }
            case 5:{
                resourceBar.append("\uE0F1");
                break;
            }
            case 4:{
                resourceBar.append("\uE0F2");
                break;
            }
            case 3:{
                resourceBar.append("\uE0F3");
                break;
            }
            case 2:{
                resourceBar.append("\uE0F4");
                break;
            }
            case 1:{
                resourceBar.append("\uE0F5");
                break;
            }
            case 0:{
                resourceBar.append("\uE0E1");
                break;
            }
        }


        return String.valueOf(resourceBar);
    }

    private String getUltimateStatus(Player player){

        StringBuilder ultimateStatus = new StringBuilder();

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(!combatStatus){
            return " ";
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return " ";
        }

        //+256
        ultimateStatus.append("\uF82D");

        //-28
        ultimateStatus.append("\uF809\uF808\uF804");


        //move it again, if they have
        //slot on hud
        ultimateStatus.append("\uE12D");

        if(!allSkillItems.getUltimate(player).hasItemMeta()){
            return String.valueOf(ultimateStatus);
        }

        ItemStack ultimateItem = allSkillItems.getUltimate(player);

        if(ultimateItem.getType().equals(Material.AIR)){
            return String.valueOf(ultimateStatus);
        }

        if(!ultimateItem.hasItemMeta()){
            return String.valueOf(ultimateStatus);
        }

        ItemMeta ultimateMeta = ultimateItem.getItemMeta();

        assert ultimateMeta != null;
        if(!ultimateMeta.hasDisplayName()){
            return String.valueOf(ultimateStatus);
        }


        //-20
        ultimateStatus.append("\uF809\uF804");

        String abilityName = ultimateMeta.getDisplayName();
        abilityName = abilityName.replaceAll("§.", "");
        ultimateStatus.append(abilityUnicode(abilityName, player));

        //-17
        ultimateStatus.append("\uF809\uF801");

        //get how much cooldown is left, just here for testing
        ultimateStatus.append(ultimateCooldown(player));


        return String.valueOf(ultimateStatus);
    }

    private String abilityUnicode(String abilityName, Player player){

        StringBuilder unicode = new StringBuilder();

        switch (abilityName.toLowerCase()){

            case "conjuring force":{
                unicode.append("\uE043");
                break;
            }
            case "fiery wing":{
                unicode.append("\uE044");
                break;
            }
            case "wild roar":{
                unicode.append("\uE045");
                break;
            }
            case "star volley":{
                unicode.append("\uE040");
                break;
            }
            case "annihilation":{

                if(abilityManager.getShadowKnightAbilities().getAnnihilation().returnWhichItem(player) == 0){
                    unicode.append("\uE12E");
                }
                else{
                    unicode.append("\uE06D");
                }


                break;
            }
            case "blood shield":{

                if(abilityManager.getShadowKnightAbilities().getBloodShield().returnWhichItem(player) == 0){
                    unicode.append("\uE042");
                }
                else{
                    unicode.append("\uE06E");
                }

                break;
            }
            case "arcane missiles":{
                unicode.append("\uE046");
                break;
            }
            case "enlightenment":{

                if(abilityManager.getMysticAbilities().getEnlightenment().returnWhichItem(player) == 0){
                    unicode.append("\uE047");
                }
                else{
                    unicode.append("\uE06C");
                }

                break;
            }
            case "duelist's frenzy":{


                if(abilityManager.getAssassinAbilities().getDuelistsFrenzy().returnWhichItem(player)==0){
                    unicode.append("\uE049");
                }
                else{
                    unicode.append("\uE04A");
                }


                break;
            }
            case "wicked concoction":{
                unicode.append("\uE04B");
                break;
            }
            case "gladiator heart":{
                unicode.append("\uE058");
                break;
            }
            case "death gaze":{
                unicode.append("\uE059");
                break;
            }
            case "well of light":{
                unicode.append("\uE067");
                break;
            }
            case "shield of sanctity":{
                unicode.append("\uE068");
                break;
            }
            case "representative":{
                unicode.append("\uE069");
                break;
            }
        }

        return String.valueOf(unicode);
    }

    private String ultimateCooldown(Player player){

        StringBuilder unicode = new StringBuilder();


        int percent = iconCalculator.calculate(abilityManager.getPlayerUltimateCooldown(player), abilityManager.getUltimateCooldown(player));

        //Bukkit.getLogger().info("percent " + percent);


        switch (percent){
            case 8:{
                return "\uE12F";
            }
            case 7:{
                return "\uE130";
            }
            case 6:{
                return "\uE131";
            }
            case 5:{
                return "\uE132";
            }
            case 4:{
                return "\uE133";
            }
            case 3:{
                return "\uE134";
            }
            case 2:{
                return "\uE135";
            }
            case 1:{
                return "\uE136";
            }
            case 0:{
                return "\uE137";
            }
        }


        return String.valueOf(unicode);
    }

}
