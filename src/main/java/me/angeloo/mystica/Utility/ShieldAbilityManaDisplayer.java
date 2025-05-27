package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.kyori.adventure.platform.facet.Facet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

import static me.angeloo.mystica.Mystica.*;

public class ShieldAbilityManaDisplayer {

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final AllSkillItems allSkillItems;

    public ShieldAbilityManaDisplayer(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        abilityManager = manager;
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        allSkillItems = abilityManager.getAllSkillItems();
    }

    public void displayPlayerHealthPlusInfo(Player player){

        /*StringBuilder fullBar = new StringBuilder();

        String shieldString = getShieldString(player);
        String manaString = getManaBar(player);
        String statusString = ChatColor.GRAY + getUltimateStatus(player);

        String colorlessString = statusString.replaceAll("§.", "");

        int amountStatusChar = colorlessString.length();
        //make sure the bar is always 25 chars long
        int leftToSpaceOnEachSide = 25 - amountStatusChar;

        leftToSpaceOnEachSide = leftToSpaceOnEachSide/2;

        StringBuilder fillerString = new StringBuilder();
        for(int i=0;i<leftToSpaceOnEachSide;i++){
            fillerString.append(" ");
        }

        //replace " " with fillerString
        fullBar.append(shieldString).append(fillerString).append(statusString).append(fillerString).append(manaString);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(fullBar)));
    }

    private String getShieldString(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        double maxHp = playerProfile.getTotalHealth();
        double shieldAmount = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(player) +
                buffAndDebuffManager.getWindWallBuff().getWallHealth(player);
        int percent = (int) Math.floor((shieldAmount/maxHp) * 100);

        StringBuilder shieldBar = new StringBuilder().append(ChatColor.YELLOW);

        for(int i = 0; i<15 ; i++){
            if(percent > (i*((double) 100/15))){
                shieldBar.append("||");
            }
            else{
                shieldBar.append(" ");
            }
        }

        return String.valueOf(shieldBar);*/
    }


    private String getManaBar(Player player){

        double max = 500;
        double current = 0;

        StringBuilder manaBar = new StringBuilder();

        switch (profileManager.getAnyProfile(player).getPlayerClass().toLowerCase()){
            case "shadow knight":{
                //manaBar.append(ChatColor.of(shadowKnightColor));
                manaBar.append(ChatColor.DARK_RED);
                max = 100;
                current = abilityManager.getShadowKnightAbilities().getEnergy().getCurrentEnergy(player);
                break;
            }
            case "mystic":{

                //manaBar.append(ChatColor.of(mysticColor));
                manaBar.append(ChatColor.DARK_PURPLE);

                if(!profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("chaos")){
                    current = abilityManager.getMysticAbilities().getMana().getCurrentMana(player);
                    break;
                }

                break;
            }
            case "warrior":{
                //manaBar.append(ChatColor.of(warriorColor));
                manaBar.append(ChatColor.RED);
                current = abilityManager.getWarriorAbilities().getRage().getCurrentRage(player);
                break;
            }
            case "ranger":{
                manaBar.append(ChatColor.GREEN);
                max = 10;
                current = abilityManager.getRangerAbilities().getFocus().getFocus(player);
                //manaBar.append(ChatColor.of(rangerColor));
                break;
            }
            case "elementalist":{
                //manaBar.append(ChatColor.of(elementalistColor));
                manaBar.append(ChatColor.BLUE);
                max = 100;
                current = abilityManager.getElementalistAbilities().getHeat().getHeat(player);
                break;
            }
        }

        int percent = (int) Math.floor((current/max) * 100);

        for(int i = 0; i<15 ; i++){
            if(percent > (i*((double) 100/15))){
                manaBar.append("||");
            }
            else{
                manaBar.append(" ");
            }
        }


        return String.valueOf(manaBar);
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
