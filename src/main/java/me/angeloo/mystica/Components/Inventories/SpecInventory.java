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
                //inv.setItem(13, getCryomancerItem());
                inv.setItem(15, getConjurerItem());
                break;
            }
            case "ranger":{
                inv.setItem(11, getScoutItem());
                inv.setItem(15, getTamerItem());
                break;
            }
            case "mystic":{
                inv.setItem(11, getShepardItem());
                //chaos is a secret subclass
                inv.setItem(13, getChaosItem());
                inv.setItem(15, getArcaneItem());
                break;
            }
            case "shadow knight":{
                inv.setItem(11, getBloodItem());
                inv.setItem(15, getDoomItem());
                break;
            }
            case "paladin":{
                inv.setItem(11, getTemplarItem());
                //secret
                inv.setItem(13, getDivineItem());
                inv.setItem(15, getDawnItem());
                break;
            }
            case "warrior":{
                inv.setItem(11, getGladiatorItem());
                inv.setItem(15, getExecutionerItem());
                break;
            }
            case "assassin":{
                inv.setItem(11, getDuelistItem());
                inv.setItem(15, getAlchemistItem());
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
            /*case "cryomancer":
                return getCryomancerItem();*/
            case "scout":
                return getScoutItem();
            case "animal tamer":
                return getTamerItem();
            case "chaos":
                return getChaosItem();
            case "arcane master":
                return getArcaneItem();
            case "shepard":
                return getShepardItem();
            case "doom":
                return getDoomItem();
            case "blood":
                return getBloodItem();
            case "templar":
                return getTemplarItem();
            case "divine":
                return getDivineItem();
            case "dawn":
                return getDawnItem();
            case "gladiator":
                return getGladiatorItem();
            case "executioner":
                return getExecutionerItem();
            case "duelist":
                return getDuelistItem();
            case "alchemist":
                return getAlchemistItem();
        }
        return new ItemStack(Material.AIR);
    }

    private ItemStack getPyromancerItem(){
        return getItem(Material.CYAN_DYE, 17,
                ChatColor.of(new Color(250, 102, 0)) + "Pyromancer",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Magic, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                ChatColor.of(new Color(230,230,230)) + "Every 4 hit fire skills cast refresh",
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
        return getItem(Material.CYAN_DYE, 21,
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

    /*private ItemStack getCryomancerItem(){
        return getItem(Material.CYAN_DYE, 19,
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
    }*/

    private ItemStack getScoutItem(){
        return getItem(Material.LIME_DYE, 17,
                ChatColor.of(new Color(34, 111, 80)) + "Scout",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Attack, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                ChatColor.of(new Color(230,230,230)) + "Increases crit of " + ChatColor.of(new Color(34, 111, 80)) + "Razor Wind " +
                        ChatColor.of(new Color(230,230,230)) + "by 15%",
                ChatColor.of(new Color(230,230,230)) + "Applies haste level 1 after a skill",
                ChatColor.of(new Color(230,230,230)) + "inflicts a critical hit",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(34, 111, 80)) + "Star Volley",
                "",
                ChatColor.of(new Color(230,230,230)) + "Summon an arrow from the stars",
                ChatColor.of(new Color(230,230,230)) + "Landing a crit with any skill",
                ChatColor.of(new Color(230,230,230)) + "decreases this skills cooldown");
    }

    private ItemStack getTamerItem(){
        return getItem(Material.LIME_DYE, 19,
                ChatColor.of(new Color(0, 117, 94)) + "Animal Tamer",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+30 Health, +2 Attack, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(34, 111, 80)) + "Shadow Crows " + ChatColor.of(new Color(230,230,230)) + "increases damage taken",
                ChatColor.of(new Color(230,230,230)) + "by the target by 10%",
                ChatColor.of(new Color(230,230,230)) + "Increases crit of " + ChatColor.of(new Color(34, 111, 80)) + "Shadow Crows " +
                        ChatColor.of(new Color(230,230,230)) + "by 15%",
                ChatColor.of(new Color(34,111,80)) + "Wild Spirit " + ChatColor.of(new Color(230,230,230)) + "now heals nearby",
                ChatColor.of(new Color(230,230,230)) + "players",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(0, 117, 94)) + "Wild Roar",
                "",
                ChatColor.of(new Color(230,230,230)) + "Inspire 5 member of your team",
                ChatColor.of(new Color(230,230,230)) + "to deal increased damage");
    }

    private ItemStack getChaosItem(){
        return getItem(Material.ENDER_EYE, 0,
                ChatColor.of(new Color(59, 14, 114)) + "Chaos",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Magic, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                "Your abilities are replaced with",
                "new ones",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(59, 14, 114)) + "Evil Spirit",
                "",
                ChatColor.of(new Color(230,230,230)) + "Transform into the embodiment",
                ChatColor.of(new Color(230,230,230)) + "of chaos, with the one goal",
                ChatColor.of(new Color(230,230,230)) + "of destroying your enemies.",
                ChatColor.of(new Color(230,230,230)) + "Your skills build up chaos ",
                ChatColor.of(new Color(230,230,230)) + "shards. When you have six",
                ChatColor.of(new Color(230,230,230)) + "this skill is able to be cast");

    }

    private ItemStack getArcaneItem(){
        return getItem(Material.DIAMOND, 0,
                ChatColor.of(new Color(155, 120, 197)) + "Arcane Master",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Magic, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                ChatColor.of(new Color(230,230,230)) + "Increases " + ChatColor.of(new Color(155, 120, 197)) + "Dreadfall " +
                        ChatColor.of(new Color(230,230,230)) + "damage by 50%.",
                ChatColor.of(new Color(230,230,230)) + "Landing a crit additionally deals",
                ChatColor.of(new Color(230,230,230)) + "15% of your Magic.",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(155, 120, 197)) + "Arcane Missiles",
                "",
                ChatColor.of(new Color(230,230,230)) + "Rapidly fire multiple projectiles",
                ChatColor.of(new Color(230,230,230)) + "of pure arcane energy that bombard",
                ChatColor.of(new Color(230,230,230)) + "your enemy");
    }

    private ItemStack getShepardItem(){
        return getItem(Material.GOLDEN_APPLE, 0,
                ChatColor.of(new Color(126, 101, 238)) + "Shepard",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+30 Health, +2 Magic, +100 Mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Increases healing by 20%.",
                "",
                ChatColor.of(new Color(155,120,197)) + "Arcane Shield " +
                        ChatColor.of(new Color(230,230,230)) + "restores the target's health",
                ChatColor.of(new Color(230,230,230)) + "by 30% every 20 seconds.",
                ChatColor.of(new Color(155,120,197)) + "Aurora " + ChatColor.of(new Color(230,230,230)) + "area restores health to",
                ChatColor.of(new Color(230,230,230)) + "all allies within",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(126, 101, 238)) + "Enlightenment",
                "",
                ChatColor.of(new Color(230,230,230)) + "Instantly heal nearby allies",
                ChatColor.of(new Color(230,230,230)) + "and give them a 10% damage",
                ChatColor.of(new Color(230,230,230)) + "reduction buff for 5 seconds");
    }

    private ItemStack getBloodItem(){
        return getItem(Material.RED_DYE, 0,
                ChatColor.of(new Color(213, 33, 3)) + "Blood",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+40 Health, +1 Attack, +2 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Gain Crushing Resistance",
                "",
                ChatColor.of(new Color(230,230,230)) + "Standing in " +
                        ChatColor.of(new Color(213, 33, 3)) + "Burial Ground " +
                ChatColor.of(new Color(230,230,230)) + "area",
                ChatColor.of(new Color(230,230,230)) + "gives you 20% damage reduction.",
                ChatColor.of(new Color(213, 33, 3)) + "Bloodsucker " +
                        ChatColor.of(new Color(230,230,230)) + "restores an additional",
                ChatColor.of(new Color(230,230,230)) + "30% of your maximum health",
                ChatColor.of(new Color(213, 33, 3)) + "Shadow Grip " +
                        ChatColor.of(new Color(230,230,230)) + "now taunts.",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(213, 33, 3)) + "Blood Shield",
                "",
                ChatColor.of(new Color(230,230,230)) + "Instantly heal 50% of your",
                ChatColor.of(new Color(230,230,230)) + "missing health. Shield yourself",
                ChatColor.of(new Color(230,230,230)) + "for an equivalent of your current",
                ChatColor.of(new Color(230,230,230)) + "health for 10 seconds. While active,",
                ChatColor.of(new Color(230,230,230)) + "casting " + ChatColor.of(new Color(213, 33, 3)) + "Bloodsucker " +
                        ChatColor.of(new Color(230,230,230)) + "increases the",
                ChatColor.of(new Color(230,230,230)) + "duration by 3 seconds");
    }

    private ItemStack getDoomItem(){
        return getItem(Material.RED_DYE, 0,
                ChatColor.of(new Color(3, 7, 219)) + "Doom",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+30 Health, +3 Attack, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                ChatColor.of(new Color(230,230,230)) + "Landing " + ChatColor.of(new Color(213, 33, 3)) + "Spiritual Attack " +
                        ChatColor.of(new Color(230,230,230)) + "on an enemy",
                ChatColor.of(new Color(230,230,230)) + "gives you a Soul Mark.",
                ChatColor.of(new Color(213, 33, 3)) + "Soul Reap " +
                        ChatColor.of(new Color(230,230,230)) + "consumes Soul Marks and",
                ChatColor.of(new Color(230,230,230)) + "Enhanced Infections to deal increased",
                ChatColor.of(new Color(230,230,230)) + "damage.",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(3, 7, 219)) + "Annihilation",
                "",
                ChatColor.of(new Color(230,230,230)) + "Your spiritual ally strikes",
                ChatColor.of(new Color(230,230,230)) + "your foe with a powerful sweep,",
                ChatColor.of(new Color(230,230,230)) + "enhancing any " +
                ChatColor.of(new Color(213, 33, 3)) + "Infections " + ChatColor.of(new Color(230,230,230)) + "and",
                ChatColor.of(new Color(230,230,230)) + "refreshing its duration");
    }

    private ItemStack getTemplarItem(){
        return getItem(Material.YELLOW_DYE, 0,
                ChatColor.of(new Color(207, 180, 80)) + "Templar",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+30 Health, +1 Attack, +200 mana, +2 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Gain Crushing Resistance",
                "",
                ChatColor.of(new Color(207, 214, 61)) + "Reigning Sword" +
                        ChatColor.of(new Color(230,230,230)) + " damage increases by 120%",
                ChatColor.of(new Color(230,230,230)) + "and shield increased by 20%.",
                ChatColor.of(new Color(207, 214, 61)) + "Judgement" +
                        ChatColor.of(new Color(230,230,230)) + " will now taunt the target.",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(207, 180, 80)) + "Shield of Sanctity",
                "",
                ChatColor.of(new Color(230,230,230)) + "Gain a shield to absorb your",
                ChatColor.of(new Color(230,230,230)) + "level% of your max health for",
                ChatColor.of(new Color(230,230,230)) + "5 seconds. While active",
                ChatColor.of(new Color(230,230,230)) + "restore your health.");
    }

    private ItemStack getDivineItem(){
        return getItem(Material.YELLOW_DYE, 0,
                ChatColor.of(new Color(207, 180, 129)) + "Divine",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+30 Health, +2 Attack, +100 mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Increases healing by 20%.",
                "",
                "Your abilities are replaced with",
                "new ones",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(207, 180, 129)) + "Representative",
                "",
                ChatColor.of(new Color(230,230,230)) + "Instantly heal nearby allies",
                ChatColor.of(new Color(230,230,230)) + "and grant yourself haste as",
                ChatColor.of(new Color(230,230,230)) + "well as your healing and damage",
                ChatColor.of(new Color(230,230,230)) + "for 10 seconds. This skill is",
                ChatColor.of(new Color(230,230,230)) + "unaffected by haste"
                );
    }

    private ItemStack getDawnItem(){
        return getItem(Material.YELLOW_DYE, 0,
                ChatColor.of(new Color(207, 214, 61)) + "Dawn",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Attack, +100 mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                ChatColor.of(new Color(207, 214, 61)) + "Torah Sword" +
                        ChatColor.of(new Color(230,230,230)) + " crit rate increased by 15%.",
                ChatColor.of(new Color(230,230,230)) + "Landing a crit refreshes the",
                ChatColor.of(new Color(230,230,230)) + "cooldown of " + ChatColor.of(new Color(207, 214, 61)) + "Judgement.",
                ChatColor.of(new Color(230,230,230)) + "In addition, your next " + ChatColor.of(new Color(207, 214, 61)) + "Judgement,",
                ChatColor.of(new Color(230,230,230)) + "Reigning Sword, or Sword of the Covenant's",
                ChatColor.of(new Color(230,230,230)) + "Damage will be increased by 80%.",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(207, 214, 61)) + "Well of Light",
                "",
                ChatColor.of(new Color(230,230,230)) + "Summon a well that deals",
                ChatColor.of(new Color(230,230,230)) + "area damage. This also",
                ChatColor.of(new Color(230,230,230)) + "leaves behind orbs that",
                ChatColor.of(new Color(230,230,230)) + "can be collected by you to",
                ChatColor.of(new Color(230,230,230)) + "increase your crit rate by 10%");
    }

    private ItemStack getExecutionerItem(){
        return getItem(Material.YELLOW_DYE, 0,
                ChatColor.of(new Color(214, 126, 61)) + "Executioner",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Attack, +100 mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                ChatColor.of(new Color(214, 126, 61)) + "Anvil Drop" +
                        ChatColor.of(new Color(230,230,230)) + " crit rate increased by 15%",
                ChatColor.of(new Color(230,230,230)) + "and damage increased by 100%.",
                ChatColor.of(new Color(214, 126, 61)) + "Flaming Sigil " + ChatColor.of(new Color(230,230,230)) + "now also grants",
                ChatColor.of(new Color(230,230,230)) + "attack boost to allies",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(214, 126, 61)) + "Death Gaze",
                "",
                ChatColor.of(new Color(230,230,230)) + "Hook your target, pulling",
                ChatColor.of(new Color(230,230,230)) + "them towards you and stunning",
                ChatColor.of(new Color(230,230,230)) + "them. If they are unable to be",
                ChatColor.of(new Color(230,230,230)) + "pulled, go to their position",
                ChatColor.of(new Color(230,230,230)) + "instead");
    }

    private ItemStack getGladiatorItem(){
        return getItem(Material.YELLOW_DYE, 0,
                ChatColor.of(new Color(214, 126, 102)) + "Gladiator",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+30 Health, +1 Attack, +200 mana, +2 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Gain Crushing Resistance",
                "",
                ChatColor.of(new Color(230,230,230)) + "Taking damage reduces the",
                ChatColor.of(new Color(230,230,230)) + "cooldown of " + ChatColor.of(new Color(214, 126, 61)) + "Searing Chains.",
                ChatColor.of(new Color(214, 126, 61)) + "Flaming Sigil " + ChatColor.of(new Color(230,230,230)) + "now also grants",
                ChatColor.of(new Color(230,230,230)) + "health boost to allies",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(214, 126, 102)) + "Gladiator Heart",
                "",
                ChatColor.of(new Color(230,230,230)) + "Gain a shield to absorb your",
                ChatColor.of(new Color(230,230,230)) + "level% of your max health for",
                ChatColor.of(new Color(230,230,230)) + "5 seconds. While active",
                ChatColor.of(new Color(230,230,230)) + "increase your damage resistance");
    }

    private ItemStack getDuelistItem(){
        return getItem(Material.YELLOW_DYE, 0,
                ChatColor.of(new Color(214, 61, 207)) + "Duelist",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+15 Health, +3 Attack, +100 mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(230,230,230)) + "Crit rate increased by 10%",
                "",
                ChatColor.of(new Color(230,230,230)) + "Increase your maximum combo",
                ChatColor.of(new Color(230,230,230)) + "points by 1." ,
                ChatColor.of(new Color(214, 61, 207)) + "Blade Tempest " + ChatColor.of(new Color(230,230,230)) + "increases crit",
                ChatColor.of(new Color(230,230,230)) + "rate by 10% for 5 seconds.",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(214, 61, 207)) + "Duelist's Frenzy",
                "",
                ChatColor.of(new Color(230,230,230)) + "Consume your combo points",
                ChatColor.of(new Color(230,230,230)) + "to deal a massive blow.",
                ChatColor.of(new Color(230,230,230)) + "For the next 15 seconds,",
                ChatColor.of(new Color(230,230,230)) + "every basic attack grants",
                ChatColor.of(new Color(230,230,230)) + "a combo point.",
                ChatColor.of(new Color(230,230,230)) + "Requires 6 Combo Points");
    }

    private ItemStack getAlchemistItem(){
        return getItem(Material.YELLOW_DYE, 0,
                ChatColor.of(new Color(248, 61, 119)) + "Alchemist",
                "",
                ChatColor.of(new Color(0,102,0)) + "Each level",
                ChatColor.of(new Color(230,230,230)) + "+30 Health, +2 Attack, +100 mana, +1 Both Defense",
                "",
                ChatColor.of(new Color(214, 61, 207)) + "Laceration " + ChatColor.of(new Color(230,230,230)) + "now consumes",
                ChatColor.of(new Color(230,230,230)) + "combo points to deal increased",
                ChatColor.of(new Color(230,230,230)) + "bleeding damage.",
                "",
                ChatColor.of(new Color(0,102,0)) + "Get Skill",
                ChatColor.of(new Color(248, 61, 119)) + "Wicked Concoction",
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
