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
                            ChatColor.of(new Color(207, 180, 129)) + "Decree of Honor",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_1_Level() + skillLevel.getSkill_1_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Damage enemies or",
                            ChatColor.of(new Color(230,230,230)) + "heal friendly units");
                }
                case 2:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(new Color(207, 180, 129)) + "Merciful Healing",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_2_Level() + skillLevel.getSkill_2_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "After a cast, heal a",
                            ChatColor.of(new Color(230,230,230)) + "friendly unit for a",
                            ChatColor.of(new Color(230,230,230)) + "large amount of Hp");
                }
                case 3:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(new Color(207, 180, 129)) + "Honorable Counter",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_3_Level() + skillLevel.getSkill_3_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Deal damage to a nearby enemy.",
                            ChatColor.of(new Color(230,230,230)) + "Additionally, deal the amount",
                            ChatColor.of(new Color(230,230,230)) + "of damage equal to the damage",
                            ChatColor.of(new Color(230,230,230)) + "you have taken in the last 3",
                            ChatColor.of(new Color(230,230,230)) + "seconds");
                }
                case 4:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(new Color(207, 180, 129)) + "Divine Infusion",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_4_Level() + skillLevel.getSkill_4_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Drop an infusion to the",
                            ChatColor.of(new Color(230,230,230)) + "target area, dealing continuous",
                            ChatColor.of(new Color(230,230,230)) + "damage to enemies and shielding",
                            ChatColor.of(new Color(230,230,230)) + "allies. Players in its range gain",
                            ChatColor.of(new Color(230,230,230)) + "speed up effect");
                }
                case 5:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(new Color(207, 180, 129)) + "Spiritual Gift",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_5_Level() + skillLevel.getSkill_5_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Grant damage reduction, haste and",
                            ChatColor.of(new Color(230,230,230)) + "increase the damage they deal for",
                            ChatColor.of(new Color(230,230,230)) + "8 seconds. Heal the target when",
                            ChatColor.of(new Color(230,230,230)) + "the effect runs out");
                }
                case 6:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(new Color(207, 180, 129)) + "Sacred Aegis",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_6_Level() + skillLevel.getSkill_6_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Grant damage immunity to a",
                            ChatColor.of(new Color(230,230,230)) + "friendly target");
                }
                case 7:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(new Color(207, 180, 129)) + "Modest Calling",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_7_Level() + skillLevel.getSkill_7_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Reduce the damage cause by the",
                            ChatColor.of(new Color(230,230,230)) + "enemy target as well as increase",
                            ChatColor.of(new Color(230,230,230)) + "the damage they take");
                }
                case 8:{
                    return getItem(Material.YELLOW_DYE, 0,
                            ChatColor.of(new Color(207, 180, 129)) + "Mark of Justice",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_8_Level() + skillLevel.getSkill_8_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Set a marker on a friendly unit",
                            ChatColor.of(new Color(230,230,230)) + "as well as 4 nearby allies within",
                            ChatColor.of(new Color(230,230,230)) + "range. Healing a marked ally",
                            ChatColor.of(new Color(230,230,230)) + "also heals other marked allies");
                }
            }
        }

        switch(number){

            case 1:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 214, 61)) + "Torah Sword",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_1_Level() + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon several swords",
                        ChatColor.of(new Color(230,230,230)) + "to fall from the sky to",
                        ChatColor.of(new Color(230,230,230)) + "attack your target");
            }
            case 2:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 214, 61)) + "Divine Guidance",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_2_Level() + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Deal area damage to",
                        ChatColor.of(new Color(230,230,230)) + "nearby enemies and heal",
                        ChatColor.of(new Color(230,230,230)) + "three nearby allies with",
                        ChatColor.of(new Color(230,230,230)) + "the lowest Hp percentage");
            }
            case 3:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 214, 61)) + "Reigning Sword",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_3_Level() + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Attack nearby enemies",
                        ChatColor.of(new Color(230,230,230)) + "and grant yourself a shield");
            }
            case 4:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 214, 61)) + "Sword of the Covenant",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_4_Level() + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Concentrate the faith of",
                        ChatColor.of(new Color(230,230,230)) + "Paladins in one sword, dealing",
                        ChatColor.of(new Color(230,230,230)) + "damage to the target with the",
                        ChatColor.of(new Color(230,230,230)) + "swing of a giant sword. Ignites",
                        ChatColor.of(new Color(230,230,230)) + "the ground beneath the target with",
                        ChatColor.of(new Color(230,230,230)) + "holy light, dealing continuous damage",
                        ChatColor.of(new Color(230,230,230)) + "to enemies standing on it");
            }
            case 5:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 214, 61)) + "Shield of Order",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_5_Level() + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Consume 10% of your max Hp to",
                        ChatColor.of(new Color(230,230,230)) + "throw your shield to deal damage",
                        ChatColor.of(new Color(230,230,230)) + "to the target. Gain an effect to",
                        ChatColor.of(new Color(230,230,230)) + "restore your Hp continuously");
            }
            case 6:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 214, 61)) + "Glory of Paladins",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_6_Level() + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Gain Glory of Paladins for yourself",
                        ChatColor.of(new Color(230,230,230)) + "throw your shield to deal damage",
                        ChatColor.of(new Color(230,230,230)) + "causing your basic attacks to deal",
                        ChatColor.of(new Color(230,230,230)) + "additional damage. Increase your max",
                        ChatColor.of(new Color(230,230,230)) + "Hp, and give your basic attacks a",
                        ChatColor.of(new Color(230,230,230)) + "chance to restore Hp");
            }
            case 7:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 214, 61)) + "Durance of Truth",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_7_Level() + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Leap toward your target and",
                        ChatColor.of(new Color(230,230,230)) + "create a Durance of Truth around",
                        ChatColor.of(new Color(230,230,230)) + "them. While in the durance, gain",
                        ChatColor.of(new Color(230,230,230)) + "damage reduction and Hp regen.",
                        ChatColor.of(new Color(230,230,230)) + "Enemies who leave the Durance",
                        ChatColor.of(new Color(230,230,230)) + "become silenced");
            }
            case 8:{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 214, 61)) + "Judgement",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_8_Level() + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon a light pillar from",
                        ChatColor.of(new Color(230,230,230)) + "the sky, to either heal an",
                        ChatColor.of(new Color(230,230,230)) + "ally or damage an enemy");
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
                        ChatColor.of(new Color(207, 180, 80)) + "Shield of Sanctity",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Gain a shield to absorb",
                        ChatColor.of(new Color(230,230,230)) + (String.valueOf(level)) + "% of your max health for",
                        ChatColor.of(new Color(230,230,230)) + "5 seconds");
            }
            case "divine":{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 180, 129)) + "Representative",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Instantly heal nearby allies",
                        ChatColor.of(new Color(230,230,230)) + "and grant yourself haste as",
                        ChatColor.of(new Color(230,230,230)) + "well as your healing and damage",
                        ChatColor.of(new Color(230,230,230)) + "for 10 seconds. This skill is",
                        ChatColor.of(new Color(230,230,230)) + "unaffected by haste");
            }
            case "dawn":{
                return getItem(Material.YELLOW_DYE, 0,
                        ChatColor.of(new Color(207, 214, 61)) + "Well of Light",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon a well that deals",
                        ChatColor.of(new Color(230,230,230)) + "area damage. This also",
                        ChatColor.of(new Color(230,230,230)) + "leaves behind orbs that",
                        ChatColor.of(new Color(230,230,230)) + "can be collected by you to",
                        ChatColor.of(new Color(230,230,230)) + "increase your crit rate by 10%",
                        ChatColor.of(new Color(230,230,230)) + "until you land a critical hit");
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
