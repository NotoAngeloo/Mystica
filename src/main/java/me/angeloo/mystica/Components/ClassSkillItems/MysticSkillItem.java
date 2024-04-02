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

import static me.angeloo.mystica.Mystica.*;
import static me.angeloo.mystica.Mystica.levelColor;

public class MysticSkillItem {

    private final ProfileManager profileManager;

    public MysticSkillItem(Mystica main){
        profileManager = main.getProfileManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();

        String subclass = playerProfile.getPlayerSubclass();

        if(subclass.equalsIgnoreCase("chaos")){

            switch (number){

                case 1:{
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(mysticColor) + "Chaos Void",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Hide underground to recover your",
                            ChatColor.of(Color.WHITE) + "health. You are unable to take",
                            ChatColor.of(Color.WHITE) + "damage, but are also unable to move");
                }

                case 2:{
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(mysticColor) + "Plague Curse",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Deal damage to your target and",
                            ChatColor.of(Color.WHITE) + "all nearby enemies. All hit enemies",
                            ChatColor.of(Color.WHITE) + "will take damage and become cursed.",
                            ChatColor.of(Color.WHITE) + "Using this skill gains a chaos shard");
                }

                case 3:{
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(mysticColor) + "Shadow of Darkness",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Cause a meteor of chaos energy",
                            ChatColor.of(Color.WHITE) + "to fall from the sky and hit",
                            ChatColor.of(Color.WHITE) + "your target. More damage is dealt",
                            ChatColor.of(Color.WHITE) + "depending on how may chaos shards",
                            ChatColor.of(Color.WHITE) + "you possess");
                }

                case 4:{
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(mysticColor) + "Flickering Chaos",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Teleport, either toward your target",
                            ChatColor.of(Color.WHITE) + "or in front you");
                }

                case 5:{
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(mysticColor) + "Spiritual Descent",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Erupt the ground under your target",
                            ChatColor.of(Color.WHITE) + "with chaos energy.",
                            ChatColor.of(Color.WHITE) + "Using this skill gains a chaos shard");
                }

                case 6: {
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(mysticColor) + "Chaos Lash",
                            ChatColor.of(new Color(0, 102, 0)) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230, 230, 230)) + "Release chaotic flames toward your",
                            ChatColor.of(new Color(230, 230, 230)) + "target during a short period of time",
                            ChatColor.of(new Color(230, 230, 230)) + "dealing massive damage. If the target",
                            ChatColor.of(new Color(230, 230, 230)) + "is cursed, you gain two chaos shards");
                }

                case 7: {
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(mysticColor) + "Cursing Voice",
                            ChatColor.of(new Color(0, 102, 0)) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230, 230, 230)) + "Cause your target to fall asleep.",
                            ChatColor.of(new Color(230, 230, 230)) + "If they take damage, they awaken");
                }

                case 8: {
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(mysticColor) + "Health Absorb",
                            ChatColor.of(new Color(0, 102, 0)) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230, 230, 230)) + "Leech life from your target, restoring",
                            ChatColor.of(new Color(230, 230, 230)) + "your own health");
                }

            }

        }

        switch (number){

            case 1:{
                return getItem(Material.PURPLE_DYE, 1,
                        ChatColor.of(mysticColor) + "Arcane Shield",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Raise a shield of arcane energy",
                        ChatColor.of(Color.WHITE) + "around your target that absorbs",
                        ChatColor.of(Color.WHITE) + "damage");
            }

            case 2:{
                return getItem(Material.PURPLE_DYE, 2,
                        ChatColor.of(mysticColor) + "Purifying Blast",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Surround yourself with a ring",
                        ChatColor.of(Color.WHITE) + "of arcane energy, healing allies",
                        ChatColor.of(Color.WHITE) + "and damaging enemies");
            }

            case 3:{
                return getItem(Material.PURPLE_DYE, 3,
                        ChatColor.of(mysticColor) + "Force of Will",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Channel arcane energy to deal",
                        ChatColor.of(Color.WHITE) + "continuous damage to your target");
            }

            case 4:{
                return getItem(Material.PURPLE_DYE, 4,
                        ChatColor.of(mysticColor) + "Dreadfall",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a void meteor that deals",
                        ChatColor.of(Color.WHITE) + "heavy area damage");
            }

            case 5:{
                return getItem(Material.PURPLE_DYE, 5,
                        ChatColor.of(mysticColor) + "Void Walk",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Teleport, either toward your target",
                        ChatColor.of(Color.WHITE) + "or in front you");
            }

            case 6:{
                return getItem(Material.PURPLE_DYE, 6,
                        ChatColor.of(mysticColor) + "Aurora",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Summon an arcane energy barrier to",
                        ChatColor.of(Color.WHITE) + "surround the target ally. All allies",
                        ChatColor.of(Color.WHITE) + "within are protected by a shield");
            }

            case 7:{
                return getItem(Material.PURPLE_DYE, 7,
                        ChatColor.of(mysticColor) + "Arcane Contract",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Revive one dead player. Using this",
                        ChatColor.of(Color.WHITE) + "skill also puts it in cooldown for",
                        ChatColor.of(Color.WHITE) + "allied mystics");
            }

            case 8:{
                return getItem(Material.PURPLE_DYE, 8,
                        ChatColor.of(mysticColor) + "Light Sigil",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Generate a Light Sigil",
                        ChatColor.of(Color.WHITE) + "that continuously damages",
                        ChatColor.of(Color.WHITE) + "nearby enemies. Your",
                        ChatColor.of(Color.WHITE) + "next " + ChatColor.of(mysticColor) + "Purifying Blast " +
                        ChatColor.of(Color.WHITE) + "has no",
                        ChatColor.of(Color.WHITE) + "cast time");
            }

        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        int level = playerProfile.getStats().getLevel();
        String subClass = playerProfile.getPlayerSubclass();

        switch (subClass.toLowerCase()){
            case "chaos":{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(mysticColor) + "Evil Spirit",
                        ChatColor.of(levelColor) + "Level " + level,
                        "",
                        ChatColor.of(Color.WHITE) + "Transform into the embodiment",
                        ChatColor.of(Color.WHITE) + "of chaos, with the one goal",
                        ChatColor.of(Color.WHITE) + "of destroying your enemies.",
                        ChatColor.of(Color.WHITE) + "Your skills build up chaos ",
                        ChatColor.of(Color.WHITE) + "shards. When you have six",
                        ChatColor.of(Color.WHITE) + "this skill is able to be cast");
            }

            case "arcane master":{
                return getItem(Material.PURPLE_DYE, 9,
                        ChatColor.of(mysticColor) + "Arcane Missiles",
                        ChatColor.of(levelColor) + "Level " + level,
                        "",
                        ChatColor.of(Color.WHITE) + "Rapidly fire multiple projectiles",
                        ChatColor.of(Color.WHITE) + "of pure arcane energy that bombard",
                        ChatColor.of(Color.WHITE) + "your enemy");
            }

            case "shepard":{
                return getItem(Material.PURPLE_DYE, 10,
                        ChatColor.of(mysticColor) + "Enlightenment",
                        ChatColor.of(levelColor) + "Level " + level,
                        "",
                        ChatColor.of(Color.WHITE) + "Instantly heal nearby allies",
                        ChatColor.of(Color.WHITE) + "and give them a 10% damage",
                        ChatColor.of(Color.WHITE) + "reduction buff for 5 seconds");
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
