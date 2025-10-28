package me.angeloo.mystica.Components.Guis.Storage;

import com.google.gson.Gson;
import io.r2dbc.spi.Parameter;
import me.angeloo.mystica.Components.Guis.CustomInventoryManager;
import me.angeloo.mystica.Components.Guis.InventoryTextGenerator;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Items.StackableItem;
import me.angeloo.mystica.Components.Items.StackableItemRegistry;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.EquipmentSlot;
import me.angeloo.mystica.Utility.GearReader;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BagEquipmentFunctions implements Listener {

    private final ProfileManager profileManager;
    private final CustomInventoryManager inventoryManager;
    private final DisplayWeapons displayWeapons;
    private final CustomInventoryManager customInventoryManager;
    private final InventoryTextGenerator textGenerator;
    private final GearReader gearReader;

    public BagEquipmentFunctions(Mystica main){
        profileManager = main.getProfileManager();
        displayWeapons = main.getDisplayWeapons();
        inventoryManager = main.getInventoryManager();
        customInventoryManager = main.getInventoryManager();
        textGenerator = customInventoryManager.getTextGenerator();
        gearReader = main.getGearReader();
    }

    public void open(Player player, ItemStack item, ItemStack[] oldContents){



        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
        assert meta != null;
        String json = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        Gson gson = new Gson();
        MysticaEquipment equipment = gson.fromJson(json, MysticaEquipment.class);

        //get slot its in
        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();
        EquipmentSlot equipmentSlot = equipment.getEquipmentSlot();

        MysticaEquipment oldEquipment = null;
        switch (equipmentSlot){
            case WEAPON -> {
                oldEquipment = playerEquipment.getWeapon();
            }
            case HEAD -> {
                oldEquipment = playerEquipment.getHelmet();
            }
            case CHEST -> {
                oldEquipment = playerEquipment.getChestPlate();
            }
            case LEGS -> {
                oldEquipment = playerEquipment.getLeggings();
            }
            case BOOTS -> {
                oldEquipment = playerEquipment.getBoots();
            }
        }

        Map<Object, Integer> deltas = new HashMap<>();
        Map<Object, Integer> oldStats = new HashMap<>(gearReader.getGearStats(oldEquipment));
        Map<Object, Integer> newStats = new HashMap<>(gearReader.getGearStats(equipment));

        for(Map.Entry<Object, Integer> entry : oldStats.entrySet()){

            Object mapKey = entry.getKey();
            int value = entry.getValue();
            int value2 = newStats.getOrDefault(mapKey, 0);
            int diff = value2 - value;

            if(diff == 0){
                continue;
            }

            deltas.put(mapKey, diff);
        }

        //Bukkit.getLogger().info("difference: " + deltas);


        //-8, png, +80
        String title = ChatColor.WHITE + "\uF808" + "\uE06B" + "\uF82B\uF829";

        title = customInventoryManager.addBagPng(title);

        title = title + textGenerator.getEquipmentDifferenceText(deltas);


        Inventory inv = Bukkit.createInventory(null, 9*6, title);

        switch (equipmentSlot){
            case HEAD -> {
                if(playerEquipment.getHelmet()!=null){
                    inv.setItem(0, playerEquipment.getHelmet().build());
                }
            }
            case CHEST -> {
                if(playerEquipment.getChestPlate()!=null){
                    inv.setItem(0, playerEquipment.getChestPlate().build());
                }
            }
            case LEGS -> {
                if(playerEquipment.getLeggings()!=null){
                    inv.setItem(0, playerEquipment.getLeggings().build());
                }
            }
            case BOOTS -> {
                if(playerEquipment.getBoots()!=null){
                    inv.setItem(0, playerEquipment.getBoots().build());
                }
            }
            case WEAPON -> {
                if (playerEquipment.getWeapon() != null) {
                    inv.setItem(0, playerEquipment.getWeapon().build());
                }
            }
        }

        //inv.setItem(0, new ItemStack(Material.RED_STAINED_GLASS_PANE));
        //inv.setItem(2, new ItemStack(Material.RED_STAINED_GLASS_PANE));

        inv.setItem(2, item);

        //inv.setItem(45, new ItemStack(Material.RED_STAINED_GLASS_PANE));
        //inv.setItem(46, new ItemStack(Material.RED_STAINED_GLASS_PANE));

        //inv.setItem(48, new ItemStack(Material.RED_STAINED_GLASS_PANE));
        //inv.setItem(49, new ItemStack(Material.RED_STAINED_GLASS_PANE));

        player.openInventory(inv);

        //displaying bag items changes those items uuids to no longer match :(
        //profileManager.getAnyProfile(player).getMysticaBagCollection().getBag(customInventoryManager.getBagIndex(player)).displayBagItems(player);

        //this instead clones the inventory that was passed through
        player.getInventory().setContents(oldContents);

        displayWeapons.displayArmor(player);
    }


    @EventHandler
    public void EquipmentFunctionClicks(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE06B")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();


        int slot = event.getSlot();

        MysticaBagCollection collection = profileManager.getAnyProfile(player).getMysticaBagCollection();
        MysticaBag currentBag = collection.getBag(inventoryManager.getBagIndex(player));

        if(event.getClickedInventory()==topInv){

            //implied to be equipment
            ItemStack actionItem = topInv.getItem(2);

            if(actionItem==null){
                return;
            }

            if(slot==48||slot==49){


                ItemStack invItem;
                MysticaItem bagItem = null;

                for(int i = 0; i< 26; i++){

                    invItem = bottomInv.getItem(i+9);

                    if(invItem == null){
                        continue;
                    }

                    //this is where its not detecting
                    if(invItem.equals(actionItem)){

                        bagItem = currentBag.getBag().get(i);

                        if(bagItem.questItem()){
                            player.sendMessage("cannot discard this item");
                            return;
                        }

                        break;
                    }
                }

                if(bagItem == null){
                    return;
                }

                currentBag.removeFromBag(bagItem);
                profileManager.getAnyProfile(player).getMysticaBagCollection().openMysticaBag(player, inventoryManager.getBagIndex(player));

                return;
            }

            //equip, add old item to bag
            if(slot==45||slot==46){

                ItemMeta meta = actionItem.getItemMeta();
                NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
                assert meta != null;
                String json = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                Gson gson = new Gson();
                MysticaEquipment equipment = gson.fromJson(json, MysticaEquipment.class);

                PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

                switch(equipment.getEquipmentSlot()){
                    case WEAPON -> {
                        if(playerEquipment.getWeapon() != null){
                            collection.addToFirstBag(playerEquipment.getWeapon());
                        }
                        collection.removeItemsFromMultipleBags(equipment);
                        playerEquipment.setWeapon(equipment);
                    }
                    case HEAD ->{
                        if(playerEquipment.getHelmet()!=null){
                            collection.addToFirstBag(playerEquipment.getHelmet());
                        }
                        collection.removeItemsFromMultipleBags(equipment);
                        playerEquipment.setHelmet(equipment);
                    }
                    case CHEST -> {
                        if(playerEquipment.getChestPlate()!=null){
                            collection.addToFirstBag(playerEquipment.getChestPlate());
                        }
                        collection.removeItemsFromMultipleBags(equipment);
                        playerEquipment.setChestPlate(equipment);
                    }
                    case LEGS -> {
                        if(playerEquipment.getLeggings()!=null){
                            collection.addToFirstBag(playerEquipment.getLeggings());
                        }
                        collection.removeItemsFromMultipleBags(equipment);
                        playerEquipment.setLeggings(equipment);
                    }
                    case BOOTS -> {
                        if(playerEquipment.getBoots()!=null){
                            collection.addToFirstBag(playerEquipment.getBoots());
                        }
                        collection.removeItemsFromMultipleBags(equipment);
                        playerEquipment.setBoots(equipment);
                    }
                }

                currentBag.open(player);

                return;
            }

        }

    }


}
