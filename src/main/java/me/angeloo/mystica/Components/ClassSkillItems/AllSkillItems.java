package me.angeloo.mystica.Components.ClassSkillItems;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class AllSkillItems {

    private final ProfileManager profileManager;

    private final ElementalistSkillItems elementalistSkillItems;
    private final RangerSkillItems rangerSkillItems;

    public AllSkillItems(Mystica main){
        profileManager = main.getProfileManager();
        elementalistSkillItems = new ElementalistSkillItems(main);
        rangerSkillItems = new RangerSkillItems(main);
    }

    public ItemStack getPlayerSkill(Player player, int skillNumber){

        if(profileManager.getIfClassTrial(player)){
            return getTrialSkill(player, skillNumber);
        }

        Profile playerProfile = profileManager.getAnyProfile(player);
        String clazz = playerProfile.getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                return elementalistSkillItems.getSkill(skillNumber, player);
            }
            case "ranger":{
                return rangerSkillItems.getSkill(skillNumber, player);
            }
        }

        return new ItemStack(Material.AIR);
    }

    private ItemStack getTrialSkill(Player player, int skillNumber){

        String trialClass = profileManager.getTrialClass(player);

        switch (trialClass.toLowerCase()){
            case "elementalist":{
                return elementalistSkillItems.getSkill(skillNumber, player);
            }
            case "ranger":{
                return rangerSkillItems.getSkill(skillNumber, player);
            }
        }

        return new ItemStack(Material.AIR);

    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        String clazz = playerProfile.getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                return elementalistSkillItems.getUltimate(player);
            }
            case "ranger":{
                return rangerSkillItems.getUltimate(player);
            }
        }

        return new ItemStack(Material.AIR);
    }

}
