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
import static me.angeloo.mystica.Mystica.levelColor;

public class MysticSkillItem {

    private final MysticaEquipment weapon;
    private final ProfileManager profileManager;
    private final InventoryItemGetter itemGetter;
    private final AbilityManager abilityManager;

    public MysticSkillItem(Mystica main, AbilityManager manager){
        weapon = new MysticaEquipment(EquipmentSlot.WEAPON, PlayerClass.Mystic, 1);
        profileManager = main.getProfileManager();
        abilityManager = manager;
        itemGetter = main.getItemGetter();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();

        SubClass subclass = playerProfile.getPlayerSubclass();

        if(subclass.equals(SubClass.Chaos)){

            switch (number){

                case 1:{
                    return itemGetter.getItem(Material.PURPLE_DYE, 0,
                            ChatColor.of(mysticColor) + "Chaos Void",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Hide underground to recover your",
                            ChatColor.of(Color.WHITE) + "health. You are unable to take",
                            ChatColor.of(Color.WHITE) + "damage, but are also unable to move");
                }

                case 2:{
                    return itemGetter.getItem(Material.PURPLE_DYE,0,
                            ChatColor.of(mysticColor) + "Plague Curse",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                            ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getPlagueCurse().getSkillDamage(player)) + " power",
                            "",
                            ChatColor.of(Color.WHITE) + "Deal damage to your target and",
                            ChatColor.of(Color.WHITE) + "all nearby enemies. All hit enemies",
                            ChatColor.of(Color.WHITE) + "will take damage and become cursed.",
                            ChatColor.of(Color.WHITE) + "Using this skill gains a chaos shard");
                }

                case 3:{
                    return itemGetter.getItem(Material.PURPLE_DYE,0,
                            ChatColor.of(mysticColor) + "Shadow of Darkness",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                            ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getShadowOfDarkness().getSkillDamage(player)) + " damage + (2 x shards)",
                            "",
                            ChatColor.of(Color.WHITE) + "Cause a meteor of chaos energy",
                            ChatColor.of(Color.WHITE) + "to fall from the sky and hit",
                            ChatColor.of(Color.WHITE) + "your target. More damage is dealt",
                            ChatColor.of(Color.WHITE) + "depending on how may chaos shards",
                            ChatColor.of(Color.WHITE) + "you possess");
                }

                case 4:{
                    return itemGetter.getItem(Material.PURPLE_DYE,0,
                            ChatColor.of(mysticColor) + "Flickering Chaos",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                            "",
                            ChatColor.of(Color.WHITE) + "Teleport, either toward your target",
                            ChatColor.of(Color.WHITE) + "or in front you");
                }

                case 5:{
                    return itemGetter.getItem(Material.PURPLE_DYE,0,
                            ChatColor.of(mysticColor) + "Spiritual Descent",
                            ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                            ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getSpiritualDescent().getSkillDamage(player)) + " damage x 8",
                            "",
                            ChatColor.of(Color.WHITE) + "Erupt the ground under your target",
                            ChatColor.of(Color.WHITE) + "with chaos energy.",
                            ChatColor.of(Color.WHITE) + "Using this skill gains a chaos shard");
                }

                case 6: {
                    return itemGetter.getItem(Material.PURPLE_DYE,0,
                            ChatColor.of(mysticColor) + "Chaos Lash",
                            ChatColor.of(new Color(0, 102, 0)) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                            ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getChaosLash().getSkillDamage(player)) + " power",
                            "",
                            ChatColor.of(new Color(230, 230, 230)) + "Release chaotic flames toward your",
                            ChatColor.of(new Color(230, 230, 230)) + "target during a short period of time",
                            ChatColor.of(new Color(230, 230, 230)) + "dealing massive damage. If the target",
                            ChatColor.of(new Color(230, 230, 230)) + "is cursed, you gain two chaos shards");
                }

                case 7: {
                    return itemGetter.getItem(Material.PURPLE_DYE,0,
                            ChatColor.of(mysticColor) + "Cursing Voice",
                            ChatColor.of(new Color(0, 102, 0)) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                            "",
                            ChatColor.of(new Color(230, 230, 230)) + "Cause your target to fall asleep.",
                            ChatColor.of(new Color(230, 230, 230)) + "If they take damage, they awaken");
                }

                case 8: {
                    return itemGetter.getItem(Material.PURPLE_DYE,0,
                            ChatColor.of(mysticColor) + "Health Absorb",
                            ChatColor.of(new Color(0, 102, 0)) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                            ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getHealthAbsorb().getSkillDamage(player)) + " power",
                            "",
                            ChatColor.of(new Color(230, 230, 230)) + "Leech life from your target, restoring",
                            ChatColor.of(new Color(230, 230, 230)) + "your own health");
                }

            }

        }

        switch (number){

            case 1:{
                return itemGetter.getItem(Material.PURPLE_DYE,1,
                        ChatColor.of(mysticColor) + "Arcane Shield",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getMysticAbilities().getArcaneShield().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Raise a shield of arcane energy",
                        ChatColor.of(Color.WHITE) + "around your target that absorbs",
                        ChatColor.of(Color.WHITE) + "damage");
            }

            case 2:{
                return itemGetter.getItem(Material.PURPLE_DYE,2,
                        ChatColor.of(mysticColor) + "Purifying Blast",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getPurifyingBlast().getSkillDamage(player)) + " power",
                        ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getPurifyingBlast().getSkillDamage(player)) + " heal power",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getMysticAbilities().getPurifyingBlast().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Surround yourself with a ring",
                        ChatColor.of(Color.WHITE) + "of arcane energy, healing allies",
                        ChatColor.of(Color.WHITE) + "and damaging enemies");
            }

            case 3:{
                return itemGetter.getItem(Material.PURPLE_DYE,3,
                        ChatColor.of(mysticColor) + "Force of Will",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getForceOfWill().getSkillDamage(player)) + " power",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getMysticAbilities().getForceOfWill().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Channel arcane energy to deal",
                        ChatColor.of(Color.WHITE) + "continuous damage to your target");
            }

            case 4:{
                return itemGetter.getItem(Material.PURPLE_DYE,4,
                        ChatColor.of(mysticColor) + "Dreadfall",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getDreadfall().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a void meteor that deals",
                        ChatColor.of(Color.WHITE) + "heavy area damage");
            }

            case 5:{
                return itemGetter.getItem(Material.PURPLE_DYE,5,
                        ChatColor.of(mysticColor) + "Void Walk",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Teleport, either toward your target",
                        ChatColor.of(Color.WHITE) + "or in front you");
            }

            case 6:{
                return itemGetter.getItem(Material.PURPLE_DYE,6,
                        ChatColor.of(mysticColor) + "Aurora",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getMysticAbilities().getAurora().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon an arcane energy barrier to",
                        ChatColor.of(Color.WHITE) + "surround the target ally. All allies",
                        ChatColor.of(Color.WHITE) + "within are protected by a shield");
            }

            case 7:{
                return itemGetter.getItem(Material.PURPLE_DYE,7,
                        ChatColor.of(mysticColor) + "Arcane Contract",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getMysticAbilities().getArcaneContract().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Revive one dead player. Using this",
                        ChatColor.of(Color.WHITE) + "skill also puts it in cooldown for",
                        ChatColor.of(Color.WHITE) + "allied mystics");
            }

            case 8:{
                return itemGetter.getItem(Material.PURPLE_DYE,8,
                        ChatColor.of(mysticColor) + "Light Sigil",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getLightSigil().getSkillDamage(player)) + " power",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getMysticAbilities().getLightSigil().getCost()) + " mana",
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
        SubClass subClass = playerProfile.getPlayerSubclass();

        switch (subClass){
            case Chaos:{
                return itemGetter.getItem(Material.PURPLE_DYE,0,
                        ChatColor.of(mysticColor) + "Evil Spirit",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getMysticBasic().getEvilSpiritDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Transform into the embodiment",
                        ChatColor.of(Color.WHITE) + "of chaos, with the one goal",
                        ChatColor.of(Color.WHITE) + "of destroying your enemies.",
                        ChatColor.of(Color.WHITE) + "Your skills build up chaos ",
                        ChatColor.of(Color.WHITE) + "shards. When you have six",
                        ChatColor.of(Color.WHITE) + "this skill is able to be cast");
            }

            case Arcane:{
                return itemGetter.getItem(Material.PURPLE_DYE,9,
                        ChatColor.of(mysticColor) + "Arcane Missiles",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getArcaneMissiles().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Rapidly fire multiple projectiles",
                        ChatColor.of(Color.WHITE) + "of pure arcane energy that bombard",
                        ChatColor.of(Color.WHITE) + "your enemy");
            }

            case Shepard:{
                return itemGetter.getItem(Material.PURPLE_DYE,10,
                        ChatColor.of(mysticColor) + "Enlightenment",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getEnlightenment().getHealPercent(player)) + " heal power",
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getMysticAbilities().getEnlightenment().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Your basic attacks and " + ChatColor.of(mysticColor) + "Purifying Blast",
                        ChatColor.of(Color.WHITE) + "Mark your targets with " + ChatColor.of(mysticColor) + "Consolation.",
                        "",
                        ChatColor.of(Color.WHITE) + "Heal all marked targets",
                        ChatColor.of(Color.WHITE) + "and give them a 40% damage",
                        ChatColor.of(Color.WHITE) + "reduction buff for 5 seconds");
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
        basicLore.add(ChatColor.of(mysticColor) + String.valueOf(abilityManager.getMysticAbilities().getMysticBasic().getSkillDamage(player)) + " power");

        assert basicMeta != null;
        basicMeta.setLore(basicLore);
        basicItem.setItemMeta(basicMeta);


        return basicItem;
    }



}
