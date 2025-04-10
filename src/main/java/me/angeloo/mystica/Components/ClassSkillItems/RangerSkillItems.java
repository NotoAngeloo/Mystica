package me.angeloo.mystica.Components.ClassSkillItems;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.Skill_Level;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ItemManager;
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

public class RangerSkillItems {

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final ItemManager itemManager;

    public RangerSkillItems(Mystica main,AbilityManager manager){
        profileManager = main.getProfileManager();
        abilityManager = manager;
        itemManager = main.getClassEquipmentManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return getItem(1,
                        ChatColor.of(rangerColor) + "Biting Rain",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        ChatColor.of(rangerColor) + String.valueOf(abilityManager.getRangerAbilities().getBitingRain().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "A shower of arrows deals",
                        ChatColor.of(Color.WHITE) + "moderate damage to the target",
                        ChatColor.of(Color.WHITE) + "and enemies near it");
            }
            case 2:{
                return getItem(2,
                        ChatColor.of(rangerColor) + "Shadow Crows",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        ChatColor.of(rangerColor) + String.valueOf(abilityManager.getRangerAbilities().getShadowCrows().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a flock of crows that inflict",
                        ChatColor.of(Color.WHITE) + "continuous damage on the target");
            }
            case 3:{
                return getItem(3,
                        ChatColor.of(rangerColor) + "Relentless",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        ChatColor.of(rangerColor) + String.valueOf(abilityManager.getRangerAbilities().getRelentless().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "A barrage of arrows deal heavy",
                        ChatColor.of(Color.WHITE) + "damage to your target. While",
                        ChatColor.of(Color.WHITE) + "channelling, increase your",
                        ChatColor.of(Color.WHITE) + "movement speed");
            }
            case 4:{
                return getItem(4,
                        ChatColor.of(rangerColor) + "Razor Wind",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        ChatColor.of(rangerColor) + String.valueOf(abilityManager.getRangerAbilities().getRazorWind().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "A charged attack that launches a",
                        ChatColor.of(Color.WHITE) + "spinning blade at the target",
                        ChatColor.of(Color.WHITE) + "dealing heavy damage");
            }
            case 5:{
                return getItem(5,
                        ChatColor.of(rangerColor) + "Blessed Arrow",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        ChatColor.of(rangerColor) + String.valueOf(abilityManager.getRangerAbilities().getBlessedArrow().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "An arrow enchanted with magic",
                        ChatColor.of(Color.WHITE) + "that deals moderate damage.",
                        ChatColor.of(Color.WHITE) + "Can be fired at an ally",
                        ChatColor.of(Color.WHITE) + "or self to restore mana");
            }
            case 6:{
                return getItem(6,
                        ChatColor.of(rangerColor) + "Rallying Cry",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "While active, normal attack",
                        ChatColor.of(Color.WHITE) + "and blessed arrow damage",
                        ChatColor.of(Color.WHITE) + "increases 25%");
            }
            case 7:{
                return getItem(7,
                        ChatColor.of(rangerColor) + "Wild Spirit",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        ChatColor.of(rangerColor) + String.valueOf(abilityManager.getRangerAbilities().getWildSpirit().getSkillDamage(player)) + " power/hit",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a spirit wolf that",
                        ChatColor.of(Color.WHITE) + "attacks your target");
            }
            case 8:{
                return getItem(8,
                        ChatColor.of(rangerColor) + "Roll",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Roll in a desired direction",
                        ChatColor.of(Color.WHITE) + "granting a shield and increased",
                        ChatColor.of(Color.WHITE) + "movement speed");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        int level = playerProfile.getStats().getLevel();
        String subClass = playerProfile.getPlayerSubclass();

        switch(subClass.toLowerCase()){
            case "scout":{
                return getItem(9,
                        ChatColor.of(rangerColor) + "Star Volley",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(rangerColor) + String.valueOf(abilityManager.getRangerAbilities().getStarVolley().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon an arrow from the stars",
                        ChatColor.of(Color.WHITE) + "Landing a crit with any skill",
                        ChatColor.of(Color.WHITE) + "decreases this skills cooldown");
            }
            case "animal tamer":{
                return getItem(10,
                        ChatColor.of(rangerColor) + "Wild Roar",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(rangerColor) + String.valueOf(abilityManager.getRangerAbilities().getWildRoar().getBuffAmount(player)) + " damage multiplier",
                        "",
                        ChatColor.of(Color.WHITE) + "Inspire 5 member of your team",
                        ChatColor.of(Color.WHITE) + "to deal increased damage");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getBasic(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        ItemStack basicItem = itemManager.getRangerEquipment().getBaseWeapon();

        ItemMeta basicMeta = basicItem.getItemMeta();
        List<String> basicLore = new ArrayList<>();

        int level = playerProfile.getStats().getLevel();
        basicLore.add(ChatColor.of(levelColor) + "Level "  + level);
        basicLore.add(ChatColor.of(rangerColor) + String.valueOf(abilityManager.getRangerAbilities().getRangerBasic().getSkillDamage(player)) + " power");

        basicMeta.setLore(basicLore);
        basicItem.setItemMeta(basicMeta);


        return basicItem;
    }

    private ItemStack getItem(int modelData, String name, String ... lore) {

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);

        ItemStack item = new ItemStack(Material.LIME_DYE);

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
