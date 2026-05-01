package me.angeloo.mystica.Components.CombatSystem.ClassSkillItems;

import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.InventoryItemGetter;

public class NoneSkillItems {

    private final ProfileManager profileManager;
    private final InventoryItemGetter itemGetter;

    public NoneSkillItems(Mystica main){
        profileManager = main.getProfileManager();
        itemGetter = main.getItemGetter();
    }

    /*public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number) {

            case 1: {
                return itemGetter.getItem(Material.WHITE_DYE, 1,
                        ChatColor.of(Color.WHITE) + "Kick",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Kick your enemy for slightly more",
                        ChatColor.of(Color.WHITE) + "damage than punching them");
            }
            case 2: {
                return itemGetter.getItem(Material.WHITE_DYE, 2,
                        ChatColor.of(Color.WHITE) + "Dash",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Increase your movement speed.");
            }
            case 3: {
                return itemGetter.getItem(Material.WHITE_DYE, 3,
                        ChatColor.of(Color.WHITE) + "Roll",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Roll in the direction you are looking.");
            }
        }

        return new ItemStack(Material.AIR);
    }*/



}
