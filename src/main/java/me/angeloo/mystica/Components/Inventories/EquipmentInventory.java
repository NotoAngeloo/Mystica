package me.angeloo.mystica.Components.Inventories;

import com.google.gson.Gson;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.EquipmentSlot;
import me.angeloo.mystica.Utility.GearReader;
import me.angeloo.mystica.Utility.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EquipmentInventory implements Listener {

    private final ProfileManager profileManager;
    private final CustomInventoryManager customInventoryManager;
    private final ItemManager itemManager;
    private final GearReader gearReader;
    private final DisplayWeapons displayWeapons;

    public EquipmentInventory(Mystica main) {
        profileManager = main.getProfileManager();
        customInventoryManager = main.getInventoryIndexingManager();
        gearReader = main.getGearReader();
        displayWeapons = new DisplayWeapons(main);
        itemManager = main.getItemManager();
    }


    public void openEquipmentInventory(Player player) {
        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        String title = ChatColor.WHITE + "\uF807" + "\uE066";

        title = customInventoryManager.addBagPng(title);

        Inventory inv = Bukkit.createInventory(null, 9*6, title);

        if(playerEquipment.getWeapon() != null){
            inv.setItem(18, playerEquipment.getWeapon().build());
        }

        if(playerEquipment.getHelmet() != null){
            inv.setItem(10, playerEquipment.getHelmet().build());
        }

        if(playerEquipment.getChestPlate() != null){
            inv.setItem(19, playerEquipment.getChestPlate().build());
        }

        if(playerEquipment.getLeggings() != null){
            inv.setItem(28, playerEquipment.getLeggings().build());
        }

        if(playerEquipment.getBoots() != null){
            inv.setItem(37, playerEquipment.getBoots().build());
        }
        
        //health
        int health = profileManager.getAnyProfile(player).getTotalHealth();

        List<String> healthStatStrings = statStringDivider(health);
        Collections.reverse(healthStatStrings);

        if(healthStatStrings.size() >= 3){
            inv.setItem(6, itemManager.getNumberItem(healthStatStrings.get(2), health));
        }

        if(healthStatStrings.size() >= 2){
            inv.setItem(7, itemManager.getNumberItem(healthStatStrings.get(1), health));
        }

        if(healthStatStrings.size() >= 1){
            inv.setItem(8, itemManager.getNumberItem(healthStatStrings.get(0), health));
        }



        //attack
        int attack = profileManager.getAnyProfile(player).getTotalAttack();

        List<String> attackStatStrings = statStringDivider(attack);
        Collections.reverse(attackStatStrings);

        if(attackStatStrings.size() >= 3){
            inv.setItem(15, itemManager.getNumberItem(attackStatStrings.get(2), attack));
        }

        if(attackStatStrings.size() >= 2){
            inv.setItem(16, itemManager.getNumberItem(attackStatStrings.get(1), attack));
        }

        if(attackStatStrings.size() >= 1){
            inv.setItem(17, itemManager.getNumberItem(attackStatStrings.get(0), attack));
        }

        //defense
        int defense = profileManager.getAnyProfile(player).getTotalDefense();

        List<String> defenseStatStrings = statStringDivider(defense);
        Collections.reverse(defenseStatStrings);

        if(defenseStatStrings.size() >= 3){
            inv.setItem(24, itemManager.getNumberItem(defenseStatStrings.get(2), defense));
        }

        if(defenseStatStrings.size() >= 2){
            inv.setItem(25, itemManager.getNumberItem(defenseStatStrings.get(1), defense));
        }

        if(defenseStatStrings.size() >= 1){
            inv.setItem(26, itemManager.getNumberItem(defenseStatStrings.get(0), defense));
        }


        //magic defense
        int magic = profileManager.getAnyProfile(player).getTotalMagicDefense();

        List<String> magicStatStrings = statStringDivider(magic);
        Collections.reverse(magicStatStrings);

        if(magicStatStrings.size() >= 3){
            inv.setItem(33, itemManager.getNumberItem(magicStatStrings.get(2), magic));
        }

        if(magicStatStrings.size() >= 2){
            inv.setItem(34, itemManager.getNumberItem(magicStatStrings.get(1), magic));
        }

        if(magicStatStrings.size() >= 1){
            inv.setItem(35, itemManager.getNumberItem(magicStatStrings.get(0), magic));
        }


        //crit
        int crit = profileManager.getAnyProfile(player).getTotalCrit();

        List<String> critStatStrings = statStringDivider(crit);
        Collections.reverse(critStatStrings);

        if(critStatStrings.size() >= 3){
            inv.setItem(42, itemManager.getNumberItem(critStatStrings.get(2), crit));
        }

        if(critStatStrings.size() >= 2){
            inv.setItem(43, itemManager.getNumberItem(critStatStrings.get(1), crit));
        }

        if(critStatStrings.size() >= 1){
            inv.setItem(44, itemManager.getNumberItem(critStatStrings.get(0), crit));
        }



        player.openInventory(inv);

        profileManager.getAnyProfile(player).getMysticaBagCollection().getBag(customInventoryManager.getBagIndex(player)).displayBagItems(player);

    }

    private List<String> statStringDivider(int stat){

        List<String> result = new ArrayList<>();

        String statString = String.valueOf(stat);

        int index = statString.length();

        while (index > 2){
            result.add(0, statString.substring(index - 2, index));
            index -= 2;
        }

        if(index > 0){
            result.add(0, statString.substring(0, index));
        }

        return result;
    }


    @EventHandler
    public void gearClick(InventoryClickEvent event){

        if(event.getView().getTitle().contains("\uE066")){

            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            Inventory topInv = event.getView().getTopInventory();
            Inventory bottomInv = event.getView().getBottomInventory();

            PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

            if(event.getClickedInventory() == bottomInv){

                ItemStack item = event.getCurrentItem();

                if(item == null){
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


                NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
                if(!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING) ){
                    return;
                }

                String json = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

                Gson gson = new Gson();
                MysticaEquipment mysticaEquipment = gson.fromJson(json, MysticaEquipment.class);

                //Bukkit.getLogger().info(String.valueOf(mysticaEquipment.getEquipmentSlot()));


                //check the class of the equipment
                PlayerClass playerClass = profileManager.getAnyProfile(player).getPlayerClass();
                PlayerClass equipmentClass = mysticaEquipment.getPlayerClass();

                if(playerClass != equipmentClass){
                    return;
                }

                EquipmentSlot slot = mysticaEquipment.getEquipmentSlot();


                switch (slot){
                    case WEAPON:{
                        ItemStack oldWeapon = topInv.getItem(18);

                        if(oldWeapon != null){
                            ItemMeta oldMeta = oldWeapon.getItemMeta();
                            assert oldMeta != null;
                            PersistentDataContainer persistentDataContainer = oldMeta.getPersistentDataContainer();
                            String oldJson = persistentDataContainer.get(key, PersistentDataType.STRING);
                            MysticaEquipment oldEquipment = gson.fromJson(oldJson, MysticaEquipment.class);

                            //add this to bag
                        }

                        //remove new item from bag
                        //player.getInventory().remove(item);

                        //equip the new thing
                        playerEquipment.setWeapon(mysticaEquipment);

                        gearReader.setGearStats(player);
                        openEquipmentInventory(player);
                        displayWeapons.displayArmor(player);
                        return;
                    }
                    case HEAD:{
                        ItemStack oldHelmet = topInv.getItem(10);

                        if(oldHelmet != null){
                            ItemMeta oldMeta = oldHelmet.getItemMeta();
                            assert oldMeta != null;
                            PersistentDataContainer persistentDataContainer = oldMeta.getPersistentDataContainer();
                            String oldJson = persistentDataContainer.get(key, PersistentDataType.STRING);
                            MysticaEquipment oldEquipment = gson.fromJson(oldJson, MysticaEquipment.class);

                            //add this to bag
                        }

                        //remove new item from bag
                        //player.getInventory().remove(item);

                        //equip the new thing
                        playerEquipment.setHelmet(mysticaEquipment);

                        gearReader.setGearStats(player);
                        openEquipmentInventory(player);
                        displayWeapons.displayArmor(player);
                        return;
                    }
                    case CHEST:{
                        ItemStack oldChestPlate = topInv.getItem(19);

                        if(oldChestPlate != null){
                            ItemMeta oldMeta = oldChestPlate.getItemMeta();
                            assert oldMeta != null;
                            PersistentDataContainer persistentDataContainer = oldMeta.getPersistentDataContainer();
                            String oldJson = persistentDataContainer.get(key, PersistentDataType.STRING);
                            MysticaEquipment oldEquipment = gson.fromJson(oldJson, MysticaEquipment.class);

                            //add this to bag
                        }

                        //remove new item from bag
                        //player.getInventory().remove(item);

                        //equip the new thing
                        playerEquipment.setChestPlate(mysticaEquipment);

                        gearReader.setGearStats(player);
                        openEquipmentInventory(player);
                        displayWeapons.displayArmor(player);
                        return;
                    }
                    case LEGS:{
                        ItemStack oldLeggings = topInv.getItem(28);

                        if(oldLeggings != null){
                            ItemMeta oldMeta = oldLeggings.getItemMeta();
                            assert oldMeta != null;
                            PersistentDataContainer persistentDataContainer = oldMeta.getPersistentDataContainer();
                            String oldJson = persistentDataContainer.get(key, PersistentDataType.STRING);
                            MysticaEquipment oldEquipment = gson.fromJson(oldJson, MysticaEquipment.class);

                            //add this to bag
                        }

                        //remove new item from bag
                        //player.getInventory().remove(item);

                        //equip the new thing
                        playerEquipment.setLeggings(mysticaEquipment);

                        gearReader.setGearStats(player);
                        openEquipmentInventory(player);
                        displayWeapons.displayArmor(player);
                        return;
                    }
                    case BOOTS:{
                        ItemStack oldBoots = topInv.getItem(37);

                        if(oldBoots != null){
                            ItemMeta oldMeta = oldBoots.getItemMeta();
                            assert oldMeta != null;
                            PersistentDataContainer persistentDataContainer = oldMeta.getPersistentDataContainer();
                            String oldJson = persistentDataContainer.get(key, PersistentDataType.STRING);
                            MysticaEquipment oldEquipment = gson.fromJson(oldJson, MysticaEquipment.class);

                            //add this to bag
                        }

                        //remove new item from bag
                        //player.getInventory().remove(item);

                        //equip the new thing
                        playerEquipment.setBoots(mysticaEquipment);

                        gearReader.setGearStats(player);
                        openEquipmentInventory(player);
                        displayWeapons.displayArmor(player);
                        return;
                    }
                }


            }

            if(event.getClickedInventory() == topInv){

                ItemStack item = event.getCurrentItem();

                if(item == null){
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

                switch (event.getSlot()){
                    case 18:{

                        return;
                    }
                    case 10:{

                        return;
                    }
                    case 19:{

                        return;
                    }
                    case 28:{

                        return;
                    }
                    case 37:{
                        /*player.getInventory().addItem(item);
                        topInv.setItem(37, null);
                        playerEquipment.setBoots(null);
                        gearReader.setGearStats(player);
                        openEquipmentInventory(player);
                        displayWeapons.displayArmor(player);*/
                        return;
                    }
                }

            }

        }

    }

    @EventHandler
    public void gearClose(InventoryCloseEvent event){

        if(event.getView().getTitle().contains("\uE066")){
            Player player = (Player) event.getPlayer();
            displayWeapons.displayArmor(player);

        }

    }



}

