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

public class ShadowKnightSkillItems {

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;

    public ShadowKnightSkillItems(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        abilityManager = manager;
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return getItem(1,
                        ChatColor.of(shadowKnightColor) + "Infection",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        ChatColor.of(shadowKnightColor) + String.valueOf(abilityManager.getShadowKnightAbilities().getInfection().getSkillDamage(player)) + " damage",
                        "",
                        ChatColor.of(Color.WHITE) + "Launch a projectile that infects your",
                        ChatColor.of(Color.WHITE) + "target for continuous damage");
            }
            case 2:{
                return getItem(2,
                        ChatColor.of(shadowKnightColor) + "Spiritual Attack",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        ChatColor.of(shadowKnightColor) + String.valueOf(abilityManager.getShadowKnightAbilities().getSpiritualAttack().getSkillDamage(player)) + " damage",
                        ChatColor.of(Color.RED) + String.valueOf(abilityManager.getShadowKnightAbilities().getSpiritualAttack().getCost()) + " energy",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon the hand of your spiritual",
                        ChatColor.of(Color.WHITE) + "ally to strike your target from above");
            }
            case 3:{
                return getItem(3,
                        ChatColor.of(shadowKnightColor) + "Burial Ground",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        ChatColor.of(shadowKnightColor) + String.valueOf(abilityManager.getShadowKnightAbilities().getBurialGround().getHealPercent(player)) + "% healed",
                        ChatColor.of(Color.RED) + String.valueOf(abilityManager.getShadowKnightAbilities().getBurialGround().getEnergyRefund()) + " energy restored / tick",
                        "",
                        ChatColor.of(Color.WHITE) + "Summon a graveyard beneath your",
                        ChatColor.of(Color.WHITE) + "feet. Standing within restores",
                        ChatColor.of(Color.WHITE) + "your health and your energy",
                        ChatColor.of(Color.WHITE) + "over time.");
            }
            case 4:{
                return getItem(4,
                        ChatColor.of(shadowKnightColor) + "Bloodsucker",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        ChatColor.of(shadowKnightColor) + String.valueOf(abilityManager.getShadowKnightAbilities().getBloodsucker().getSkillDamage(player)) + " damage",
                        ChatColor.of(shadowKnightColor) + String.valueOf(abilityManager.getShadowKnightAbilities().getBloodsucker().getHealPercent(player)) + "% healed",
                        ChatColor.of(Color.RED) + String.valueOf(abilityManager.getShadowKnightAbilities().getBloodsucker().getCost()) + " energy",
                        "",
                        ChatColor.of(Color.WHITE) + "Leech life from your target",
                        ChatColor.of(Color.WHITE) + "to restore your own health.");
            }
            case 5:{
                return getItem(5,
                        ChatColor.of(shadowKnightColor) + "Soul Reap",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        ChatColor.of(shadowKnightColor) + String.valueOf(abilityManager.getShadowKnightAbilities().getSoulReap().getSkillDamage(player)) + " damage + modifiers",
                        ChatColor.of(Color.RED) + String.valueOf(abilityManager.getShadowKnightAbilities().getSoulReap().getCost()) + " energy",
                        "",
                        ChatColor.of(Color.WHITE) + "Strike your target, piercing",
                        ChatColor.of(Color.WHITE) + "their very soul. If they",
                        ChatColor.of(Color.WHITE) + "are beneath 30% of their",
                        ChatColor.of(Color.WHITE) + "maximum health, damage is",
                        ChatColor.of(Color.WHITE) + "increased by 30%");
            }
            case 6:{
                return getItem(6,
                        ChatColor.of(shadowKnightColor) + "Shadow Grip",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        ChatColor.of(shadowKnightColor) + String.valueOf(abilityManager.getShadowKnightAbilities().getShadowGrip().getSkillDamage(player)) + " damage",
                        ChatColor.of(Color.RED) + String.valueOf(abilityManager.getShadowKnightAbilities().getShadowGrip().getCost()) + " energy",
                        "",
                        ChatColor.of(Color.WHITE) + "The hand of your spiritual",
                        ChatColor.of(Color.WHITE) + "ally pulls your target toward",
                        ChatColor.of(Color.WHITE) + "you. This ability causes",
                        ChatColor.of(Color.WHITE) + "the hit enemy to target",
                        ChatColor.of(Color.WHITE) + "you");
            }
            case 7:{
                return getItem(7,
                        ChatColor.of(shadowKnightColor) + "Spectral Steed",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Summon your spectral steed",
                        ChatColor.of(Color.WHITE) + "to increase your combative",
                        ChatColor.of(Color.WHITE) + "advantage. Your movement",
                        ChatColor.of(Color.WHITE) + "speed is increased and you",
                        ChatColor.of(Color.WHITE) + "may use abilities while mounted");
            }
            case 8:{
                return getItem(8,
                        ChatColor.of(shadowKnightColor) + "Soulcrack",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        ChatColor.of(shadowKnightColor) + String.valueOf(abilityManager.getShadowKnightAbilities().getSoulcrack().getSkillDamage(player)) + " damage",
                        ChatColor.of(Color.RED) + String.valueOf(abilityManager.getShadowKnightAbilities().getSoulcrack().getEnergyRestored()) + " energy restored",
                        "",
                        ChatColor.of(Color.WHITE) + "Strike all foes around you.",
                        ChatColor.of(Color.WHITE) + "This ability restores your",
                        ChatColor.of(Color.WHITE) + "energy instead of consuming",
                        ChatColor.of(Color.WHITE) + "it.");
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
                return getItem(9,
                        ChatColor.of(shadowKnightColor) + "Blood Shield",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(Color.RED) + String.valueOf(abilityManager.getShadowKnightAbilities().getBloodShield().getCost()) + " energy",
                        "",
                        ChatColor.of(Color.WHITE) + "Instantly heal 50% of your",
                        ChatColor.of(Color.WHITE) + "missing health. Shield yourself",
                        ChatColor.of(Color.WHITE) + "for an equivalent of your current",
                        ChatColor.of(Color.WHITE) + "health for 10 seconds. While active,",
                        ChatColor.of(Color.WHITE) + "casting " + ChatColor.of(shadowKnightColor) + "Bloodsucker " +
                                ChatColor.of(Color.WHITE) + "increases the",
                        ChatColor.of(Color.WHITE) + "duration by 3 seconds");
            }
            case "doom":{
                return getItem(10,
                        ChatColor.of(shadowKnightColor) + "Annihilation",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(shadowKnightColor) + String.valueOf(abilityManager.getShadowKnightAbilities().getAnnihilation().getSkillDamage(player)) + " damage",
                        ChatColor.of(Color.RED) + String.valueOf(abilityManager.getShadowKnightAbilities().getAnnihilation().getCost()) + " energy",
                        "",
                        ChatColor.of(Color.WHITE) + "Your spiritual ally strikes",
                        ChatColor.of(Color.WHITE) + "your foe with a powerful sweep,",
                        ChatColor.of(Color.WHITE) + "enhancing any " +
                                ChatColor.of(shadowKnightColor) + "Infections " + ChatColor.of(Color.WHITE) + "and",
                        ChatColor.of(Color.WHITE) + "refreshing its duration");
            }
        }

        return new ItemStack(Material.AIR);
    }

    private ItemStack getItem(int modelData, String name, String ... lore) {

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);

        ItemStack item = new ItemStack(Material.RED_DYE);

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
