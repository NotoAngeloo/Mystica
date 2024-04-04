package me.angeloo.mystica.Components.ClassSkillItems;


import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.Skill_Level;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static me.angeloo.mystica.Mystica.*;

public class ElementalistSkillItems {

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;

    public ElementalistSkillItems(Mystica main){
        profileManager = main.getProfileManager();
        abilityManager = main.getAbilityManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return getItem(1,
                        ChatColor.of(elementalistColor) + "Ice Bolt",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getIceBolt().getSkillDamage(player)) + " damage",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getIceBolt().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Channel water to form an ice arrow",
                        ChatColor.of(Color.WHITE) + "and shoot it at the enemy");
            }
            case 2:{
                return getItem(2,
                        ChatColor.of(elementalistColor) + "Fiery Magma",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getFieryMagma().getSkillDamage(player)) + " damage +  (" +
                                abilityManager.getElementalistAbilities().getFieryMagma().getSkillDamage(player) * .10 + " x 3)",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getFieryMagma().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a burning meteorite from the sky",
                        ChatColor.of(Color.WHITE) + "to hit the enemy, dealing sustained",
                        ChatColor.of(Color.WHITE) + "damage for 3 seconds before exploding,",
                        ChatColor.of(Color.WHITE) + "dealing damage to nearby enemies");
            }
            case 3:{
                return getItem(3,
                        ChatColor.of(elementalistColor) + "Descending Inferno",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getDescendingInferno().getSkillDamage(player)) + " x 3 damage",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getDescendingInferno().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Turn a thick strand of flame energy",
                        ChatColor.of(Color.WHITE) + "into three blazing fireballs which hurtle",
                        ChatColor.of(Color.WHITE) + "towards the target, causing damage on",
                        ChatColor.of(Color.WHITE) + "impact");
            }
            case 4:{
                return getItem(4,
                        ChatColor.of(elementalistColor) + "Windrush Form",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getWindrushForm().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Calls a strong wind to envelop the",
                        ChatColor.of(Color.WHITE) + "caster, expediting movement.");
            }
            case 5:{
                return getItem(5,
                        ChatColor.of(elementalistColor) + "Wind Wall",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getWindWall().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "The dancing airstream forms a wind",
                        ChatColor.of(Color.WHITE) + "shield, absorbing and reflecting",
                        ChatColor.of(Color.WHITE) + "a certain amount of damage");
            }
            case 6:{
                return getItem(6,
                        ChatColor.of(elementalistColor) + "Dragon Breathing",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getDragonBreathing().getSkillDamage(player)) + " damage + " +
                                (abilityManager.getElementalistAbilities().getDragonBreathing().getSkillDamage(player) * .1) + " x 5",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getDragonBreathing().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Channel the fire elements beneath",
                        ChatColor.of(Color.WHITE) + "your feet, summoning a fire dragon to",
                        ChatColor.of(Color.WHITE) + "spray forth its fiery breath, causing",
                        ChatColor.of(Color.WHITE) + "damage to the enemy on the path",
                        ChatColor.of(Color.WHITE) + "ahead, and causing affected enemies",
                        ChatColor.of(Color.WHITE) + "to continue burning for 5 seconds");
            }
            case 7:{
                return getItem(7,
                        ChatColor.of(elementalistColor) + "Elemental Breath",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getElementalBreath().getCost()) + " mana",
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
            case 8:{
                return getItem(8,
                        ChatColor.of(elementalistColor) + "Elemental Matrix",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getElemental_matrix().getSkillDamage(player)) + " damage x 2",
                        ChatColor.of(elementalistColor) + "heals 5% health + mana",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getElemental_matrix().getCost()) + " mana",
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
        String subClass = playerProfile.getPlayerSubclass();

        switch(subClass.toLowerCase()){
            case "pyromancer":{
                return getItem(9,
                        ChatColor.of(elementalistColor) + "Fiery Wing",
                        ChatColor.of(levelColor) + "Level "  + level,
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getFieryWing().getSkillDamage(player)) + " damage",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getFieryWing().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon an elemental that charges",
                        ChatColor.of(Color.WHITE) + "towards an enemy, dealing massive",
                        ChatColor.of(Color.WHITE) + "damage on arrival");
            }
            case "conjurer":{
                return getItem(10,
                        ChatColor.of(elementalistColor) + "Conjuring Force",
                        ChatColor.of(levelColor) + "Level "  + level,
                        ChatColor.of(elementalistColor) + String.valueOf(abilityManager.getElementalistAbilities().getConjuringForce().getBuffAmount(player)) + " damage added",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getElementalistAbilities().getConjuringForce().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a force shield centered around",
                        ChatColor.of(Color.WHITE) + "you, enhancing damage and range of all",
                        ChatColor.of(Color.WHITE) + "allies within");
            }
        }

        return new ItemStack(Material.AIR);
    }

    private ItemStack getItem(int modelData, String name, String ... lore){

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);

        ItemStack item = new ItemStack(Material.CYAN_DYE);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);

        List<String> lores = new ArrayList<>(Arrays.asList(lore));

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroer);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }

}
