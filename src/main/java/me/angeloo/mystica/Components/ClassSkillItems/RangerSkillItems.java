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

public class RangerSkillItems {

    private final ProfileManager profileManager;

    public RangerSkillItems(Mystica main){
        profileManager = main.getProfileManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return getItem(Material.LIME_DYE, 0,
                        ChatColor.of(new Color(34, 111, 80)) + "Biting Rain",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_1_Level() + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "A shower of arrows deals",
                        ChatColor.of(new Color(230,230,230)) + "moderate damage to the target",
                        ChatColor.of(new Color(230,230,230)) + "and enemies near it");
            }
            case 2:{
                return getItem(Material.LIME_DYE, 0,
                        ChatColor.of(new Color(34, 111, 80)) + "Shadow Crows",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_2_Level() + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon a flock of crows that inflict",
                        ChatColor.of(new Color(230,230,230)) + "continuous damage on the target");
            }
            case 3:{
                return getItem(Material.LIME_DYE, 0,
                        ChatColor.of(new Color(34, 111, 80)) + "Relentless",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_3_Level() + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "A barrage of arrows deal heavy",
                        ChatColor.of(new Color(230,230,230)) + "damage to your target. While",
                        ChatColor.of(new Color(230,230,230)) + "channelling, increase your",
                        ChatColor.of(new Color(230,230,230)) + "movement speed");
            }
            case 4:{
                return getItem(Material.LIME_DYE, 0,
                        ChatColor.of(new Color(34, 111, 80)) + "Razor Wind",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_4_Level() + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "A charged attack that launches a",
                        ChatColor.of(new Color(230,230,230)) + "spinning blade at the target",
                        ChatColor.of(new Color(230,230,230)) + "dealing heavy damage");
            }
            case 5:{
                return getItem(Material.LIME_DYE, 0,
                        ChatColor.of(new Color(34, 111, 80)) + "Blessed Arrow",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_5_Level() + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "An arrow enchanted with magic",
                        ChatColor.of(new Color(230,230,230)) + "that deals moderate damage.",
                        ChatColor.of(new Color(230,230,230)) + "Can be fired at an ally",
                        ChatColor.of(new Color(230,230,230)) + "or self to restore mana");
            }
            case 6:{
                return getItem(Material.LIME_DYE, 0,
                        ChatColor.of(new Color(34, 111, 80)) + "Rallying Cry",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_6_Level() + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "While active, normal attack",
                        ChatColor.of(new Color(230,230,230)) + "and blessed arrow damage",
                        ChatColor.of(new Color(230,230,230)) + "increases 25%");
            }
            case 7:{
                return getItem(Material.LIME_DYE, 0,
                        ChatColor.of(new Color(34, 111, 80)) + "Wild Spirit",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_7_Level() + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon a spirit wolf that",
                        ChatColor.of(new Color(230,230,230)) + "attacks your target");
            }
            case 8:{
                return getItem(Material.LIME_DYE, 0,
                        ChatColor.of(new Color(34, 111, 80)) + "Roll",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_8_Level() + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Roll in a desired direction",
                        ChatColor.of(new Color(230,230,230)) + "granting a shield and increased",
                        ChatColor.of(new Color(230,230,230)) + "movement speed");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        int level = playerProfile.getStats().getLevel();
        String subClass = playerProfile.getPlayerSubclass();

        switch(subClass.toLowerCase()){
            //maybe change this skill
            case "scout":{
                return getItem(Material.ARROW, 0,
                        ChatColor.of(new Color(34, 111, 80)) + "Star Volley",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon an arrow from the stars",
                        ChatColor.of(new Color(230,230,230)) + "Landing a crit with any skill",
                        ChatColor.of(new Color(230,230,230)) + "decreases this skills cooldown");
            }
            case "animal tamer":{
                return getItem(Material.BONE, 0,
                        ChatColor.of(new Color(0, 117, 94)) + "Wild Roar",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Inspire 5 member of your team",
                        ChatColor.of(new Color(230,230,230)) + "to deal increased damage");
            }
        }

        return new ItemStack(Material.AIR);
    }

    private ItemStack getItem(Material material, int modelData, String name, String ... lore) {

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
