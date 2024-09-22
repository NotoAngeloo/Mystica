package me.angeloo.mystica.Components.ClassSkillItems;

import me.angeloo.mystica.Components.ClassEquipment.NoneEquipment;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
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

public class WarriorSkillItems {

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;

    public WarriorSkillItems(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        abilityManager = manager;
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return getItem(Material.ORANGE_DYE, 1,
                        ChatColor.of(warriorColor) + "Lava Quake",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_1_Level_Bonus()),
                        ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getLavaQuake().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Deal damage to enemies in",
                        ChatColor.of(Color.WHITE) + "a cone in front of you. In",
                        ChatColor.of(Color.WHITE) + "addition, shield yourself");
            }
            case 2:{
                return getItem(Material.ORANGE_DYE, 2,
                        ChatColor.of(warriorColor) + "Searing Chains",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_2_Level_Bonus()),
                        ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getSearingChains().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Snare nearby enemies in",
                        ChatColor.of(Color.WHITE) + "a cone in front of you,",
                        ChatColor.of(Color.WHITE) + "pulling them closer. Enemies",
                        ChatColor.of(Color.WHITE) + "hit are taunted as well as slowed");
            }
            case 3:{
                return getItem(Material.ORANGE_DYE, 3,
                        ChatColor.of(warriorColor) + "Tempest Rage",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_3_Level_Bonus()),
                        ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getTempestRage().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Deal continuous moderate",
                        ChatColor.of(Color.WHITE) + "damage to nearby enemies");
            }
            case 4:{
                return getItem(Material.ORANGE_DYE, 4,
                        ChatColor.of(warriorColor) + "Meteor Crater",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_4_Level_Bonus()),
                        ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getMeteorCrater().getSkillDamage(player)) + " power",
                        ChatColor.of(warriorColor) + "Costs " + (abilityManager.getWarriorAbilities().getMeteorCrater().getCost()) + " rage",
                        "",
                        ChatColor.of(Color.WHITE) + "Deal heavy damage and",
                        ChatColor.of(Color.WHITE) + "inflict stun. Extra damage",
                        ChatColor.of(Color.WHITE) + "is dealt if the target is above",
                        ChatColor.of(Color.WHITE) + "70% of their max Hp");
            }
            case 5:{
                return getItem(Material.ORANGE_DYE, 5,
                        ChatColor.of(warriorColor) + "Anvil Drop",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_5_Level_Bonus()),
                        ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getAnvilDrop().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Leap towards the target",
                        ChatColor.of(Color.WHITE) + "and knock them in the air.",
                        ChatColor.of(Color.WHITE) + "If you are already within",
                        ChatColor.of(Color.WHITE) + "range, knock them in the",
                        ChatColor.of(Color.WHITE) + "air without leaping");
            }
            case 6:{
                return getItem(Material.ORANGE_DYE, 6,
                        ChatColor.of(warriorColor) + "Flaming Sigil",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_6_Level_Bonus()),
                        ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getFlamingSigil().getBuffAmount(player)) + " buff amount",
                        "",
                        ChatColor.of(Color.WHITE) + "Strike the ground with",
                        ChatColor.of(Color.WHITE) + "your totem, increasing your",
                        ChatColor.of(Color.WHITE) + "max Hp as well as Attack");
            }
            case 7:{
                return getItem(Material.ORANGE_DYE, 7,
                        ChatColor.of(warriorColor) + "Magma Spikes",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_7_Level_Bonus()),
                        ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getMagmaSpikes().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Deal heavy damage to the",
                        ChatColor.of(Color.WHITE) + "nearby area, juggling all",
                        ChatColor.of(Color.WHITE) + "nearby foes");
            }
            case 8:{
                return getItem(Material.ORANGE_DYE, 8,
                        ChatColor.of(warriorColor) + "Burning Blessing",
                        ChatColor.of(levelColor) + "Level " + (profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) + skillLevel.getSkill_8_Level_Bonus()),
                        ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getBurningBlessing().getBuffAmount(player)) + " buff amount",
                        "",
                        ChatColor.of(Color.WHITE) + "Temporarily increase your max",
                        ChatColor.of(Color.WHITE) + "Hp, as well as heal yourself",
                        ChatColor.of(Color.WHITE) + "for 25% of it");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        int level = playerProfile.getStats().getLevel();
        String subClass = playerProfile.getPlayerSubclass();

        switch(subClass.toLowerCase()){
            case "gladiator":{
                return getItem(Material.ORANGE_DYE, 9,
                        ChatColor.of(warriorColor) + "Gladiator Heart",
                        ChatColor.of(levelColor) + "Level " + level,
                        "",
                        ChatColor.of(Color.WHITE) + "Gain a shield to absorb your",
                        ChatColor.of(Color.WHITE) + String.valueOf(abilityManager.getWarriorAbilities().getGladiatorHeart().getShieldAmount(player)) +"% of your max health for",
                        ChatColor.of(Color.WHITE) + "5 seconds. While active",
                        ChatColor.of(Color.WHITE) + "increase your damage resistance");
            }
            case "executioner":{
                return getItem(Material.ORANGE_DYE, 10,
                        ChatColor.of(warriorColor) + "Death Gaze",
                        ChatColor.of(levelColor) + "Level " + level,
                        ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getDeathGaze().getSkillDamage(player)) + " power",
                        "",
                        ChatColor.of(Color.WHITE) + "Hook your target, pulling",
                        ChatColor.of(Color.WHITE) + "them towards you and stunning",
                        ChatColor.of(Color.WHITE) + "them. If they are unable to be",
                        ChatColor.of(Color.WHITE) + "pulled, go to their position",
                        ChatColor.of(Color.WHITE) + "instead");
            }
        }

        return new ItemStack(Material.AIR);
    }

    public ItemStack getBasic(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        ItemStack basicItem = new NoneEquipment().getBaseWeapon();

        PlayerEquipment equipment = playerProfile.getPlayerEquipment();

        ItemStack weapon = equipment.getWeapon();

        if(weapon != null && !weapon.getType().isAir()){
            basicItem = equipment.getWeapon().clone();
        }

        ItemMeta basicMeta = basicItem.getItemMeta();
        List<String> basicLore = new ArrayList<>();

        int level = playerProfile.getStats().getLevel();
        basicLore.add(ChatColor.of(levelColor) + "Level "  + level);
        basicLore.add(ChatColor.of(warriorColor) + String.valueOf(abilityManager.getWarriorAbilities().getWarriorBasic().getSkillDamage(player)) + " power");

        assert basicMeta != null;
        basicMeta.setLore(basicLore);
        basicItem.setItemMeta(basicMeta);


        return basicItem;
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
