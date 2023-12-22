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

public class WarriorSkillItems {

    private final ProfileManager profileManager;

    public WarriorSkillItems(Mystica main){
        profileManager = main.getProfileManager();
    }

    public ItemStack getSkill(int number, Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);
        Skill_Level skillLevel = playerProfile.getSkillLevels();


        switch(number){

            case 1:{
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 61)) + "Lava Quake",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_1_Level() + skillLevel.getSkill_1_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Deal damage to enemies in",
                        ChatColor.of(new Color(230,230,230)) + "a cone in front of you. In",
                        ChatColor.of(new Color(230,230,230)) + "addition, shield yourself");
            }
            case 2:{
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 61)) + "Searing Chains",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_2_Level() + skillLevel.getSkill_2_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Snare nearby enemies in",
                        ChatColor.of(new Color(230,230,230)) + "a cone in front of you,",
                        ChatColor.of(new Color(230,230,230)) + "pulling them closer. Enemies",
                        ChatColor.of(new Color(230,230,230)) + "hit are taunted as well as slowed");
            }
            case 3:{
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 61)) + "Tempest Rage",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_3_Level() + skillLevel.getSkill_3_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Deal continuous moderate",
                        ChatColor.of(new Color(230,230,230)) + "damage to nearby enemies");
            }
            case 4:{
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 61)) + "Meteor Crater",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_4_Level() + skillLevel.getSkill_4_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Deal heavy damage and",
                        ChatColor.of(new Color(230,230,230)) + "inflict stun. Extra damage",
                        ChatColor.of(new Color(230,230,230)) + "is dealt if the target is above",
                        ChatColor.of(new Color(230,230,230)) + "70% of their max Hp");
            }
            case 5:{
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 61)) + "Anvil Drop",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_5_Level() + skillLevel.getSkill_5_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Leap towards the target",
                        ChatColor.of(new Color(230,230,230)) + "and knock them in the air.",
                        ChatColor.of(new Color(230,230,230)) + "If you are already within",
                        ChatColor.of(new Color(230,230,230)) + "range, knock them in the",
                        ChatColor.of(new Color(230,230,230)) + "air without leaping");
            }
            case 6:{
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 61)) + "Flaming Sigil",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_6_Level() + skillLevel.getSkill_6_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Strike the ground with",
                        ChatColor.of(new Color(230,230,230)) + "your totem, increasing your",
                        ChatColor.of(new Color(230,230,230)) + "max Hp as well as Attack");
            }
            case 7:{
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 61)) + "Magma Spikes",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_7_Level() + skillLevel.getSkill_7_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Deal heavy damage to the",
                        ChatColor.of(new Color(230,230,230)) + "nearby area, juggling all",
                        ChatColor.of(new Color(230,230,230)) + "nearby foes");
            }
            case 8:{
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 61)) + "Burning Blessing",
                        ChatColor.of(new Color(0,102,0)) + "Level " + (skillLevel.getSkill_8_Level() + skillLevel.getSkill_8_Level_Bonus()),
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Temporarily increase your max",
                        ChatColor.of(new Color(230,230,230)) + "Hp, as well as heal yourself",
                        ChatColor.of(new Color(230,230,230)) + "for 25% of it");
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
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 102)) + "Gladiator Heart",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Gain a shield to absorb your",
                        ChatColor.of(new Color(230,230,230)) + "level% of your max health for",
                        ChatColor.of(new Color(230,230,230)) + "5 seconds. While active",
                        ChatColor.of(new Color(230,230,230)) + "increase your damage resistance");
            }
            case "executioner":{
                return getItem(Material.ORANGE_DYE, 0,
                        ChatColor.of(new Color(214, 126, 61)) + "Death Gaze",
                        ChatColor.of(new Color(0,102,0)) + "Level " + level,
                        "",
                        ChatColor.of(new Color(230,230,230)) + "Hook your target, pulling",
                        ChatColor.of(new Color(230,230,230)) + "them towards you and stunning",
                        ChatColor.of(new Color(230,230,230)) + "them. If they are unable to be",
                        ChatColor.of(new Color(230,230,230)) + "pulled, go to their position",
                        ChatColor.of(new Color(230,230,230)) + "instead");
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
