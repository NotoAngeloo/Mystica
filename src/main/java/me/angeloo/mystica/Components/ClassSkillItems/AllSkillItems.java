package me.angeloo.mystica.Components.ClassSkillItems;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
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

        PlayerClass playerClass = playerProfile.getPlayerClass();

        switch (playerClass) {
            case Elementalist -> {
                return elementalistSkillItems.getSkill(skillNumber, player);
            }
            case Ranger -> {
                return rangerSkillItems.getSkill(skillNumber, player);
            }
            case Mystic -> {
                return mysticSkillItem.getSkill(skillNumber, player);
            }
            case Shadow_Knight -> {
                return shadowKnightSkillItems.getSkill(skillNumber, player);
            }
            case Paladin -> {
                return paladinSkillItems.getSkill(skillNumber, player);
            }
            case Warrior -> {
                return warriorSkillItems.getSkill(skillNumber, player);
            }
            case Assassin -> {
                return assassinSkillItems.getSkill(skillNumber, player);
            }
            case NONE -> {
                return noneSkillItems.getSkill(skillNumber, player);
            }
        }

        return new ItemStack(Material.AIR);
    }


    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                return elementalistSkillItems.getUltimate(player);
            }
            case Ranger -> {
                return rangerSkillItems.getUltimate(player);
            }
            case Mystic -> {
                return mysticSkillItem.getUltimate(player);
            }
            case Shadow_Knight -> {
                return shadowKnightSkillItems.getUltimate(player);
            }
            case Paladin -> {
                return paladinSkillItems.getUltimate(player);
            }
            case Warrior -> {
                return warriorSkillItems.getUltimate(player);
            }
            case Assassin -> {
                return assassinSkillItems.getUltimate(player);
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getBasic(Player player){
        Profile playerProfile = profileManager.getAnyProfile(player);
        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz){
            case Elementalist:{
                return elementalistSkillItems.getBasic(player);
            }
            case Ranger:{
                return rangerSkillItems.getBasic(player);
            }
            case Mystic:{
                return mysticSkillItem.getBasic(player);
            }
            case Shadow_Knight:{
                return shadowKnightSkillItems.getBasic(player);
            }
            case Paladin:{
                return paladinSkillItems.getBasic(player);
            }
            case Warrior:{
                return warriorSkillItems.getBasic(player);
            }
            case Assassin:{
                return assassinSkillItems.getBasic(player);
            }
        }

        return new ItemStack(Material.AIR);
    }

}
