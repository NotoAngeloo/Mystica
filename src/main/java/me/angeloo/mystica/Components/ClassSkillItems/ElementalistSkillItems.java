package me.angeloo.mystica.Components.ClassSkillItems;


import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.Skill_Level;
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

public class ElementalistSkillItems {

    private final ProfileManager profileManager;

    public ElementalistSkillItems(Mystica main){
        profileManager = main.getProfileManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return getItem(Material.ICE, 0,
                        ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_1_Level() + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Channel water to form an ice arrow",
                        ChatColor.of(new Color(230,230,230)) + "and shoot it at the enemy");
            }
            case 2:{
                return getItem(Material.FIRE_CHARGE, 0,
                        ChatColor.of(new Color(153, 204, 255)) + "Fiery Magma",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_2_Level() + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon a burning meteorite from the sky",
                        ChatColor.of(new Color(230,230,230)) + "to hit the enemy, dealing sustained",
                        ChatColor.of(new Color(230,230,230)) + "damage for 3 seconds before exploding,",
                        ChatColor.of(new Color(230,230,230)) + "dealing damage to nearby enemies");
            }
            case 3:{
                return getItem(Material.FLINT_AND_STEEL, 0,
                        ChatColor.of(new Color(153, 204, 255)) + "Descending Inferno",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_3_Level() + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Turn a thick strand of flame energy",
                        ChatColor.of(new Color(230,230,230)) + "into three blazing fireballs which hurtle",
                        ChatColor.of(new Color(230,230,230)) + "towards the target, causing damage on",
                        ChatColor.of(new Color(230,230,230)) + "impact");
            }
            case 4:{
                return getItem(Material.FEATHER, 0,
                        ChatColor.of(new Color(153, 204, 255)) + "Windrush Form",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_4_Level() + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Calls a strong wind to envelop the",
                        ChatColor.of(new Color(230,230,230)) + "caster, expediting movement and",
                        ChatColor.of(new Color(230,230,230)) + "reducing incoming damage while",
                        ChatColor.of(new Color(230,230,230)) + "damaging enemies on the target path");
            }
            case 5:{
                return getItem(Material.CACTUS, 0,
                        ChatColor.of(new Color(153, 204, 255)) + "Wind Wall",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_5_Level() + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "The dancing airstream forms a wind",
                        ChatColor.of(new Color(230,230,230)) + "shield, absorbing and reflecting",
                        ChatColor.of(new Color(230,230,230)) + "a certain amount of damage");
            }
            case 6:{
                return getItem(Material.DRAGON_HEAD, 0,
                        ChatColor.of(new Color(153, 204, 255)) + "Dragon Breathing",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_6_Level() + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Channel the fire elements beneath",
                        ChatColor.of(new Color(230,230,230)) + "your feet, summoning a fire dragon to",
                        ChatColor.of(new Color(230,230,230)) + "spray forth its fiery breath, causing",
                        ChatColor.of(new Color(230,230,230)) + "damage to the enemy on the path",
                        ChatColor.of(new Color(230,230,230)) + "ahead, and causing affected enemies",
                        ChatColor.of(new Color(230,230,230)) + "to continue burning for 5 seconds");
            }
            case 7:{
                return getItem(Material.DRAGON_BREATH, 0,
                        ChatColor.of(new Color(153, 204, 255)) + "Elemental Breath",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_7_Level() + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Listen to the elements, surround",
                        ChatColor.of(new Color(230,230,230)) + "yourself with their force, enhancing the",
                        ChatColor.of(new Color(230,230,230)) + "effects of " + ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt "
                                + ChatColor.of(new Color(230,230,230)) + "and " + ChatColor.of(new Color(153, 204, 255)) + "Descending",
                        ChatColor.of(new Color(153, 204, 255)) + "Inferno" + ChatColor.of(new Color(230,230,230)) + ". " +
                        ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt " + ChatColor.of(new Color(230,230,230)) + "becomes " +
                                ChatColor.of(new Color(153, 204, 255)) + "Ice Lance",
                        ChatColor.of(new Color(230,230,230)) + "dealing double damage. Fireballs from",
                        ChatColor.of(new Color(153, 204, 255)) + "Descending Inferno "
                                + ChatColor.of(new Color(230,230,230)) + "explode upon",
                        ChatColor.of(new Color(230,230,230)) + "hitting targets, dealing area of effect",
                        ChatColor.of(new Color(230,230,230)) + "damage");
            }
            case 8:{
                return getItem(Material.REDSTONE, 0,
                        ChatColor.of(new Color(153, 204, 255)) + "Elemental Matrix",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_8_Level() + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Condense the elements to create a",
                        ChatColor.of(new Color(230,230,230)) + "magic circle under the enemy's feet,",
                        ChatColor.of(new Color(230,230,230)) + "dealing sustained damage to affected",
                        ChatColor.of(new Color(230,230,230)) + "enemies for 5 seconds, while providing",
                        ChatColor.of(new Color(230,230,230)) + "a healing effect for allies. When the",
                        ChatColor.of(new Color(230,230,230)) + "circle expires, it explodes, inflicting",
                        ChatColor.of(new Color(230,230,230)) + "high damage in the area");
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
                return getItem(Material.TORCH, 0,
                        ChatColor.of(new Color(250, 102, 0)) + "Fiery Wing",
                        ChatColor.of(new Color(0,102,0)) + "Level "  + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon an elemental that charges",
                        ChatColor.of(new Color(230,230,230)) + "towards an enemy, dealing massive",
                        ChatColor.of(new Color(230,230,230)) + "damage on arrival");
            }
            case "cryomancer":{
                return getItem(Material.PRISMARINE_CRYSTALS, 0,
                        ChatColor.of(new Color(153, 204, 255)) + "Crystal Storm",
                        ChatColor.of(new Color(0,102,0)) + "Level "  + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon a storm cloud that pelts",
                        ChatColor.of(new Color(230,230,230)) + "the area with icicles. Hitting",
                        ChatColor.of(new Color(230,230,230)) + "an enemy effected by the storm",
                        ChatColor.of(new Color(230,230,230)) + "with " + ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt " +
                                ChatColor.of(new Color(230,230,230)) + "resets the cooldown",
                        ChatColor.of(new Color(230,230,230)) + "of "  +ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt"
                        );
            }
            case "conjurer":{
                return getItem(Material.AMETHYST_SHARD, 0,
                        ChatColor.of(new Color(153, 0, 255)) + "Conjuring Force",
                        ChatColor.of(new Color(0,102,0)) + "Level "  + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon a force shield centered around",
                        ChatColor.of(new Color(230,230,230)) + "you, enhancing damage and range of all",
                        ChatColor.of(new Color(230,230,230)) + "allies within");
            }
        }

        return new ItemStack(Material.AIR);
    }

    private ItemStack getItem(Material material, int modelData, String name, String ... lore){

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);

        ItemStack item = new ItemStack(material);

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
