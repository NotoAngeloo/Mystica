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
    private final MysticSkillItem mysticSkillItem;

    public AllSkillItems(Mystica main){
        profileManager = main.getProfileManager();
        elementalistSkillItems = new ElementalistSkillItems(main);
        rangerSkillItems = new RangerSkillItems(main);
        mysticSkillItem = new MysticSkillItem(main);
    }

    public ItemStack getPlayerSkill(Player player, int skillNumber){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz;

        if(profileManager.getIfClassTrial(player)){
            clazz = profileManager.getTrialClass(player);
        }
        else{
            clazz = playerProfile.getPlayerClass();
        }

        switch (clazz.toLowerCase()){
            case "elementalist":{
                return elementalistSkillItems.getSkill(skillNumber, player);
            }
            case "ranger":{
                return rangerSkillItems.getSkill(skillNumber, player);
            }
            case "mystic":{
                return mysticSkillItem.getSkill(skillNumber, player);
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
            case "mystic":{
                return mysticSkillItem.getUltimate(player);
            }
        }

        return new ItemStack(Material.AIR);
    }

}
