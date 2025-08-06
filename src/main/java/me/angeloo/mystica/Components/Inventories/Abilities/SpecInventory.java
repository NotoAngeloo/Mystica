package me.angeloo.mystica.Components.Inventories.Abilities;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.MaxHealthChangeOutOfCombatEvent;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.InventoryItemGetter;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
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
    private final DisplayWeapons displayWeapons;
    private final InventoryItemGetter inventoryItemGetter;
    private final AbilityInventory abilityInventory;

    public SpecInventory(Mystica main, AbilityInventory abilityInventory){
        profileManager = main.getProfileManager();
        inventoryItemGetter = main.getItemGetter();
        displayWeapons = main.getDisplayWeapons();
        this.abilityInventory = abilityInventory;
    }

    public void openSpecInventory(Player player){
        //-46
        Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF80A\uF808\uF806"+ "\uE065");

        Profile playerProfile = profileManager.getAnyProfile(player);

        PlayerClass playerClass = playerProfile.getPlayerClass();
        SubClass subClass = playerProfile.getPlayerSubclass();

        //slots for ability upgrades, coming soon tm

        /*inv.setItem(3, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
        inv.setItem(5, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));

        inv.setItem(10, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
        inv.setItem(16, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));

        inv.setItem(22, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));

        inv.setItem(27, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
        inv.setItem(29, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));

        inv.setItem(33, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
        inv.setItem(35, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));*/


        player.openInventory(inv);
        player.getInventory().clear();
        displayWeapons.displayArmor(player);

        player.getInventory().setItem(13, getSubclassItem(subClass));

        switch (playerClass) {
            case Elementalist -> {
                player.getInventory().setItem(29, getPyromancerItem());
                //player.getInventory().setItem(22, getcrymancerItem());
                player.getInventory().setItem(33, getConjurerItem());
            }
            case Ranger -> {
                player.getInventory().setItem(29, getScoutItem());
                player.getInventory().setItem(33, getTamerItem());
            }
            case Mystic -> {
                player.getInventory().setItem(29, getArcaneItem());
                //player.getInventory().setItem(22, getchoseItem());
                player.getInventory().setItem(33, getShepardItem());
            }
            case Shadow_Knight -> {
                player.getInventory().setItem(29, getBloodItem());
                player.getInventory().setItem(33, getDoomItem());
            }
            case Paladin -> {
                player.getInventory().setItem(29, getTemplarItem());
                //player.getInventory().setItem(22, getdivineItem());
                player.getInventory().setItem(33, getDawnItem());
            }
            case Warrior -> {
                player.getInventory().setItem(29, getGladiatorItem());
                player.getInventory().setItem(33, getExecutionerItem());
            }
            case Assassin -> {
                player.getInventory().setItem(29, getDuelistItem());
                player.getInventory().setItem(33, getAlchemistItem());
            }
        }


    }

    @EventHandler
    public void specClicks(InventoryClickEvent event){
        if(event.getView().getTitle().contains("\uE065")){
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            ItemStack item = event.getCurrentItem();

            Inventory topInv = event.getView().getTopInventory();
            Inventory bottomInv = event.getView().getBottomInventory();

            if(event.getClickedInventory() == topInv){
                return;
            }


            if(event.getClickedInventory() == bottomInv){
                List<Integer> specSlots = new ArrayList<>();
                specSlots.add(29);
                specSlots.add(31);
                specSlots.add(33);


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

                    String name = meta.getDisplayName();
                    name = name.replaceAll("§.", "");

                    SubClass subClass = SubClass.valueOf(name);

                    profileManager.getAnyProfile(player).setPlayerSubclass(subClass);
                    profileManager.getAnyProfile(player).getStats().setLevelStats(
                            profileManager.getAnyProfile(player).getStats().getLevel(),
                            profileManager.getAnyProfile(player).getPlayerClass(),
                            subClass);

                    bottomInv.setItem(13, getSubclassItem(subClass));


                    Bukkit.getServer().getPluginManager().callEvent(new MaxHealthChangeOutOfCombatEvent(player));
                    Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Resource));
                    return;
                }

                if(slot == 13){

                    profileManager.getAnyProfile(player).setPlayerSubclass(SubClass.NONE);
                    profileManager.getAnyProfile(player).getStats().setLevelStats(
                            profileManager.getAnyProfile(player).getStats().getLevel(),
                            profileManager.getAnyProfile(player).getPlayerClass(),
                            SubClass.NONE);

                    bottomInv.setItem(13, null);

                    return;
                }

                List<Integer> skillSlots = new ArrayList<>();
                for(int i=6;i<=8;i++){
                    skillSlots.add(i);
                }

                if(skillSlots.contains(slot)){
                    abilityInventory.openAbilityInventory(player, -1);
                }
                return;
            }








        }
    }


    private ItemStack getSubclassItem(SubClass subclass){

        return switch (subclass) {
            case Pyromancer -> getPyromancerItem();
            case Conjurer -> getConjurerItem();
            /*case "cryomancer":
                return getCryomancerItem();*/
            case Scout -> getScoutItem();
            case Tamer -> getTamerItem();
            case Chaos -> getChaosItem();
            case Arcane -> getArcaneItem();
            case Shepard -> getShepardItem();
            case Doom -> getDoomItem();
            case Blood -> getBloodItem();
            case Templar -> getTemplarItem();
            case Divine -> getDivineItem();
            case Dawn -> getDawnItem();
            case Gladiator -> getGladiatorItem();
            case Executioner -> getExecutionerItem();
            case Duelist -> getDuelistItem();
            case Alchemist -> getAlchemistItem();
            default -> new ItemStack(Material.AIR);
        };
    }

    private ItemStack getPyromancerItem(){
        return inventoryItemGetter.getItem(Material.CYAN_DYE, 9,
                ChatColor.of(elementalistColor) + SubClass.Pyromancer.name(),
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
        return inventoryItemGetter.getItem(Material.CYAN_DYE, 10,
                ChatColor.of(elementalistColor) + SubClass.Conjurer.name(),
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
        return inventoryItemGetter.getItem(Material.LIME_DYE, 9,
                ChatColor.of(rangerColor) + SubClass.Scout.name(),
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
        return inventoryItemGetter.getItem(Material.LIME_DYE, 10,
                ChatColor.of(rangerColor) + SubClass.Tamer.name(),
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
        return inventoryItemGetter.getItem(Material.ENDER_EYE, 0,
                ChatColor.of(mysticColor) + SubClass.Chaos.name(),
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
        return inventoryItemGetter.getItem(Material.PURPLE_DYE, 9,
                ChatColor.of(mysticColor) + SubClass.Arcane.name(),
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
        return inventoryItemGetter.getItem(Material.PURPLE_DYE, 10,
                ChatColor.of(mysticColor) + SubClass.Shepard.name(),
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
        return inventoryItemGetter.getItem(Material.RED_DYE, 9,
                ChatColor.of(shadowKnightColor) + SubClass.Blood.name(),
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
        return inventoryItemGetter.getItem(Material.RED_DYE, 10,
                ChatColor.of(shadowKnightColor) + SubClass.Doom.name(),
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
        return inventoryItemGetter.getItem(Material.YELLOW_DYE, 9,
                ChatColor.of(paladinColor) + SubClass.Templar.name(),
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
        return inventoryItemGetter.getItem(Material.YELLOW_DYE, 19,
                ChatColor.of(paladinColor) + SubClass.Divine.name(),
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
        return inventoryItemGetter.getItem(Material.YELLOW_DYE, 10,
                ChatColor.of(paladinColor) + SubClass.Dawn.name(),
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
        return inventoryItemGetter.getItem(Material.ORANGE_DYE, 10,
                ChatColor.of(warriorColor) + SubClass.Executioner.name(),
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
        return inventoryItemGetter.getItem(Material.ORANGE_DYE, 9,
                ChatColor.of(warriorColor) + SubClass.Gladiator.name(),
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
        return inventoryItemGetter.getItem(Material.PINK_DYE, 12,
                ChatColor.of(assassinColor) + SubClass.Duelist.name(),
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
        return inventoryItemGetter.getItem(Material.PINK_DYE, 14,
                ChatColor.of(assassinColor) + SubClass.Alchemist.name(),
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
