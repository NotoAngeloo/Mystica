package me.angeloo.mystica.Components.ClassSkillItems;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
import me.angeloo.mystica.Components.Abilities.Paladin.PaladinBasic;
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
    private final ShadowKnightSkillItems shadowKnightSkillItems;
    private final PaladinSkillItems paladinSkillItems;
    private final WarriorSkillItems warriorSkillItems;
    private final AssassinSkillItems assassinSkillItems;

    public AllSkillItems(Mystica main){
        profileManager = main.getProfileManager();
        elementalistSkillItems = new ElementalistSkillItems(main);
        rangerSkillItems = new RangerSkillItems(main);
        mysticSkillItem = new MysticSkillItem(main);
        shadowKnightSkillItems = new ShadowKnightSkillItems(main);
        paladinSkillItems = new PaladinSkillItems(main);
        warriorSkillItems = new WarriorSkillItems(main);
        assassinSkillItems = new AssassinSkillItems(main);
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
