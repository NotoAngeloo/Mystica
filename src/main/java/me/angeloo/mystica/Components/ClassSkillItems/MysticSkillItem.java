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
                            ChatColor.of(new Color(155, 120, 197)) + "Chaos Void",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_1_Level() + skillLevel.getSkill_1_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Hide underground to recover your",
                            ChatColor.of(new Color(230,230,230)) + "health. You are unable to take",
                            ChatColor.of(new Color(230,230,230)) + "damage, but are also unable to move");
                }

                case 2:{
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(new Color(155, 120, 197)) + "Plague Curse",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_2_Level() + skillLevel.getSkill_2_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Deal damage to your target and",
                            ChatColor.of(new Color(230,230,230)) + "all nearby enemies. All hit enemies",
                            ChatColor.of(new Color(230,230,230)) + "will take damage and become cursed.",
                            ChatColor.of(new Color(230,230,230)) + "Using this skill gains a chaos shard");
                }

                case 3:{
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(new Color(155, 120, 197)) + "Shadow of Darkness",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_3_Level() + skillLevel.getSkill_3_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Cause a meteor of chaos energy",
                            ChatColor.of(new Color(230,230,230)) + "to fall from the sky and hit",
                            ChatColor.of(new Color(230,230,230)) + "your target. More damage is dealt",
                            ChatColor.of(new Color(230,230,230)) + "depending on how may chaos shards",
                            ChatColor.of(new Color(230,230,230)) + "you possess");
                }

                case 4:{
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(new Color(155, 120, 197)) + "Flickering Chaos",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_4_Level() + skillLevel.getSkill_4_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Teleport, either toward your target",
                            ChatColor.of(new Color(230,230,230)) + "or in front you");
                }

                case 5:{
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(new Color(155, 120, 197)) + "Spiritual Descent",
                            ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_5_Level() + skillLevel.getSkill_5_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230,230,230)) + "Erupt the ground under your target",
                            ChatColor.of(new Color(230,230,230)) + "with chaos energy.",
                            ChatColor.of(new Color(230,230,230)) + "Using this skill gains a chaos shard");
                }

                case 6: {
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(new Color(155, 120, 197)) + "Chaos Lash",
                            ChatColor.of(new Color(0, 102, 0)) + "Level " + (skillLevel.getSkill_6_Level() + skillLevel.getSkill_6_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230, 230, 230)) + "Release chaotic flames toward your",
                            ChatColor.of(new Color(230, 230, 230)) + "target during a short period of time",
                            ChatColor.of(new Color(230, 230, 230)) + "dealing massive damage. If the target",
                            ChatColor.of(new Color(230, 230, 230)) + "is cursed, you gain two chaos shards");
                }

                case 7: {
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(new Color(155, 120, 197)) + "Cursing Voice",
                            ChatColor.of(new Color(0, 102, 0)) + "Level " + (skillLevel.getSkill_7_Level() + skillLevel.getSkill_7_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230, 230, 230)) + "Cause your target to fall asleep.",
                            ChatColor.of(new Color(230, 230, 230)) + "If they take damage, they awaken");
                }

                case 8: {
                    return getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(new Color(155, 120, 197)) + "Health Absorb",
                            ChatColor.of(new Color(0, 102, 0)) + "Level " + (skillLevel.getSkill_8_Level() + skillLevel.getSkill_8_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230, 230, 230)) + "Leech life from your target, restoring",
                            ChatColor.of(new Color(230, 230, 230)) + "your own health");
                }

            }

        }

        switch (number){

            case 1:{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(155, 120, 197)) + "Arcane Shield",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_1_Level() + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Raise a shield of arcane energy",
                        ChatColor.of(new Color(230,230,230)) + "around your target that absorbs",
                        ChatColor.of(new Color(230,230,230)) + "damage");
            }

            case 2:{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(155, 120, 197)) + "Purifying Blast",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_2_Level() + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Surround yourself with a ring",
                        ChatColor.of(new Color(230,230,230)) + "of arcane energy, healing allies",
                        ChatColor.of(new Color(230,230,230)) + "and damaging enemies");
            }

            case 3:{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(155, 120, 197)) + "Force of Will",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_3_Level() + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Channel arcane energy to deal",
                        ChatColor.of(new Color(230,230,230)) + "continuous damage to your target");
            }

            case 4:{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(155, 120, 197)) + "Dreadfall",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_4_Level() + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon a void meteor that deals",
                        ChatColor.of(new Color(230,230,230)) + "heavy area damage");
            }

            case 5:{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(155, 120, 197)) + "Void Walk",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_5_Level() + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Teleport, either toward your target",
                        ChatColor.of(new Color(230,230,230)) + "or in front you");
            }

            case 6:{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(155, 120, 197)) + "Aurora",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_6_Level() + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Summon an arcane energy barrier to",
                        ChatColor.of(new Color(230,230,230)) + "surround the target ally. All allies",
                        ChatColor.of(new Color(230,230,230)) + "within are protected by a shield");
            }

            case 7:{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(155, 120, 197)) + "Arcane Contract",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_7_Level() + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Revive one dead player. Using this",
                        ChatColor.of(new Color(230,230,230)) + "skill also puts it in cooldown for",
                        ChatColor.of(new Color(230,230,230)) + "allied mystics");
            }

            case 8:{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(155, 120, 197)) + "Light Sigil",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_8_Level() + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Generate a Light Sigil",
                        ChatColor.of(new Color(230,230,230)) + "that continuously damages",
                        ChatColor.of(new Color(230,230,230)) + "nearby enemies. Your",
                        ChatColor.of(new Color(230,230,230)) + "next " + ChatColor.of(new Color(155, 120, 197)) + "Purifying Blast " +
                        ChatColor.of(new Color(230,230,230)) + "has no",
                        ChatColor.of(new Color(230,230,230)) + "cast time");
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
                        ChatColor.of(new Color(59, 14, 114)) + "Evil Spirit",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Transform into the embodiment",
                        ChatColor.of(new Color(230,230,230)) + "of chaos, with the one goal",
                        ChatColor.of(new Color(230,230,230)) + "of destroying your enemies.",
                        ChatColor.of(new Color(230,230,230)) + "Your skills build up chaos ",
                        ChatColor.of(new Color(230,230,230)) + "shards. When you have six",
                        ChatColor.of(new Color(230,230,230)) + "this skill is able to be cast");
            }

            case "arcane master":{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(155, 120, 197)) + "Arcane Missiles",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Rapidly fire multiple projectiles",
                        ChatColor.of(new Color(230,230,230)) + "of pure arcane energy that bombard",
                        ChatColor.of(new Color(230,230,230)) + "your enemy");
            }

            case "shepard":{
                return getItem(Material.PURPLE_DYE, 0,
                        ChatColor.of(new Color(126, 101, 238)) + "Enlightenment",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Instantly heal nearby allies",
                        ChatColor.of(new Color(230,230,230)) + "and give them a 10% damage",
                        ChatColor.of(new Color(230,230,230)) + "reduction buff for 5 seconds");
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
