package me.angeloo.mystica.Utility.Listeners;


import me.angeloo.mystica.Components.Inventories.*;
import me.angeloo.mystica.Components.Inventories.Storage.MysticaBag;
import me.angeloo.mystica.Components.Inventories.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.Items.StackableItem;
import me.angeloo.mystica.Components.Items.StackableItemRegistry;
import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InventoryEventListener implements Listener {

    private final Mystica main;

    private final ProfileManager profileManager;

    private final CustomInventoryManager inventoryManager;


    public InventoryEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        inventoryManager = main.getInventoryManager();
    }

    @EventHandler
    public void bagClicks(InventoryClickEvent event){

        //the bag png
        if(!event.getView().getTitle().contains("\uE05C")){
            return;
        }

        event.setCancelled(true);

        //if player clicks an item, move it to the top inventory with options to dismantle or move

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        int slot = event.getSlot();

        MysticaBagCollection collection = profileManager.getAnyProfile(player).getMysticaBagCollection();
        MysticaBag currentBag = collection.getBag(inventoryManager.getBagIndex(player));

        if(event.getClickedInventory() == bottomInv){

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

                Bukkit.getLogger().info(String.valueOf(keys));



            }
        }
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

        if(clickedInv.getType() != InventoryType.PLAYER){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        //Bukkit.getLogger().info("Slot: " + event.getSlot());

        if(event.getSlot() == 0){

            profileManager.getAnyProfile(player).getMysticaBagCollection().openMysticaBag(player, 0);

            return;
        }

    }

    @EventHandler
    public void guiClose(InventoryCloseEvent event){

        Player player = (Player) event.getPlayer();


        if(profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }


        player.getInventory().clear();
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
