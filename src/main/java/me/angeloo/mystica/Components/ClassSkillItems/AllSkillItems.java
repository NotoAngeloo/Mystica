package me.angeloo.mystica.Components.ClassSkillItems;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class AllSkillItems {

    private final ProfileManager profileManager;

    private final NoneSkillItems noneSkillItems;
    private final ElementalistSkillItems elementalistSkillItems;
    private final RangerSkillItems rangerSkillItems;
    private final MysticSkillItem mysticSkillItem;
    private final ShadowKnightSkillItems shadowKnightSkillItems;
    private final PaladinSkillItems paladinSkillItems;
    private final WarriorSkillItems warriorSkillItems;
    private final AssassinSkillItems assassinSkillItems;

    public AllSkillItems(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        noneSkillItems = new NoneSkillItems(main);
        elementalistSkillItems = new ElementalistSkillItems(main, manager);
        rangerSkillItems = new RangerSkillItems(main, manager);
        mysticSkillItem = new MysticSkillItem(main, manager);
        shadowKnightSkillItems = new ShadowKnightSkillItems(main, manager);
        paladinSkillItems = new PaladinSkillItems(main, manager);
        warriorSkillItems = new WarriorSkillItems(main, manager);
        assassinSkillItems = new AssassinSkillItems(main, manager);

    }

    public ItemStack getPlayerSkill(Player player, int skillNumber){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz= playerProfile.getPlayerClass();

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
            case "shadow knight":{
                return shadowKnightSkillItems.getSkill(skillNumber, player);
            }
            case "paladin":{
                return paladinSkillItems.getSkill(skillNumber, player);
            }
            case "warrior":{
                return warriorSkillItems.getSkill(skillNumber, player);
            }
            case "assassin":{
                return assassinSkillItems.getSkill(skillNumber, player);
            }
            case "none":{
                return noneSkillItems.getSkill(skillNumber, player);
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
            case "shadow knight":{
                return shadowKnightSkillItems.getUltimate(player);
            }
            case "paladin":{
                return paladinSkillItems.getUltimate(player);
            }
            case "warrior":{
                return warriorSkillItems.getUltimate(player);
            }
            case "assassin":{
                return assassinSkillItems.getUltimate(player);
            }
        }

        return new ItemStack(Material.AIR);
    }

}
