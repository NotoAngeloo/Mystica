package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.IconCalculator;
import me.angeloo.mystica.Utility.SkinGrabber;
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


        /*BossBar teamBar = Bukkit.createBossBar("Team", BarColor.WHITE, BarStyle.SOLID);
        teamBar.addPlayer(player);
        teamBar.setVisible(true);

        BossBar statusBar = Bukkit.createBossBar("Status", BarColor.WHITE, BarStyle.SOLID);
        statusBar.addPlayer(player);
        statusBar.setVisible(true);


         */

    }

    public void displayUltimate(Player player){

        StringBuilder hotBar = new StringBuilder();

        String statusString = getUltimateStatus(player);

        hotBar.append(statusString);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(hotBar)));
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





        /*

        //if player not in a team, sill have the bar, but have no data in it

        //can change color via chatcolor good to know
        //teamData.append(ChatColor.of(assassinColor));


        //-512space
        teamData.append("\uF80E");

        teamData.append(ChatColor.RESET);
        teamData.append("\uE138");

        teamData.append(" ");

        teamData.append(ChatColor.RESET);
        teamData.append("\uE138");

        teamData.append(" ");

        teamData.append(ChatColor.RESET);
        teamData.append("\uE138");


        //-107
        teamData.append("\uF80B\uF80A\uF808\uF803");

        teamData.append(ChatColor.RESET);
        teamData.append("\uE139");

        teamData.append(" ");

        teamData.append(ChatColor.RESET);
        teamData.append("\uE139");

        teamData.append(" ");

        teamData.append(ChatColor.RESET);
        teamData.append("\uE139");


        //-107
        teamData.append("\uF80B\uF80A\uF808\uF803");

        teamData.append(ChatColor.RESET);
        teamData.append("\uE13A");

        teamData.append(" ");

        teamData.append(ChatColor.RESET);
        teamData.append("\uE13A");

        teamData.append(" ");

        teamData.append(ChatColor.RESET);
        teamData.append("\uE13A");*/


        return String.valueOf(teamData);
    }

    private String getTeamMemberDataString(LivingEntity entity, int slot){

        StringBuilder entityBar = new StringBuilder();

        //this grabs the frame of the right y value
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

            //currently, these are default steve faces
            switch (slot){
                case 0:{
                    entityBar.append("\uE14E");
                    break;
                }
                case 1:{
                    entityBar.append("\uE179");
                    break;
                }
                case 2:{
                    entityBar.append("\uE1A4");
                    break;
                }
                case 3:{
                    entityBar.append("\uE1CF");
                    break;
                }

            }


            //+3 is only because default steve face.
            entityBar.append("\uF823");
        }

        //change this to get the right value
        entityBar.append(teamHealthBar(entity, slot));

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

            //default frame, change later
            icon.append("\uE143");

            //-29
            icon.append("\uF809\uF808\uF805");


            icon.append(skinGrabber.getFace(player));

            //+29
            icon.append("\uF829\uF828\uF825");
            return String.valueOf(icon);
        }

        //removed entity instance of player
        if(profileManager.getAnyProfile(entity).fakePlayer()){
            //TODO: if player has cosmetic equipped, equip that instead

            //default frame
            icon.append("\uE143");

            //-29
            icon.append("\uF809\uF808\uF805");

            //default face, for testing
            icon.append("\uE144");

            //+3
            icon.append("\uF823");
            return String.valueOf(icon);

            /*Profile playerProfile = profileManager.getAnyProfile(entity);
            String playerClass = playerProfile.getPlayerClass();

            //change this to companion specific icons

            //these are default ones
            switch (playerClass.toLowerCase()){
                case "ranger":{
                    icon.append(("\uE124"));
                    return String.valueOf(icon);
                }
                case "mystic":{
                    icon.append(("\uE125"));
                    return String.valueOf(icon);
                }
                case "paladin":{
                    icon.append(("\uE126"));
                    return String.valueOf(icon);
                }
                case "assassin":{
                    icon.append(("\uE129"));
                    return String.valueOf(icon);
                }
                case "warrior":{
                    icon.append(("\uE12A"));
                    return String.valueOf(icon);
                }
                case "elementalist":{
                    icon.append(("\uE12B"));
                    return String.valueOf(icon);
                }
                case "shadow knight":{
                    icon.append(("\uE12C"));
                    return String.valueOf(icon);
                }
            }*/
        }

        if(profileManager.getAnyProfile(entity).getIsPassive()){
            icon.append(("\uE127"));
            return String.valueOf(icon);
        }



        //have a default icon too
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
