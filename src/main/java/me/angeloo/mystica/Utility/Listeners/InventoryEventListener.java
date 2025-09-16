package me.angeloo.mystica.Utility.Listeners;


import me.angeloo.mystica.Components.Guis.Abilities.AbilityInventory;
import me.angeloo.mystica.Components.Guis.Party.DungeonSelect;
import me.angeloo.mystica.Components.Guis.Party.PartyInventory;
import me.angeloo.mystica.Components.Guis.Storage.MysticaBag;
import me.angeloo.mystica.Components.Guis.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Items.StackableItem;
import me.angeloo.mystica.Components.Items.StackableItemRegistry;
import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.InventoryItemGetter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InventoryEventListener implements Listener {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final InventoryItemGetter itemGetter;
    private final CustomInventoryManager inventoryManager;
    private final DungeonSelect dungeonSelect;
    private final PartyInventory partyInventory;
    private final AbilityInventory abilityInventory;
    private final DisplayWeapons displayWeapons;
    private final CooldownDisplayer cooldownDisplayer;

    public InventoryEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        itemGetter = main.getItemGetter();
        inventoryManager = main.getInventoryManager();
        dungeonSelect = main.getDungeonSelect();
        partyInventory = main.getPartyInventory();
        abilityInventory = main.getAbilityInventory();
        displayWeapons = main.getDisplayWeapons();
        cooldownDisplayer = main.getCooldownDisplayer();
    }

    @EventHandler
    public void bagClicks(InventoryClickEvent event){

        if(event.getView().getTitle().equalsIgnoreCase(org.bukkit.ChatColor.WHITE + "\uF808" + "\uE05C")
        || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF807" + "\uE05D" + "\uF827" + "\uF80D" + "\uF82B\uF828\uF826" + "\uE05C")){

            event.setCancelled(true);

            //if player clicks an item, move it to the top inventory with options to dismantle or move

            Player player = (Player) event.getWhoClicked();

            Inventory topInv = event.getView().getTopInventory();
            Inventory bottomInv = event.getView().getBottomInventory();

            int slot = event.getSlot();

            MysticaBagCollection collection = profileManager.getAnyProfile(player).getMysticaBagCollection();
            MysticaBag currentBag = collection.getBag(inventoryManager.getBagIndex(player));

            if(event.getClickedInventory() == bottomInv){

                if(slot < 9){
                    return;
                }

                ItemStack item = event.getCurrentItem();

                if(item == null){
                    return;
                }

                topInv.setItem(22, item);

                //-7, title, +7
                String newTitle = ChatColor.WHITE + "\uF807" + "\uE05D" + "\uF827";

                newTitle = inventoryManager.addBagPng(newTitle);

                event.getView().setTitle(newTitle);
                return;
            }

            if(event.getClickedInventory() == topInv){

                ItemStack actionItem = topInv.getItem(22);

                if(actionItem == null){
                    return;
                }

                ItemMeta meta = actionItem.getItemMeta();

                if(meta == null){
                    return;
                }

                if(meta.getPersistentDataContainer().isEmpty()){
                    return;
                }

                List<Integer> discardSlots = new ArrayList<>();
                discardSlots.add(53);
                discardSlots.add(52);
                discardSlots.add(51);

                if(discardSlots.contains(slot)){

                    ItemStack invItem;
                    MysticaItem bagItem = null;

                    for(int i = 0; i< 26; i++){

                        invItem = bottomInv.getItem(i+9);

                        if(invItem == null){
                            continue;
                        }

                        if(invItem.equals(actionItem)){
                            bagItem = currentBag.getBag().get(i);

                            if(bagItem.questItem()){
                                player.sendMessage("cannot discard this item");
                                return;
                            }

                            break;
                        }
                    }

                    Set<NamespacedKey> keys = meta.getPersistentDataContainer().getKeys();

                    if(keys.contains(NamespacedKey.fromString( "mystica:stackable_data"))){
                        //remove x amount from current bag
                        //use the registry

                        String name = actionItem.getItemMeta().getDisplayName();
                        name = name.replaceAll("§.", "");

                        Map<String, Object> data = new HashMap<>();
                        data.put("identifier", name);
                        data.put("amount", actionItem.getAmount());

                        StackableItem stackable = StackableItemRegistry.deserialize(data);

                        currentBag.removeAnAmountOfStackables(stackable, actionItem.getAmount());

                        profileManager.getAnyProfile(player).getMysticaBagCollection().openMysticaBag(player, inventoryManager.getBagIndex(player));
                        return;
                    }

                    if(bagItem == null){
                        return;
                    }

                    currentBag.removeFromBag(bagItem);
                    profileManager.getAnyProfile(player).getMysticaBagCollection().openMysticaBag(player, inventoryManager.getBagIndex(player));

                    //this loop finds the slot that selected item is in the mysticabag
                    /*for(int i = 0; i< 26; i++){

                        ItemStack invItem = bottomInv.getItem(i+9);

                        if(invItem == null){
                            continue;
                        }

                        if(invItem.equals(actionItem)){
                            MysticaItem bagItem = currentBag.getBag().get(i);
                            currentBag.removeFromBag(bagItem);
                            profileManager.getAnyProfile(player).getMysticaBagCollection().openMysticaBag(player, inventoryManager.getBagIndex(player));
                            break;
                        }
                    }*/
                }
            }
        }


    }

    @EventHandler
    public void invOpen(InventoryOpenEvent event){
        Player player = (Player) event.getPlayer();
        player.setItemOnCursor(null);
    }

    @EventHandler
    public void menuClick(InventoryClickEvent event){

        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }

        String title = event.getView().getTitle();

        if(!title.equalsIgnoreCase("crafting")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if(profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        int slot = event.getSlot();

        if(event.getClickedInventory().equals(event.getView().getTopInventory())){

            //escape
            if(slot==1||slot==2||slot==3||slot==4){

                World world = Bukkit.getWorld("world");

                assert world != null;
                player.teleport(world.getSpawnLocation());
                return;
            }

            //settings
            if(slot==0){
                return;
            }

            return;
        }

        if(event.getClickedInventory().equals(event.getView().getBottomInventory())){

            Set<Integer> skillSlots = new HashSet<>();
            for(int i=0;i<8;i++){
                skillSlots.add(i);
            }

            if(skillSlots.contains(slot)){
                abilityInventory.openAbilityInventory(player, -1);
                return;
            }


            //bag
            if(slot==18||slot==19||slot==27||slot==28){
                profileManager.getAnyProfile(player).getMysticaBagCollection().openMysticaBag(player, 0);
                return;
            }

            //quests
            if(slot==20||slot==21||slot==29||slot==30){

                return;
            }

            //dungeon
            if(slot==22||slot==23||slot==31||slot==32){
                dungeonSelect.openDungeonSelect(player);
                return;
            }

            //party
            if(slot==24||slot==25||slot==33||slot==34){
                partyInventory.openPartyInventory(player);
                return;
            }


            return;
        }





    }




    @EventHandler
    public void guiClose(InventoryCloseEvent event){

        Player player = (Player) event.getPlayer();


        if(profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        if(event.getInventory().getType().equals(InventoryType.CRAFTING)){
            return;
        }

        player.getInventory().clear();
        displayWeapons.displayArmor(player);

        Bukkit.getScheduler().runTaskLaterAsynchronously(main, ()->{
            InventoryView open = player.getOpenInventory();

            if(open.getTitle().equalsIgnoreCase("crafting")){
                cooldownDisplayer.initializeItems(player);
            }
        },1);


    }

    @EventHandler
    public void clickBagSlot(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE05C")){
            return;
        }

        Inventory clickedInv = event.getClickedInventory();
        Inventory botInv = event.getView().getBottomInventory();

        if(clickedInv != botInv){
            return;
        }

        List<Integer> bagSlots = new ArrayList<>();
        bagSlots.add(0);
        bagSlots.add(1);
        bagSlots.add(2);
        bagSlots.add(3);
        bagSlots.add(4);
        bagSlots.add(5);
        bagSlots.add(6);
        bagSlots.add(7);
        bagSlots.add(8);

        if(!bagSlots.contains(event.getSlot())){
            return;
        }

        event.setCancelled(true);

    }


}
