package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        StringBuilder fullBar = new StringBuilder();

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

        return String.valueOf(shieldBar);
    }


    private String getManaBar(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        double maxMp = playerProfile.getTotalMana();
        double currentMp = playerProfile.getCurrentMana();
        int percent = (int) Math.floor((currentMp/maxMp) * 100);

        StringBuilder manaBar = new StringBuilder();

        if(profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("shadow knight")){
            manaBar.append(ChatColor.DARK_RED);
        }
        else{
            manaBar.append(ChatColor.BLUE);
        }



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

            //TODO: a unicode character for each ultimate

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
                unicode.append("\uE041");
                break;
            }
            case "blood shield":{
                unicode.append("\uE042");
                break;
            }
            case "arcane missiles":{
                unicode.append("\uE046");
                break;
            }
            case "enlightenment":{
                unicode.append("\uE047");
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
            default:{
                unicode.append(abilityName);
                break;
            }
        }

        return String.valueOf(unicode);
    }


}
