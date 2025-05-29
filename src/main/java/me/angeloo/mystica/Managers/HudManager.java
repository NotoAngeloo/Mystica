package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Mystica;
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

public class HudManager {

    private final ProfileManager profileManager;
    private final AllSkillItems allSkillItems;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final TargetManager targetManager;
    private final BossCastingManager bossCastingManager;


    public HudManager(Mystica main){
        profileManager = main.getProfileManager();
        abilityManager = main.getAbilityManager();
        allSkillItems = main.getAllSkillItems();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        targetManager = main.getTargetManager();
        bossCastingManager = main.getBossCastingManager();
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


        /*BossBar teamBar = Bukkit.createBossBar("Team", BarColor.WHITE, BarStyle.SOLID);
        teamBar.addPlayer(player);
        teamBar.setVisible(true);

        BossBar statusBar = Bukkit.createBossBar("Status", BarColor.WHITE, BarStyle.SOLID);
        statusBar.addPlayer(player);
        statusBar.setVisible(true);

        BossBar resourceBar = Bukkit.createBossBar(createResourceBars(player), BarColor.WHITE, BarStyle.SOLID);
        resourceBar.addPlayer(player);
        resourceBar.setVisible(true);*/

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


    private String createEntityDataString(LivingEntity entity){

        StringBuilder entityData = new StringBuilder();

        entityData.append(characterIcon(entity));
        entityData.append(healthBar(entity));
        entityData.append(resourceBar(entity));

        return String.valueOf(entityData);
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

    private String characterIcon(LivingEntity entity){

        StringBuilder icon = new StringBuilder();

        if(entity instanceof Player || profileManager.getAnyProfile(entity).fakePlayer()){
            //TODO: if player has cosmetic equipped, eqiup that instead

            Profile playerProfile = profileManager.getAnyProfile(entity);
            String playerClass = playerProfile.getPlayerClass();

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
            }
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
                                resourceBar.append("\uE107");
                                break;
                            }
                            case 1:{
                                resourceBar.append("\uE108");
                                break;
                            }
                            case 2:{
                                resourceBar.append("\uE109");
                                break;
                            }
                            case 3:{
                                resourceBar.append("\uE10A");
                                break;
                            }
                            case 4:{
                                resourceBar.append("\uE10B");
                                break;
                            }
                            case 5:{
                                resourceBar.append("\uE10C");
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
        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(!combatStatus){
            return " ";
        }

        if(!allSkillItems.getUltimate(player).hasItemMeta()){
            return " ";
        }

        int cooldown = abilityManager.getUltimateCooldown(player);

        if(cooldown <= 0){

            ItemStack ultimateItem = allSkillItems.getUltimate(player);

            if(ultimateItem.getType().equals(Material.AIR)){
                return " ";
            }

            String abilityName = ultimateItem.getItemMeta().getDisplayName();
            abilityName = abilityName.replaceAll("§.", "");


            return abilityUnicode(abilityName, player);
        }

        return String.valueOf(cooldown);
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
                    unicode.append("\uE041");
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
            default:{
                unicode.append(abilityName);
                break;
            }
        }

        return String.valueOf(unicode);
    }

}
