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

public class NoneSkillItems {

    private final ProfileManager profileManager;

    public NoneSkillItems(Mystica main){
        profileManager = main.getProfileManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number) {

            case 1: {
                return getItem(Material.WHITE_DYE, 0,
                        ChatColor.of(Color.WHITE) + "Kick",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Kick your enemy for slightly more",
                        ChatColor.of(Color.WHITE) + "damage than punching them");
            }
            case 4: {
                return getItem(Material.WHITE_DYE, 0,
                        ChatColor.of(Color.WHITE) + "Dash",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Increase your movement speed.");
            }
            case 5: {
                return getItem(Material.WHITE_DYE, 0,
                        ChatColor.of(Color.WHITE) + "Roll",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Roll in the direction you are looking.");
            }
            case 3: {
                return getItem(Material.WHITE_DYE, 0,
                        ChatColor.of(Color.WHITE) + "Pocket Sand",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Throw sand in your enemies eyes,",
                        ChatColor.of(Color.WHITE) + "blinding them.");
            }
            case 6: {
                return getItem(Material.WHITE_DYE, 0,
                        ChatColor.of(Color.WHITE) + "Bandage",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Slow your movement to recover",
                        ChatColor.of(Color.WHITE) + "your health. Can be cast on allies.");
            }
            case 7: {
                return getItem(Material.WHITE_DYE, 0,
                        ChatColor.of(Color.WHITE) + "Block",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Slow your movement to block",
                        ChatColor.of(Color.WHITE) + "an attack");
            }
            case 2: {
                return getItem(Material.WHITE_DYE, 0,
                        ChatColor.of(Color.WHITE) + "Flail",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Swing wildly to damage all nearby",
                        ChatColor.of(Color.WHITE) + "enemies");
            }
            case 8: {
                return getItem(Material.WHITE_DYE, 0,
                        ChatColor.of(Color.WHITE) + "Adrenaline",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(Color.WHITE) + "Boost the power of your attacks.",
                        ChatColor.of(Color.WHITE) + "Can only be used at half health");
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
