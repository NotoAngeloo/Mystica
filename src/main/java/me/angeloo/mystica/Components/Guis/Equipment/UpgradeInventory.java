package me.angeloo.mystica.Components.Guis.Equipment;

import com.google.gson.Gson;
import me.angeloo.mystica.Components.Guis.Storage.MysticaBag;
import me.angeloo.mystica.Components.Guis.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;


public class UpgradeInventory implements Listener {

    private final ProfileManager profileManager;
    private final EquipmentUpgradeManager manager;
    private final CustomInventoryManager inventoryManager;


    public UpgradeInventory (Mystica main, EquipmentUpgradeManager manager){
        profileManager = main.getProfileManager();
        this.manager = manager;
        inventoryManager = main.getInventoryManager();
    }

    public void openUpgradeInventory(Player player){


        //+8 space at the end
        String title = ChatColor.WHITE + "\uF807" + "\uE0B7" + "\uF828";


        title = inventoryManager.addBagPng(title);

        Inventory inv = Bukkit.createInventory(null, 9*6, title);


        player.openInventory(inv);

        profileManager.getAnyProfile(player).getMysticaBagCollection().getBag(inventoryManager.getBagIndex(player)).displayBagItems(player);

    }


    private int getRequired(MysticaEquipment equipment, int newLevel){

        int difference = newLevel - equipment.getLevel();


        return equipment.getTier() * (difference + 1);
    }


    @EventHandler
    public void upgradeClicks(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE0B7")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        int slot = event.getSlot();

        MysticaBagCollection collection = profileManager.getAnyProfile(player).getMysticaBagCollection();
        MysticaBag currentBag = collection.getBag(inventoryManager.getBagIndex(player));

        NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
        Gson gson = new Gson();

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

            //is equipment item

            if(!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING) ){
                return;
            }

            String json = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

            MysticaEquipment equipment = gson.fromJson(json, MysticaEquipment.class);
            int itemLevel = equipment.getLevel();


            //this title has this unicode, it means that player is now selecting material to consume
            if(event.getView().getTitle().contains("\uE054")){

                topInv.setItem(19, item);

                if(topInv.getItem(12) == null){
                    return;
                }

                ItemStack upgrading = topInv.getItem(12);
                assert upgrading != null;
                ItemMeta upgradeMeta = upgrading.getItemMeta();
                assert upgradeMeta != null;
                if(!upgradeMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING) ){
                    return;
                }

                String upgradeJson = upgradeMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                MysticaEquipment upgradeEquipment = gson.fromJson(upgradeJson, MysticaEquipment.class);

                int cost = getRequired(upgradeEquipment, itemLevel);

                topInv.setItem(23, new SoulStone(cost).build());

                return;
            }

            topInv.setItem(12, item);
            topInv.setItem(19, null);


            List<MysticaEquipment> allEquipment = new ArrayList<>(collection.getBagEquipment());
            List<MysticaEquipment> higherEquipment = new ArrayList<>();

            for(MysticaEquipment mysticaEquipment : allEquipment){
                int level = mysticaEquipment.getLevel();
                if(level > itemLevel){
                    higherEquipment.add(mysticaEquipment);
                }
            }

            higherEquipment.sort(Comparator.comparingInt(MysticaEquipment::getLevel).reversed());

            bottomInv.clear();

            bottomInv.setItem(9, new SoulStone(collection.getSoulStoneAmount()).build());

            for(int i = 0; i<25;i++){


                if(higherEquipment.size()<=i){
                    break;
                }

                bottomInv.setItem(i+10, higherEquipment.get(i).build());

            }

            //title as made above, -256, + 78, valid materials png

            event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0B7" + "\uF828" + "\uF80D" + "\uF82B\uF828\uF826" + "\uE054");

        }

        if(event.getClickedInventory() == topInv){

            //other slots
            Set<Integer> identifySlots = new HashSet<>();
            identifySlots.add(45);
            identifySlots.add(46);
            identifySlots.add(47);

            if(identifySlots.contains(slot)){
                manager.getIdentifyInventory().openIdentifyInventory(player);
                return;
            }

            Set<Integer> reforgeSlots = new HashSet<>();
            reforgeSlots.add(48);
            reforgeSlots.add(49);
            reforgeSlots.add(50);

            if(reforgeSlots.contains(slot)){
                manager.getReforgeInventory().openReforgeInventory(player);
                return;
            }

            Set<Integer> refineSlots = new HashSet<>();
            refineSlots.add(51);
            refineSlots.add(52);
            refineSlots.add(53);

            if(refineSlots.contains(slot)){
                manager.getRefineInventory().openRefineInventory(player);
                return;
            }

            if(topInv.getItem(12) == null){
                return;
            }

            if(slot == 12){
                openUpgradeInventory(player);
                return;
            }

            if(topInv.getItem(23) == null){
                return;
            }

            if(topInv.getItem(19) == null){
                return;
            }

            Set<Integer> upgradeSlots = new HashSet<>();
            upgradeSlots.add(42);
            upgradeSlots.add(43);
            upgradeSlots.add(44);

            if(upgradeSlots.contains(slot)){

                ItemStack upgrading = topInv.getItem(12);
                assert upgrading != null;
                ItemMeta upgradeMeta = upgrading.getItemMeta();
                assert upgradeMeta != null;
                if(!upgradeMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)){
                    return;
                }

                //take out material from bag too
                ItemStack material = topInv.getItem(19);
                assert material != null;
                ItemMeta materialMeta = material.getItemMeta();
                assert materialMeta != null;
                if(!materialMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)){
                    return;
                }

                ItemStack costItem = topInv.getItem(23);
                assert costItem != null;

                String upgradeJson = upgradeMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                String materialJson = materialMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                MysticaEquipment upgradeEquipment = gson.fromJson(upgradeJson, MysticaEquipment.class);
                MysticaEquipment materialEquipment = gson.fromJson(materialJson, MysticaEquipment.class);

                int amount = profileManager.getAnyProfile(player).getMysticaBagCollection().getSoulStoneAmount();
                int cost = costItem.getAmount();
                int newLevel = materialEquipment.getLevel();

                if(amount < cost){
                    return;
                }

                //take items out of their bag
                collection.removeItemsFromMultipleBags(new SoulStone(cost));
                collection.removeItemsFromMultipleBags(materialEquipment);
                collection.removeItemsFromMultipleBags(upgradeEquipment);

                upgradeEquipment.setLevel(newLevel);
                collection.addToFirstBag(upgradeEquipment);

                openUpgradeInventory(player);
                return;
            }



        }

    }


}
