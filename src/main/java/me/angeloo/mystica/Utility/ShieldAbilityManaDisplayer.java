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
        allSkillItems = new AllSkillItems(main);
    }

    public void displayPlayerHealthPlusInfo(Player player){

        StringBuilder fullBar = new StringBuilder();

        String shieldString = getShieldString(player);
        String manaString = getManaBar(player);
        String statusString = ChatColor.GRAY + getPlayerStatus(player);

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
        double shieldAmount = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(player);
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

    private String getPlayerStatus(Player player){

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(!combatStatus){
            return " ";
        }

        int hotBarSlot = player.getInventory().getHeldItemSlot();

        int cooldown;

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        if(hotBarSlot == 8){

            if(!allSkillItems.getUltimate(player).hasItemMeta()){
                return " ";
            }

            cooldown = abilityManager.getUltimateCooldown(player);

            if(cooldown <= 0){

                ItemStack ultimateItem = allSkillItems.getUltimate(player);

                if(ultimateItem.getType().equals(Material.AIR)){
                    return " ";
                }

                String abilityName = ultimateItem.getItemMeta().getDisplayName();

                abilityName = abilityName.replaceAll("§.", "");


                return abilityName;
            }

            return String.valueOf(cooldown);
        }

        int abilityNumber = equipSkills.getAnySlot()[hotBarSlot];

        if(abilityNumber <=0){
            return " ";
        }

        cooldown = abilityManager.getCooldown(player, abilityNumber);

        if(cooldown <=0){

            String abilityName = allSkillItems.getPlayerSkill(player, abilityNumber).getItemMeta().getDisplayName();
            abilityName = abilityName.replaceAll("§.", "");

            return abilityName;
        }

        return String.valueOf(cooldown);

    }

}
