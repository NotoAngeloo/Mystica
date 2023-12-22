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

public class AssassinSkillItems {

    private final ProfileManager profileManager;

    public AssassinSkillItems(Mystica main){
        profileManager = main.getProfileManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(214, 61, 207)) + "Assault",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_1_Level() + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Assault your target, inflicting",
                        ChatColor.of(new Color(230,230,230)) + "damage and granting 1 combo point");
            }
            case 2:{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(214, 61, 207)) + "Laceration",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_2_Level() + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Strike your target applying",
                        ChatColor.of(new Color(230,230,230)) + "bleeding damage over time",
                        ChatColor.of(new Color(230,230,230)) + "and granting 1 combo point");
            }
            case 3:{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(214, 61, 207)) + "Weakness Strike",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_3_Level() + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Attack your target with",
                        ChatColor.of(new Color(230,230,230)) + "all your might, consuming",
                        ChatColor.of(new Color(230,230,230)) + "your combo points to deal",
                        ChatColor.of(new Color(230,230,230)) + "increased damage");
            }
            case 4:{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(214, 61, 207)) + "Pierce",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_4_Level() + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Pierce through your targets",
                        ChatColor.of(new Color(230,230,230)) + "armor. For 10 seconds your",
                        ChatColor.of(new Color(230,230,230)) + "attacks ignore 25% of their",
                        ChatColor.of(new Color(230,230,230)) + "defense. Costs 1 combo point");
            }
            case 5:{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(214, 61, 207)) + "Dash",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_5_Level() + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Increase your movement speed.",
                        ChatColor.of(new Color(230,230,230)) + "This can be used without breaking",
                        ChatColor.of(new Color(230,230,230)) + "from stealth");
            }
            case 6:{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(214, 61, 207)) + "Blade Tempest",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_6_Level() + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Unleash a flurry of knives",
                        ChatColor.of(new Color(230,230,230)) + "around you, damaging all",
                        ChatColor.of(new Color(230,230,230)) + "nearby enemies. Only grants",
                        ChatColor.of(new Color(230,230,230)) + "a combo point if an enemy is hit");
            }
            case 7:{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(214, 61, 207)) + "Flying Blade",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_7_Level() + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Throw your weapon towards",
                        ChatColor.of(new Color(230,230,230)) + "your enemy and stunning",
                        ChatColor.of(new Color(230,230,230)) + "them if it hits");
            }
            case 8:{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(214, 61, 207)) + "Stealth",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_8_Level() + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Hide in the shadows. Your",
                        ChatColor.of(new Color(230,230,230)) + "next attack while stealthed",
                        ChatColor.of(new Color(230,230,230)) + "reveals you and deals additional",
                        ChatColor.of(new Color(230,230,230)) + "damage");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        int level = playerProfile.getStats().getLevel();
        String subClass = playerProfile.getPlayerSubclass();

        switch(subClass.toLowerCase()){
            case "duelist":{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(214, 61, 207)) + "Duelist's Frenzy",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Consume your combo points",
                        ChatColor.of(new Color(230,230,230)) + "to deal a massive blow.",
                        ChatColor.of(new Color(230,230,230)) + "For the next 15 seconds,",
                        ChatColor.of(new Color(230,230,230)) + "every basic attack grants",
                        ChatColor.of(new Color(230,230,230)) + "2 combo points.",
                        ChatColor.of(new Color(230,230,230)) + "Requires 6 Combo Points");
            }
            case "alchemist":{
                return getItem(Material.PINK_DYE, 0,
                        ChatColor.of(new Color(248, 61, 119)) + "Wicked Concoction",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Throw a potion at your",
                        ChatColor.of(new Color(230,230,230)) + "target. If the target",
                        ChatColor.of(new Color(230,230,230)) + "is an ally, restore their",
                        ChatColor.of(new Color(230,230,230)) + "health and grant damage",
                        ChatColor.of(new Color(230,230,230)) + "reduction for 15 seconds",
                        ChatColor.of(new Color(230,230,230)) + "If the target is an enemy",
                        ChatColor.of(new Color(230,230,230)) + "they take increased damage",
                        ChatColor.of(new Color(230,230,230)) + "for 15 seconds.");
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
