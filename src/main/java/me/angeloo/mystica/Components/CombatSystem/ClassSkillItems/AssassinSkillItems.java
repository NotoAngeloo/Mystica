package me.angeloo.mystica.Components.CombatSystem.ClassSkillItems;

import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.Skill_Level;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.EquipmentSlot;
import me.angeloo.mystica.Utility.InventoryItemGetter;
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

public class AssassinSkillItems {

    private final InventoryItemGetter itemGetter;
    private final MysticaEquipment weapon;
    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;

    public AssassinSkillItems(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        itemGetter = main.getItemGetter();
        abilityManager = manager;
        this.weapon = new MysticaEquipment(EquipmentSlot.WEAPON, PlayerClass.Assassin, 1);
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return itemGetter.getItem(Material.PINK_DYE,1,
                        ChatColor.of(assassinColor) + "Assault",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getAssault().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Assault your target, inflicting",
                        ChatColor.of(Color.WHITE) + "damage and granting 1 combo point");
            }
            case 2:{
                return itemGetter.getItem(Material.PINK_DYE,2,
                        ChatColor.of(assassinColor) + "Laceration",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getLaceration().getSkillDamage(player)) + " power",
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getLaceration().getBleedDamage(player)) + " x 10 damage over time",
                        "",
                        ChatColor.of(Color.WHITE) + "Strike your target applying",
                        ChatColor.of(Color.WHITE) + "bleeding damage over time",
                        ChatColor.of(Color.WHITE) + "and granting 1 combo point");
            }
            case 3:{
                return itemGetter.getItem(Material.PINK_DYE,3,
                        ChatColor.of(assassinColor) + "Weakness Strike",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getWeaknessStrike().getSkillDamage(player)) + " power + (15 x combo)",
                        "",
                        ChatColor.of(Color.WHITE) + "Attack your target with",
                        ChatColor.of(Color.WHITE) + "all your might, consuming",
                        ChatColor.of(Color.WHITE) + "your combo points to deal",
                        ChatColor.of(Color.WHITE) + "increased damage");
            }
            case 4:{
                return itemGetter.getItem(Material.PINK_DYE,5,
                        ChatColor.of(assassinColor) + "Pierce",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getPierce().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Pierce through your targets",
                        ChatColor.of(Color.WHITE) + "armor. For 10 seconds your",
                        ChatColor.of(Color.WHITE) + "attacks ignore 25% of their",
                        ChatColor.of(Color.WHITE) + "defense. Costs 1 combo point");
            }
            case 5:{
                return itemGetter.getItem(Material.PINK_DYE,7,
                        ChatColor.of(assassinColor) + "Dash",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        ChatColor.of(Color.BLUE) + String.valueOf(abilityManager.getAssassinAbilities().getDash().getCost()) + " mana",
                        "",
                        ChatColor.of(Color.WHITE) + "Increase your movement speed.",
                        ChatColor.of(Color.WHITE) + "This can be used without breaking",
                        ChatColor.of(Color.WHITE) + "from stealth");
            }
            case 6:{
                return itemGetter.getItem(Material.PINK_DYE,8,
                        ChatColor.of(assassinColor) + "Blade Tempest",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getBladeTempest().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Unleash a flurry of knives",
                        ChatColor.of(Color.WHITE) + "around you, damaging all",
                        ChatColor.of(Color.WHITE) + "nearby enemies. Only grants",
                        ChatColor.of(Color.WHITE) + "a combo point if an enemy is hit");
            }
            case 7:{
                return itemGetter.getItem(Material.PINK_DYE,9,
                        ChatColor.of(assassinColor) + "Flying Blade",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getFlyingBlade().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Throw your weapon towards",
                        ChatColor.of(Color.WHITE) + "your enemy and stunning",
                        ChatColor.of(Color.WHITE) + "them if it hits");
            }
            case 8:{
                return itemGetter.getItem(Material.PINK_DYE,10,
                        ChatColor.of(assassinColor) + "Stealth",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getStealth().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Hide in the shadows. Your",
                        ChatColor.of(Color.WHITE) + "next attack while stealthed",
                        ChatColor.of(Color.WHITE) + "reveals you and deals additional",
                        ChatColor.of(Color.WHITE) + "damage");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        int level = playerProfile.getStats().getLevel();
        SubClass subClass = playerProfile.getPlayerSubclass();

        switch(subClass){
            case Duelist:{
                return itemGetter.getItem(Material.PINK_DYE,12,
                        ChatColor.of(assassinColor) + "Duelist's Frenzy",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getDuelistsFrenzy().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Consume your combo points",
                        ChatColor.of(Color.WHITE) + "to deal a massive blow.",
                        ChatColor.of(Color.WHITE) + "For the next 15 seconds,",
                        ChatColor.of(Color.WHITE) + "every basic attack grants",
                        ChatColor.of(Color.WHITE) + "a combo point.",
                        ChatColor.of(Color.WHITE) + "Requires 6 Combo Points");
            }
            case Alchemist:{
                return itemGetter.getItem(Material.PINK_DYE,14,
                        ChatColor.of(assassinColor) + "Wicked Concoction",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getWickedConcoction().getSkillDamage(player)) + " power",
                        ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getWickedConcoction().getHealPower()) + " heal power",
                        "",
                        ChatColor.of(Color.WHITE) + "Throw a potion at your",
                        ChatColor.of(Color.WHITE) + "target. If the target",
                        ChatColor.of(Color.WHITE) + "is an ally, restore their",
                        ChatColor.of(Color.WHITE) + "health and grant damage",
                        ChatColor.of(Color.WHITE) + "reduction for 15 seconds",
                        ChatColor.of(Color.WHITE) + "If the target is an enemy",
                        ChatColor.of(Color.WHITE) + "they take increased damage",
                        ChatColor.of(Color.WHITE) + "for 15 seconds.");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getBasic(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        ItemStack basicItem = weapon.build();

        ItemMeta basicMeta = basicItem.getItemMeta();;
        List<String> basicLore = new ArrayList<>();

        int level = playerProfile.getStats().getLevel();
        basicLore.add(ChatColor.of(levelColor) + "Level "  + level);
        basicLore.add(ChatColor.of(assassinColor) + String.valueOf(abilityManager.getAssassinAbilities().getAssassinBasic().getSkillDamage(player)) + " power");

        assert basicMeta != null;
        basicMeta.setLore(basicLore);
        basicItem.setItemMeta(basicMeta);

        return basicItem;
    }



}
