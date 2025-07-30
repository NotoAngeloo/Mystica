package me.angeloo.mystica.Components.ClassSkillItems;


import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.Skill_Level;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Utility.EquipmentSlot;
import me.angeloo.mystica.Utility.InventoryItemGetter;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static me.angeloo.mystica.Mystica.*;

public class ElementalistSkillItems {

    private final MysticaEquipment weapon;
    private final ProfileManager profileManager;
    private final InventoryItemGetter itemGetter;
    private final AbilityManager abilityManager;

    public ElementalistSkillItems(Mystica main, AbilityManager manager){
        weapon = new MysticaEquipment(EquipmentSlot.WEAPON, PlayerClass.Elementalist, 1);
        profileManager = main.getProfileManager();
        itemGetter = main.getItemGetter();
        abilityManager = manager;
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch (number) {
            case 1 -> {
                return itemGetter.getItem(Material.CYAN_DYE, 1,
                        ChatColor.of(elementalistColor) + "Ice Bolt",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getIceBolt().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Channel water to form an ice arrow",
                        ChatColor.of(Color.WHITE) + "and shoot it at the enemy");
            }
            case 2 -> {
                return itemGetter.getItem(Material.CYAN_DYE, 2,
                        ChatColor.of(elementalistColor) + "Fiery Magma",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getFieryMagma().getSkillDamage(player)) + " power +  (" +
                                abilityManager.getElementalistAbilities().getFieryMagma().getSkillDamage(player) * .10 + " x 3)",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a burning meteorite from the sky",
                        ChatColor.of(Color.WHITE) + "to hit the enemy, dealing sustained",
                        ChatColor.of(Color.WHITE) + "damage for 3 seconds before exploding,",
                        ChatColor.of(Color.WHITE) + "dealing damage to nearby enemies");
            }
            case 3 -> {
                return itemGetter.getItem(Material.CYAN_DYE, 3,
                        ChatColor.of(elementalistColor) + "Descending Inferno",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getDescendingInferno().getSkillDamage(player)) + " x 3 damage",
                        "",
                        ChatColor.of(Color.WHITE) + "Turn a thick strand of flame energy",
                        ChatColor.of(Color.WHITE) + "into three blazing fireballs which hurtle",
                        ChatColor.of(Color.WHITE) + "towards the target, causing damage on",
                        ChatColor.of(Color.WHITE) + "impact");
            }
            case 4 -> {
                return itemGetter.getItem(Material.CYAN_DYE, 4,
                        ChatColor.of(elementalistColor) + "Windrush Form",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Calls a strong wind to envelop the",
                        ChatColor.of(Color.WHITE) + "caster, expediting movement.");
            }
            case 5 -> {
                return itemGetter.getItem(Material.CYAN_DYE, 5,
                        ChatColor.of(elementalistColor) + "Wind Wall",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "The dancing airstream forms a wind",
                        ChatColor.of(Color.WHITE) + "shield, absorbing and reflecting",
                        ChatColor.of(Color.WHITE) + "a certain amount of damage");
            }
            case 6 -> {
                return itemGetter.getItem(Material.CYAN_DYE, 6,
                        ChatColor.of(elementalistColor) + "Dragon Breathing",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getDragonBreathing().getSkillDamage(player)) + " power + " +
                                (abilityManager.getElementalistAbilities().getDragonBreathing().getSkillDamage(player) * .1) + " x 5",
                        "",
                        ChatColor.of(Color.WHITE) + "Channel the fire elements beneath",
                        ChatColor.of(Color.WHITE) + "your feet, summoning a fire dragon to",
                        ChatColor.of(Color.WHITE) + "spray forth its fiery breath, causing",
                        ChatColor.of(Color.WHITE) + "damage to the enemy on the path",
                        ChatColor.of(Color.WHITE) + "ahead, and causing affected enemies",
                        ChatColor.of(Color.WHITE) + "to continue burning for 5 seconds");
            }
            case 7 -> {
                return itemGetter.getItem(Material.CYAN_DYE, 7,
                        ChatColor.of(elementalistColor) + "Elemental Breath",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Listen to the elements, surround",
                        ChatColor.of(Color.WHITE) + "yourself with their force, enhancing the",
                        ChatColor.of(Color.WHITE) + "effects of " + ChatColor.of(elementalistColor) + "Ice Bolt "
                                + ChatColor.of(Color.WHITE) + "and " + ChatColor.of(elementalistColor) + "Descending",
                        ChatColor.of(elementalistColor) + "Inferno" + ChatColor.of(Color.WHITE) + ". " +
                                ChatColor.of(elementalistColor) + "Ice Bolt " + ChatColor.of(Color.WHITE) + "becomes " +
                                ChatColor.of(elementalistColor) + "Ice Lance",
                        ChatColor.of(Color.WHITE) + "dealing double damage. Fireballs from",
                        ChatColor.of(elementalistColor) + "Descending Inferno "
                                + ChatColor.of(Color.WHITE) + "explode upon",
                        ChatColor.of(Color.WHITE) + "hitting targets, dealing area of effect",
                        ChatColor.of(Color.WHITE) + "damage");
            }
            case 8 -> {
                return itemGetter.getItem(Material.CYAN_DYE, 8,
                        ChatColor.of(elementalistColor) + "Elemental Matrix",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getElemental_matrix().getSkillDamage(player)) + " power x 2",
                        ChatColor.of(elementalistColor) + "heals 5% health",
                        "",
                        ChatColor.of(Color.WHITE) + "Condense the elements to create a",
                        ChatColor.of(Color.WHITE) + "magic circle under the enemy's feet,",
                        ChatColor.of(Color.WHITE) + "dealing sustained damage to affected",
                        ChatColor.of(Color.WHITE) + "enemies for 5 seconds, while providing",
                        ChatColor.of(Color.WHITE) + "a healing effect for allies. When the",
                        ChatColor.of(Color.WHITE) + "circle expires, it explodes, inflicting",
                        ChatColor.of(Color.WHITE) + "high damage in the area");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        int level = playerProfile.getStats().getLevel();
        SubClass subClass = playerProfile.getPlayerSubclass();

        switch(subClass){
            case Pyromancer:{
                return itemGetter.getItem(Material.CYAN_DYE,9,
                        ChatColor.of(elementalistColor) + "Fiery Wing",
                        ChatColor.of(levelColor) + "Level "  + level,
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getFieryWing().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon an elemental that charges",
                        ChatColor.of(Color.WHITE) + "towards an enemy, dealing massive",
                        ChatColor.of(Color.WHITE) + "damage on arrival");
            }
            case Conjurer:{
                return itemGetter.getItem(Material.CYAN_DYE,10,
                        ChatColor.of(elementalistColor) + "Conjuring Force",
                        ChatColor.of(levelColor) + "Level "  + level,
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getConjuringForce().getBuffAmount(player)) + " damage added",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a force shield centered around",
                        ChatColor.of(Color.WHITE) + "you, enhancing damage and range of all",
                        ChatColor.of(Color.WHITE) + "allies within");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getBasic(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        ItemStack basicItem = weapon.build();

        ItemMeta basicMeta = basicItem.getItemMeta();
        List<String> basicLore = new ArrayList<>();

        int level = playerProfile.getStats().getLevel();
        basicLore.add(ChatColor.of(levelColor) + "Level "  + level);
        basicLore.add(ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getElementalistBasic().getSkillDamage(player)) + " power");

        assert basicMeta != null;
        basicMeta.setLore(basicLore);
        basicItem.setItemMeta(basicMeta);


        return basicItem;
    }



}
