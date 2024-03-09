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

public class ShadowKnightSkillItems {

    private final ProfileManager profileManager;

    public ShadowKnightSkillItems(Mystica main){
        profileManager = main.getProfileManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return getItem(Material.RED_DYE, 1,
                        ChatColor.of(new Color(213, 33, 3)) + "Infection",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_1_Level() + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Launch a projectile that infects your",
                        ChatColor.of(new Color(230,230,230)) + "target for continuous damage");
            }
            case 2:{
                return getItem(Material.RED_DYE, 2,
                        ChatColor.of(new Color(213, 33, 3)) + "Spiritual Attack",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_2_Level() + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon the hand of your spiritual",
                        ChatColor.of(new Color(230,230,230)) + "ally to strike your target from above");
            }
            case 3:{
                return getItem(Material.RED_DYE, 3,
                        ChatColor.of(new Color(213, 33, 3)) + "Burial Ground",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_3_Level() + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon a graveyard beneath your",
                        ChatColor.of(new Color(230,230,230)) + "feet. Standing within restores",
                        ChatColor.of(new Color(230,230,230)) + "your health and your energy",
                        ChatColor.of(new Color(230,230,230)) + "over time.");
            }
            case 4:{
                return getItem(Material.RED_DYE, 4,
                        ChatColor.of(new Color(213, 33, 3)) + "Bloodsucker",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_4_Level() + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Leech life from your target",
                        ChatColor.of(new Color(230,230,230)) + "to restore your own health.");
            }
            case 5:{
                return getItem(Material.RED_DYE, 5,
                        ChatColor.of(new Color(213, 33, 3)) + "Soul Reap",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_5_Level() + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Strike your target, piercing",
                        ChatColor.of(new Color(230,230,230)) + "their very soul. If they",
                        ChatColor.of(new Color(230,230,230)) + "are beneath 30% of their",
                        ChatColor.of(new Color(230,230,230)) + "maximum health, damage is",
                        ChatColor.of(new Color(230,230,230)) + "increased by 30%");
            }
            case 6:{
                return getItem(Material.RED_DYE, 6,
                        ChatColor.of(new Color(213, 33, 3)) + "Shadow Grip",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_6_Level() + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "The hand of your spiritual",
                        ChatColor.of(new Color(230,230,230)) + "ally pulls your target toward",
                        ChatColor.of(new Color(230,230,230)) + "you. This ability causes",
                        ChatColor.of(new Color(230,230,230)) + "the hit enemy to target",
                        ChatColor.of(new Color(230,230,230)) + "you");
            }
            case 7:{
                return getItem(Material.RED_DYE, 7,
                        ChatColor.of(new Color(213, 33, 3)) + "Spectral Steed",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_7_Level() + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon your spectral steed",
                        ChatColor.of(new Color(230,230,230)) + "to increase your combative",
                        ChatColor.of(new Color(230,230,230)) + "advantage. Your movement",
                        ChatColor.of(new Color(230,230,230)) + "speed is increased and you",
                        ChatColor.of(new Color(230,230,230)) + "may use abilities while mounted");
            }
            case 8:{
                return getItem(Material.RED_DYE, 8,
                        ChatColor.of(new Color(213, 33, 3)) + "Soulcrack",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_8_Level() + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Strike all foes around you.",
                        ChatColor.of(new Color(230,230,230)) + "This ability restores your",
                        ChatColor.of(new Color(230,230,230)) + "energy instead of consuming",
                        ChatColor.of(new Color(230,230,230)) + "it.");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        int level = playerProfile.getStats().getLevel();
        String subClass = playerProfile.getPlayerSubclass();

        switch(subClass.toLowerCase()){
            case "blood":{
                return getItem(Material.RED_DYE, 9,
                        ChatColor.of(new Color(213, 33, 3)) + "Blood Shield",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Instantly heal 50% of your",
                        ChatColor.of(new Color(230,230,230)) + "missing health. Shield yourself",
                        ChatColor.of(new Color(230,230,230)) + "for an equivalent of your current",
                        ChatColor.of(new Color(230,230,230)) + "health for 10 seconds. While active,",
                        ChatColor.of(new Color(230,230,230)) + "casting " + ChatColor.of(new Color(213, 33, 3)) + "Bloodsucker " +
                                ChatColor.of(new Color(230,230,230)) + "increases the",
                        ChatColor.of(new Color(230,230,230)) + "duration by 3 seconds");
            }
            case "doom":{
                return getItem(Material.RED_DYE, 10,
                        ChatColor.of(new Color(3, 7, 219)) + "Annihilation",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Your spiritual ally strikes",
                        ChatColor.of(new Color(230,230,230)) + "your foe with a powerful sweep,",
                        ChatColor.of(new Color(230,230,230)) + "enhancing any " +
                                ChatColor.of(new Color(213, 33, 3)) + "Infections " + ChatColor.of(new Color(230,230,230)) + "and",
                        ChatColor.of(new Color(230,230,230)) + "refreshing its duration");
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
