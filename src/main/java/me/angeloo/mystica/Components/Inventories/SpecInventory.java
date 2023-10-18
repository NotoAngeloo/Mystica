package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpecInventory {

    private final ProfileManager profileManager;

    public SpecInventory(Mystica main){
        profileManager = main.getProfileManager();
    }

    public Inventory openSpecInventory(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        Inventory inv = Bukkit.createInventory(null, 9*2, "Specializations");

        for(int i=0;i<18;i++){
            inv.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, 0, " "));
        }

        String clazz = playerProfile.getPlayerClass();
        String subClass = playerProfile.getPlayerSubclass();

        inv.setItem(4, getSubclassItem(subClass));

        inv.setItem(8, getItem(Material.EMERALD, 0, "Back"));

        switch (clazz.toLowerCase()){
            case "elementalist":{
                inv.setItem(11, getPyromancerItem());
                inv.setItem(13, getConjurerItem());
                inv.setItem(15, getCryomancerItem());
                break;
            }
            case "ranger":{
                inv.setItem(11, getScoutItem());
                inv.setItem(15, getTamerItem());
                break;
            }
        }

        return inv;
    }

    private ItemStack getSubclassItem(String subclass){

        switch (subclass.toLowerCase()){
            case "pyromancer":
                return getPyromancerItem();
            case "conjurer":
                return getConjurerItem();
            case "cryomancer":
                return getCryomancerItem();
            case "scout":
                return getScoutItem();
            case "animal tamer":
                return getTamerItem();
        }
        return new ItemStack(Material.AIR);
    }

    private ItemStack getPyromancerItem(){
        return getItem(Material.TORCH, 0,
                ChatColor.of(new Color(250, 102, 0)) + "Pyromancer",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Magic, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                ChatColor.of(new Color(230,230,230)) + "Every 4 fire skills cast consecutively refresh",
                ChatColor.of(new Color(230,230,230)) + "your " + ChatColor.of(new Color(250,102,0)) + "Fiery Wing",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(250, 102, 0)) + "Fiery Wing",
                "",
                ChatColor.of(new Color(230,230,230)) + "Summon an elemental that charges",
                ChatColor.of(new Color(230,230,230)) + "towards an enemy, dealing massive",
                ChatColor.of(new Color(230,230,230)) + "damage on arrival");
    }

    private ItemStack getConjurerItem(){
        return getItem(Material.AMETHYST_SHARD, 0,
                ChatColor.of(new Color(153, 0, 255)) + "Conjurer",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+30 Health, +2 Magic, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt, Descending Inferno, " +
                        ChatColor.of(new Color(230,230,230)) + "and " + ChatColor.of(new Color(153, 204, 255)) + "Elemental Matrix",
                ChatColor.of(new Color(230,230,230)) + "all do extra damage based on current mana pool",
                ChatColor.of(new Color(153, 204, 255)) + "Elemental Matrix " +
                        ChatColor.of(new Color(230,230,230)) + "restores mana to the caster",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(153, 0, 255)) + "Conjuring Force",
                "",
                ChatColor.of(new Color(230,230,230)) + "Summon a force shield centered around",
                ChatColor.of(new Color(230,230,230)) + "you, enhancing damage and range of all",
                ChatColor.of(new Color(230,230,230)) + "allies within");
    }

    private ItemStack getCryomancerItem(){
        return getItem(Material.PRISMARINE_CRYSTALS, 0,
                ChatColor.of(new Color(153, 204, 255)) + "Cryomancer",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Magic, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Damage of " + ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt " +
                        ChatColor.of(new Color(230,230,230)) + "increased 50%",
                ChatColor.of(new Color(230,230,230)) + "Damage of " + ChatColor.of(new Color(153, 204, 255)) + "Elemental Matrix " +
                        ChatColor.of(new Color(230,230,230)) + "increased 100%",
                ChatColor.of(new Color(230,230,230)) + "Using " + ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt " +
                        ChatColor.of(new Color(230,230,230)) + "decreases " + ChatColor.of(new Color(153, 204, 255)) + "Elemental Breath ",
                ChatColor.of(new Color(230,230,230)) + "Cooldown by 5 seconds",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(153, 204, 255)) + "Crystal Storm",
                "",
                ChatColor.of(new Color(230,230,230)) + "Summon a storm cloud that pelts",
                ChatColor.of(new Color(230,230,230)) + "the area with icicles. Hitting",
                ChatColor.of(new Color(230,230,230)) + "an enemy effected by the storm",
                ChatColor.of(new Color(230,230,230)) + "with " + ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt " +
                        ChatColor.of(new Color(230,230,230)) + "resets the cooldown",
                ChatColor.of(new Color(230,230,230)) + "of "  +ChatColor.of(new Color(153, 204, 255)) + "Ice Bolt");
    }

    private ItemStack getScoutItem(){
        return getItem(Material.ARROW, 0,
                ChatColor.of(new Color(34, 111, 80)) + "Scout",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Attack, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                ChatColor.of(new Color(230,230,230)) + "Increases crit of " + ChatColor.of(new Color(34, 111, 80)) + "Razor Wind " +
                        ChatColor.of(new Color(230,230,230)) + "by 15%",
                ChatColor.of(new Color(230,230,230)) + "Increases haste by 10% after a skill",
                ChatColor.of(new Color(230,230,230)) + "inflicts a critical hit",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(34, 111, 80)) + "Starblade Volley",
                "",
                ChatColor.of(new Color(230,230,230)) + "Launch a blade towards the target",
                ChatColor.of(new Color(230,230,230)) + "Landing a crit with any skill",
                ChatColor.of(new Color(230,230,230)) + "decreases this skills cooldown");
    }

    private ItemStack getTamerItem(){
        return getItem(Material.BONE, 0,
                ChatColor.of(new Color(0, 117, 94)) + "Animal Tamer",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+30 Health, +2 Attack, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(34, 111, 80)) + "Shadow Crows " + ChatColor.of(new Color(230,230,230)) + "increases damage taken",
                ChatColor.of(new Color(230,230,230)) + "by the target by 10%",
                ChatColor.of(new Color(230,230,230)) + "Increases crit of " + ChatColor.of(new Color(34, 111, 80)) + "Shadow Crows " +
                        ChatColor.of(new Color(230,230,230)) + "by 15%",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(0, 117, 94)) + "Wild Roar",
                "",
                ChatColor.of(new Color(230,230,230)) + "Inspire 5 member of your team",
                ChatColor.of(new Color(230,230,230)) + "to deal increased damage"); //1.25% more per level
    }


    private ItemStack getItem(Material material, int modelData, String name, String ... lore){

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroer);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }


}
