package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.PlayerClass;
import me.angeloo.mystica.Utility.SubClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static me.angeloo.mystica.Mystica.*;

public class SpecInventory implements Listener {

    private final ProfileManager profileManager;
    private final ItemManager itemManager;
    private final AbilityInventory abilityInventory;

    public SpecInventory(Mystica main, AbilityInventory abilityInventory){
        profileManager = main.getProfileManager();
        itemManager = main.getItemManager();
        this.abilityInventory = abilityInventory;
    }

    public Inventory openSpecInventory(Player player){
        Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF807" + "\uE065");

        Profile playerProfile = profileManager.getAnyProfile(player);

        PlayerClass playerClass = playerProfile.getPlayerClass();
        SubClass subClass = playerProfile.getPlayerSubclass();

        //inv.setItem(27, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        //inv.setItem(5, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(3, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        //inv.setItem(20, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(22, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(24, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));



        inv.setItem(40, getSubclassItem(subClass));

        switch (playerClass){
            case Elementalist:{
                inv.setItem(9, getPyromancerItem());
                //inv.setItem(13, getCryomancerItem());
                inv.setItem(18, getConjurerItem());
                break;
            }
            case Ranger:{
                inv.setItem(9, getScoutItem());
                inv.setItem(18, getTamerItem());
                break;
            }
            case Mystic:{
                inv.setItem(9, getShepardItem());
                //chaos is a secret subclass
                if(profileManager.getAnyProfile(player).getMilestones().getMilestone("chaos")){
                    inv.setItem(27, getChaosItem());
                }

                inv.setItem(18, getArcaneItem());
                break;
            }
            case Shadow_Knight:{
                inv.setItem(9, getBloodItem());
                inv.setItem(18, getDoomItem());
                break;
            }
            case Paladin:{
                inv.setItem(9, getTemplarItem());
                //secret
                if(profileManager.getAnyProfile(player).getMilestones().getMilestone("divine")){
                    inv.setItem(27, getDivineItem());
                }

                inv.setItem(18, getDawnItem());
                break;
            }
            case Warrior:{
                inv.setItem(9, getGladiatorItem());
                inv.setItem(18, getExecutionerItem());
                break;
            }
            case Assassin:{
                inv.setItem(9, getDuelistItem());
                inv.setItem(18, getAlchemistItem());
                break;
            }
        }

        return inv;
    }

    @EventHandler
    public void specClicks(InventoryClickEvent event){
        if(event.getView().getTitle().contains(ChatColor.WHITE + "\uF807" + "\uE065")){
            event.setCancelled(true);

            Inventory topInv = event.getView().getTopInventory();

            if(event.getClickedInventory() != topInv){
                return;
            }

            Player player = (Player) event.getWhoClicked();

            int slot = event.getSlot();
            ItemStack item = event.getCurrentItem();

            List<Integer> specSlots = new ArrayList<>();
            specSlots.add(9);
            specSlots.add(18);
            specSlots.add(27);

            if(specSlots.contains(slot)){
                if(item==null){
                    return;
                }

                if(!item.hasItemMeta()){
                    return;
                }

                ItemMeta meta = item.getItemMeta();

                assert meta != null;
                if(!meta.hasDisplayName()){
                    return;
                }

                //TODO: change this to account for enum instead of string

                /*String name = meta.getDisplayName();

                name = name.replaceAll("§.", "");

                profileManager.getAnyProfile(player).setPlayerSubclass(name);
                player.openInventory(openSpecInventory(player));
                profileManager.getAnyProfile(player).getStats().setLevelStats(profileManager.getAnyProfile(player).getStats().getLevel(),
                        profileManager.getAnyProfile(player).getPlayerClass(), name);*/

                Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "resource", true));
                return;
            }

            List<Integer> skillSlots = new ArrayList<>();
            for(int i=46;i<=48;i++){
                skillSlots.add(i);
            }

            if(skillSlots.contains(slot)){
                player.openInventory(abilityInventory.openAbilityInventory(player, -1));
            }
        }
    }


    private ItemStack getSubclassItem(SubClass subclass){

        switch (subclass){
            case Pyromancer:
                return getPyromancerItem();
            case Conjurer:
                return getConjurerItem();
            /*case "cryomancer":
                return getCryomancerItem();*/
            case Scout:
                return getScoutItem();
            case Tamer:
                return getTamerItem();
            case Chaos:
                return getChaosItem();
            case Arcane:
                return getArcaneItem();
            case Shepard:
                return getShepardItem();
            case Doom:
                return getDoomItem();
            case Blood:
                return getBloodItem();
            case Templar:
                return getTemplarItem();
            case Divine:
                return getDivineItem();
            case Dawn:
                return getDawnItem();
            case Gladiator:
                return getGladiatorItem();
            case Executioner:
                return getExecutionerItem();
            case Duelist:
                return getDuelistItem();
            case Alchemist:
                return getAlchemistItem();
        }
        return new ItemStack(Material.AIR);
    }

    private ItemStack getPyromancerItem(){
        return itemManager.getItem(Material.CYAN_DYE, 9,
                ChatColor.of(elementalistColor) + "Pyromancer",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+15 Health, +3 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Crit rate increased by 10%",
                "",
                ChatColor.of(Color.WHITE) + "Every 4 hit fire skills cast refresh",
                ChatColor.of(Color.WHITE) + "your " + ChatColor.of(elementalistColor) + "Fiery Wing",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(elementalistColor) + "Fiery Wing",
                "",
                ChatColor.of(Color.WHITE) + "Summon an elemental that charges",
                ChatColor.of(Color.WHITE) + "towards an enemy, dealing massive",
                ChatColor.of(Color.WHITE) + "damage on arrival");
    }

    private ItemStack getConjurerItem(){
        return itemManager.getItem(Material.CYAN_DYE, 10,
                ChatColor.of(elementalistColor) + "Conjurer",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+30 Health, +2 Attack, +1 Both Defense",
                "",
                ChatColor.of(elementalistColor) + "Ice Bolt, Descending Inferno, " +
                        ChatColor.of(Color.WHITE) + "and " + ChatColor.of(elementalistColor) + "Elemental Matrix",
                ChatColor.of(Color.WHITE) + "all do extra damage based on current health pool",
                ChatColor.of(elementalistColor) + "Elemental Matrix " +
                        ChatColor.of(Color.WHITE) + "restores health to the caster",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(elementalistColor) + "Conjuring Force",
                "",
                ChatColor.of(Color.WHITE) + "Summon a force shield centered around",
                ChatColor.of(Color.WHITE) + "you, enhancing damage and range of all",
                ChatColor.of(Color.WHITE) + "allies within");
    }


    private ItemStack getScoutItem(){
        return itemManager.getItem(Material.LIME_DYE, 9,
                ChatColor.of(rangerColor) + "Scout",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+15 Health, +3 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Crit rate increased by 10%",
                "",
                ChatColor.of(Color.WHITE) + "Increases crit of " + ChatColor.of(rangerColor) + "Razor Wind " +
                        ChatColor.of(Color.WHITE) + "by 15%",
                ChatColor.of(Color.WHITE) + "Applies haste level 1 after a skill",
                ChatColor.of(Color.WHITE) + "inflicts a critical hit",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(rangerColor) + "Star Volley",
                "",
                ChatColor.of(Color.WHITE) + "Summon an arrow from the stars",
                ChatColor.of(Color.WHITE) + "Landing a crit with any skill",
                ChatColor.of(Color.WHITE) + "decreases this skills cooldown");
    }

    private ItemStack getTamerItem(){
        return itemManager.getItem(Material.LIME_DYE, 10,
                ChatColor.of(rangerColor) + "Animal Tamer",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+30 Health, +2 Attack, +1 Both Defense",
                "",
                ChatColor.of(rangerColor) + "Shadow Crows " + ChatColor.of(Color.WHITE) + "increases damage taken",
                ChatColor.of(Color.WHITE) + "by the target by 10%",
                ChatColor.of(Color.WHITE) + "Increases crit of " + ChatColor.of(rangerColor) + "Shadow Crows " +
                        ChatColor.of(Color.WHITE) + "by 15%",
                ChatColor.of(rangerColor) + "Wild Spirit " + ChatColor.of(Color.WHITE) + "now heals nearby",
                ChatColor.of(Color.WHITE) + "players",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(rangerColor) + "Wild Roar",
                "",
                ChatColor.of(Color.WHITE) + "Inspire 5 member of your team",
                ChatColor.of(Color.WHITE) + "to deal increased damage");
    }

    private ItemStack getChaosItem(){
        return itemManager.getItem(Material.ENDER_EYE, 0,
                ChatColor.of(mysticColor) + "Chaos",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+15 Health, +3 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Crit rate increased by 10%",
                "",
                "Your abilities are replaced with",
                "new ones",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(mysticColor) + "Evil Spirit",
                "",
                ChatColor.of(Color.WHITE) + "Transform into the embodiment",
                ChatColor.of(Color.WHITE) + "of chaos, with the one goal",
                ChatColor.of(Color.WHITE) + "of destroying your enemies.",
                ChatColor.of(Color.WHITE) + "Your skills build up chaos ",
                ChatColor.of(Color.WHITE) + "shards. When you have six",
                ChatColor.of(Color.WHITE) + "this skill is able to be cast");

    }

    private ItemStack getArcaneItem(){
        return itemManager.getItem(Material.PURPLE_DYE, 9,
                ChatColor.of(mysticColor) + "Arcane Master",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+15 Health, +3 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Crit rate increased by 10%",
                "",
                ChatColor.of(Color.WHITE) + "Increases " + ChatColor.of(mysticColor) + "Dreadfall " +
                        ChatColor.of(Color.WHITE) + "damage by 50%.",
                ChatColor.of(Color.WHITE) + "Landing a crit additionally deals",
                ChatColor.of(Color.WHITE) + "15% of your Attack.",
                ChatColor.of(Color.WHITE) + "Increases " + ChatColor.of(mysticColor) + "Purifying Blast " +
                        ChatColor.of(Color.WHITE) + "damage by 150%.",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(mysticColor) + "Arcane Missiles",
                "",
                ChatColor.of(Color.WHITE) + "Rapidly fire multiple projectiles",
                ChatColor.of(Color.WHITE) + "of pure arcane energy that bombard",
                ChatColor.of(Color.WHITE) + "your enemy");
    }

    private ItemStack getShepardItem(){
        return itemManager.getItem(Material.PURPLE_DYE, 10,
                ChatColor.of(mysticColor) + "Shepard",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+30 Health, +2 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Increases healing by 20%.",
                "",
                ChatColor.of(mysticColor) + "Arcane Shield " +
                        ChatColor.of(Color.WHITE) + "restores the target's health",
                ChatColor.of(Color.WHITE) + "by 30% every 20 seconds.",
                ChatColor.of(mysticColor) + "Aurora " + ChatColor.of(Color.WHITE) + "area restores health to",
                ChatColor.of(Color.WHITE) + "all allies within",
                ChatColor.of(mysticColor) + "Light Sigil " +
                        ChatColor.of(Color.WHITE) + "restores allies health",
                ChatColor.of(Color.WHITE) + "instead",
                ChatColor.of(Color.WHITE) + "Increases " + ChatColor.of(mysticColor) + "Purifying Blast " +
                        ChatColor.of(Color.WHITE) + "healing by 50%.",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(mysticColor) + "Enlightenment",
                "",
                ChatColor.of(Color.WHITE) + "Your basic attacks and " + ChatColor.of(mysticColor) + "Purifying Blast",
                ChatColor.of(Color.WHITE) + "Mark your targets with " + ChatColor.of(mysticColor) + "Consolation.",
                "",
                ChatColor.of(Color.WHITE) + "Heal all marked targets",
                ChatColor.of(Color.WHITE) + "and give them a 40% damage",
                ChatColor.of(Color.WHITE) + "reduction buff for 5 seconds");
    }

    private ItemStack getBloodItem(){
        return itemManager.getItem(Material.RED_DYE, 9,
                ChatColor.of(shadowKnightColor) + "Blood",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+30 Health, +1 Attack, +2 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Gain Crushing Resistance",
                "",
                ChatColor.of(Color.WHITE) + "Standing in " +
                        ChatColor.of(shadowKnightColor) + "Burial Ground " +
                ChatColor.of(Color.WHITE) + "area",
                ChatColor.of(Color.WHITE) + "gives you 20% damage reduction.",
                ChatColor.of(shadowKnightColor) + "Bloodsucker " +
                        ChatColor.of(Color.WHITE) + "restores an additional",
                ChatColor.of(Color.WHITE) + "10% of your maximum health",
                ChatColor.of(shadowKnightColor) + "Shadow Grip " +
                        ChatColor.of(Color.WHITE) + "now taunts.",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(shadowKnightColor) + "Blood Shield",
                "",
                ChatColor.of(Color.WHITE) + "Instantly heal 50% of your",
                ChatColor.of(Color.WHITE) + "missing health. Shield yourself",
                ChatColor.of(Color.WHITE) + "for an equivalent of your current",
                ChatColor.of(Color.WHITE) + "health for 10 seconds. While active,",
                ChatColor.of(Color.WHITE) + "casting " + ChatColor.of(shadowKnightColor) + "Bloodsucker " +
                        ChatColor.of(Color.WHITE) + "increases the",
                ChatColor.of(Color.WHITE) + "duration by 3 seconds");
    }

    private ItemStack getDoomItem(){
        return itemManager.getItem(Material.RED_DYE, 10,
                ChatColor.of(shadowKnightColor) + "Doom",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+20 Health, +3 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Crit rate increased by 10%",
                "",
                ChatColor.of(Color.WHITE) + "Landing " + ChatColor.of(shadowKnightColor) + "Spiritual Attack " +
                        ChatColor.of(Color.WHITE) + "on an enemy",
                ChatColor.of(Color.WHITE) + "gives you a Soul Mark.",
                ChatColor.of(shadowKnightColor) + "Soul Reap " +
                        ChatColor.of(Color.WHITE) + "consumes Soul Marks and",
                ChatColor.of(Color.WHITE) + "Enhanced Infections to deal increased",
                ChatColor.of(Color.WHITE) + "damage.",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(shadowKnightColor) + "Annihilation",
                "",
                ChatColor.of(Color.WHITE) + "Your spiritual ally strikes",
                ChatColor.of(Color.WHITE) + "your foe with a powerful sweep,",
                ChatColor.of(Color.WHITE) + "enhancing any " +
                ChatColor.of(shadowKnightColor) + "Infections " + ChatColor.of(Color.WHITE) + "and",
                ChatColor.of(Color.WHITE) + "refreshing its duration");
    }

    private ItemStack getTemplarItem(){
        return itemManager.getItem(Material.YELLOW_DYE, 9,
                ChatColor.of(paladinColor) + "Templar",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+30 Health, +1 Attack, +2 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Gain Crushing Resistance",
                "",
                ChatColor.of(paladinColor) + "Reigning Sword" +
                        ChatColor.of(Color.WHITE) + " damage increases by 120%",
                ChatColor.of(Color.WHITE) + "and shield increased by 20%.",
                ChatColor.of(paladinColor) + "Judgement" +
                        ChatColor.of(Color.WHITE) + " will now taunt the target.",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(paladinColor) + "Shield of Sanctity",
                "",
                ChatColor.of(Color.WHITE) + "Gain a shield to absorb your",
                ChatColor.of(Color.WHITE) + "level% of your max health for",
                ChatColor.of(Color.WHITE) + "5 seconds. While active",
                ChatColor.of(Color.WHITE) + "restore your health.");
    }

    private ItemStack getDivineItem(){
        return itemManager.getItem(Material.YELLOW_DYE, 19,
                ChatColor.of(paladinColor) + "Divine",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+30 Health, +2 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Increases healing by 20%.",
                "",
                "Your abilities are replaced with",
                "new ones",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(paladinColor) + "Representative",
                "",
                ChatColor.of(Color.WHITE) + "Instantly heal nearby allies",
                ChatColor.of(Color.WHITE) + "and grant yourself haste as",
                ChatColor.of(Color.WHITE) + "well as your healing and damage",
                ChatColor.of(Color.WHITE) + "for 10 seconds. This skill is",
                ChatColor.of(Color.WHITE) + "unaffected by haste"
                );
    }

    private ItemStack getDawnItem(){
        return itemManager.getItem(Material.YELLOW_DYE, 10,
                ChatColor.of(paladinColor) + "Dawn",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+15 Health, +3 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Crit rate increased by 10%",
                "",
                ChatColor.of(paladinColor) + "Torah Sword" +
                        ChatColor.of(Color.WHITE) + " crit rate increased by 15%.",
                ChatColor.of(Color.WHITE) + "Landing a crit refreshes the",
                ChatColor.of(Color.WHITE) + "cooldown of " + ChatColor.of(paladinColor) + "Judgement.",
                ChatColor.of(Color.WHITE) + "In addition, your next " + ChatColor.of(paladinColor) + "Judgement,",
                ChatColor.of(Color.WHITE) + "Reigning Sword, or Sword of the Covenant's",
                ChatColor.of(Color.WHITE) + "Damage will be increased by 80%.",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(paladinColor) + "Well of Light",
                "",
                ChatColor.of(Color.WHITE) + "Summon a well that deals",
                ChatColor.of(Color.WHITE) + "area damage. This also",
                ChatColor.of(Color.WHITE) + "leaves behind orbs that",
                ChatColor.of(Color.WHITE) + "can be collected by you to",
                ChatColor.of(Color.WHITE) + "increase your crit rate by 10%");
    }

    private ItemStack getExecutionerItem(){
        return itemManager.getItem(Material.ORANGE_DYE, 10,
                ChatColor.of(warriorColor) + "Executioner",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+15 Health, +3 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Crit rate increased by 10%",
                ChatColor.of(Color.WHITE) + "Gain partial Crushing Resistance",
                "",
                ChatColor.of(warriorColor) + "Anvil Drop" +
                        ChatColor.of(Color.WHITE) + " crit rate increased by 15%",
                ChatColor.of(Color.WHITE) + "and damage increased by 100%.",
                ChatColor.of(warriorColor) + "Flaming Sigil " + ChatColor.of(Color.WHITE) + "now also grants",
                ChatColor.of(Color.WHITE) + "attack boost to allies",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(warriorColor) + "Death Gaze",
                "",
                ChatColor.of(Color.WHITE) + "Hook your target, pulling",
                ChatColor.of(Color.WHITE) + "them towards you and stunning",
                ChatColor.of(Color.WHITE) + "them. If they are unable to be",
                ChatColor.of(Color.WHITE) + "pulled, go to their position",
                ChatColor.of(Color.WHITE) + "instead");
    }

    private ItemStack getGladiatorItem(){
        return itemManager.getItem(Material.ORANGE_DYE, 9,
                ChatColor.of(warriorColor) + "Gladiator",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+30 Health, +1 Attack, +2 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Gain Crushing Resistance",
                "",
                ChatColor.of(Color.WHITE) + "Taking damage reduces the",
                ChatColor.of(Color.WHITE) + "cooldown of " + ChatColor.of(warriorColor) + "Searing Chains.",
                ChatColor.of(warriorColor) + "Flaming Sigil " + ChatColor.of(Color.WHITE) + "now also grants",
                ChatColor.of(Color.WHITE) + "health boost to allies",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(warriorColor) + "Gladiator Heart",
                "",
                ChatColor.of(Color.WHITE) + "Gain a shield to absorb your",
                ChatColor.of(Color.WHITE) + "level% of your max health for",
                ChatColor.of(Color.WHITE) + "5 seconds. While active",
                ChatColor.of(Color.WHITE) + "increase your damage resistance");
    }

    private ItemStack getDuelistItem(){
        return itemManager.getItem(Material.PINK_DYE, 12,
                ChatColor.of(assassinColor) + "Duelist",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+15 Health, +3 Attack, +1 Both Defense",
                "",
                ChatColor.of(Color.WHITE) + "Crit rate increased by 10%",
                "",
                ChatColor.of(Color.WHITE) + "Increase your maximum combo",
                ChatColor.of(Color.WHITE) + "points by 1." ,
                ChatColor.of(assassinColor) + "Blade Tempest " + ChatColor.of(Color.WHITE) + "increases crit",
                ChatColor.of(Color.WHITE) + "rate by 10% for 10 seconds.",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(assassinColor) + "Duelist's Frenzy",
                "",
                ChatColor.of(Color.WHITE) + "Consume your combo points",
                ChatColor.of(Color.WHITE) + "to deal a massive blow.",
                ChatColor.of(Color.WHITE) + "For the next 15 seconds,",
                ChatColor.of(Color.WHITE) + "every basic attack grants",
                ChatColor.of(Color.WHITE) + "a combo point.",
                ChatColor.of(Color.WHITE) + "Requires 6 Combo Points");
    }

    private ItemStack getAlchemistItem(){
        return itemManager.getItem(Material.PINK_DYE, 14,
                ChatColor.of(assassinColor) + "Alchemist",
                "",
                ChatColor.of(levelColor) + "Each level",
                ChatColor.of(Color.WHITE) + "+30 Health, +2 Attack, +1 Both Defense",
                "",
                ChatColor.of(assassinColor) + "Laceration " + ChatColor.of(Color.WHITE) + "now consumes",
                ChatColor.of(Color.WHITE) + "combo points to deal increased",
                ChatColor.of(Color.WHITE) + "bleeding damage.",
                "",
                ChatColor.of(levelColor) + "Get Skill",
                ChatColor.of(assassinColor) + "Wicked Concoction",
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
