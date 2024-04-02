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

import static me.angeloo.mystica.Mystica.levelColor;
import static me.angeloo.mystica.Mystica.paladinColor;

public class PaladinSkillItems {

    private final ProfileManager profileManager;

    public PaladinSkillItems(Mystica main){
        profileManager = main.getProfileManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();

        String subclass = playerProfile.getPlayerSubclass();

        if(subclass.equalsIgnoreCase("divine")){
            switch(number){

                case 1:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(paladinColor) + "Decree of Honor",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Damage enemies or",
                            ChatColor.of(Color.WHITE) + "heal friendly units.",
                            ChatColor.of(Color.WHITE) + "Landing a crit makes",
                            ChatColor.of(Color.WHITE) + "your next " + ChatColor.of(paladinColor) + "Merciful Healing",
                            ChatColor.of(Color.WHITE) + "able to be cast while",
                            ChatColor.of(Color.WHITE) + "moving");
                }
                case 2:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(paladinColor) + "Merciful Healing",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "After a cast, heal a",
                            ChatColor.of(Color.WHITE) + "friendly unit for a",
                            ChatColor.of(Color.WHITE) + "large amount of Hp");
                }
                case 3:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(paladinColor) + "Honorable Counter",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Deal damage to a nearby enemy.",
                            ChatColor.of(Color.WHITE) + "Additionally, deal the amount",
                            ChatColor.of(Color.WHITE) + "of damage equal to the damage",
                            ChatColor.of(Color.WHITE) + "you have taken in the last 3",
                            ChatColor.of(Color.WHITE) + "seconds");
                }
                case 4:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(paladinColor) + "Divine Infusion",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Drop an infusion to the",
                            ChatColor.of(Color.WHITE) + "target area, dealing continuous",
                            ChatColor.of(Color.WHITE) + "damage to enemies and shielding",
                            ChatColor.of(Color.WHITE) + "allies. Players in its range gain",
                            ChatColor.of(Color.WHITE) + "speed up effect");
                }
                case 5:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(paladinColor) + "Spiritual Gift",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Grant damage reduction, haste and",
                            ChatColor.of(Color.WHITE) + "increase the damage they deal for",
                            ChatColor.of(Color.WHITE) + "8 seconds. Heal the target when",
                            ChatColor.of(Color.WHITE) + "the effect runs out");
                }
                case 6:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(paladinColor) + "Sacred Aegis",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Grant damage immunity to a",
                            ChatColor.of(Color.WHITE) + "friendly target");
                }
                case 7:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(paladinColor) + "Modest Calling",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Reduce the damage cause by the",
                            ChatColor.of(Color.WHITE) + "enemy target as well as increase",
                            ChatColor.of(Color.WHITE) + "the damage they take.",
                            ChatColor.of(Color.WHITE) + "In addition, put the target",
                            ChatColor.of(Color.WHITE) + "to sleep.");
                }
                case 8:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(paladinColor) + "Mark of Justice",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Set a marker on a friendly unit",
                            ChatColor.of(Color.WHITE) + "as well as 4 nearby allies within",
                            ChatColor.of(Color.WHITE) + "range. Healing a marked ally",
                            ChatColor.of(Color.WHITE) + "also heals other marked allies");
                }
            }
        }

        switch(number){

            case 1:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Torah Sword",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Summon several swords",
                        ChatColor.of(Color.WHITE) + "to fall from the sky to",
                        ChatColor.of(Color.WHITE) + "attack your target");
            }
            case 2:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Divine Guidance",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Deal area damage to",
                        ChatColor.of(Color.WHITE) + "nearby enemies and heal",
                        ChatColor.of(Color.WHITE) + "three nearby allies with",
                        ChatColor.of(Color.WHITE) + "the lowest Hp percentage");
            }
            case 3:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Reigning Sword",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Attack nearby enemies",
                        ChatColor.of(Color.WHITE) + "and grant yourself a shield");
            }
            case 4:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Sword of the Covenant",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Concentrate the faith of",
                        ChatColor.of(Color.WHITE) + "Paladins in one sword, dealing",
                        ChatColor.of(Color.WHITE) + "damage to the target with the",
                        ChatColor.of(Color.WHITE) + "swing of a giant sword. Ignites",
                        ChatColor.of(Color.WHITE) + "the ground beneath the target with",
                        ChatColor.of(Color.WHITE) + "holy light, dealing continuous damage",
                        ChatColor.of(Color.WHITE) + "to enemies standing on it");
            }
            case 5:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Shield of Order",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Consume 10% of your max Hp to",
                        ChatColor.of(Color.WHITE) + "throw your shield to deal damage",
                        ChatColor.of(Color.WHITE) + "to the target. Gain an effect to",
                        ChatColor.of(Color.WHITE) + "restore your Hp continuously");
            }
            case 6:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Glory of Paladins",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Gain Glory of Paladins for yourself",
                        ChatColor.of(Color.WHITE) + "causing your basic attacks to deal",
                        ChatColor.of(Color.WHITE) + "additional damage. Increase your max",
                        ChatColor.of(Color.WHITE) + "Hp, and give your basic attacks a",
                        ChatColor.of(Color.WHITE) + "chance to restore Hp");
            }
            case 7:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Durance of Truth",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Leap toward your target and",
                        ChatColor.of(Color.WHITE) + "create a Durance of Truth around",
                        ChatColor.of(Color.WHITE) + "them. While in the durance, gain",
                        ChatColor.of(Color.WHITE) + "damage reduction and Hp regen.",
                        ChatColor.of(Color.WHITE) + "Enemies who leave the Durance",
                        ChatColor.of(Color.WHITE) + "become silenced");
            }
            case 8:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Judgement",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a light pillar from",
                        ChatColor.of(Color.WHITE) + "the sky, to either heal an",
                        ChatColor.of(Color.WHITE) + "ally or damage an enemy");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        int level = playerProfile.getStats().getLevel();
        String subClass = playerProfile.getPlayerSubclass();

        switch(subClass.toLowerCase()){
            case "templar":{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Shield of Sanctity",
                        ChatColor.of(levelColor) + "Level " + level,
                        "",
                        ChatColor.of(Color.WHITE) + "Gain a shield to absorb",
                        ChatColor.of(Color.WHITE) + (String.valueOf(level)) + "% of your max health for",
                        ChatColor.of(Color.WHITE) + "5 seconds. While active,",
                        ChatColor.of(Color.WHITE) + "restore your health.");
            }
            case "divine":{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Representative",
                        ChatColor.of(levelColor) + "Level " + level,
                        "",
                        ChatColor.of(Color.WHITE) + "Instantly heal nearby allies",
                        ChatColor.of(Color.WHITE) + "and grant yourself haste as",
                        ChatColor.of(Color.WHITE) + "well as your healing and damage",
                        ChatColor.of(Color.WHITE) + "for 10 seconds. This skill is",
                        ChatColor.of(Color.WHITE) + "unaffected by haste");
            }
            case "dawn":{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(paladinColor) + "Well of Light",
                        ChatColor.of(levelColor) + "Level " + level,
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a well that deals",
                        ChatColor.of(Color.WHITE) + "area damage. This also",
                        ChatColor.of(Color.WHITE) + "leaves behind orbs that",
                        ChatColor.of(Color.WHITE) + "can be collected to increase",
                        ChatColor.of(Color.WHITE) + "your crit rate by 10% until",
                        ChatColor.of(Color.WHITE) + "you land a critical hit");
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
