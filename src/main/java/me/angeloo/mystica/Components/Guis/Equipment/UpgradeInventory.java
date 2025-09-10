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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class UpgradeInventory implements Listener {

    private final ProfileManager profileManager;
    private final CustomInventoryManager inventoryManager;


    public UpgradeInventory (Mystica main){
        profileManager = main.getProfileManager();
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

                topInv.setItem(29, item);

                if(topInv.getItem(13) == null){
                    return;
                }

                ItemStack upgrading = topInv.getItem(13);
                assert upgrading != null;
                ItemMeta upgradeMeta = upgrading.getItemMeta();
                assert upgradeMeta != null;
                if(!upgradeMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING) ){
                    return;
                }

                String upgradeJson = upgradeMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                MysticaEquipment upgradeEquipment = gson.fromJson(upgradeJson, MysticaEquipment.class);

                int cost = getRequired(upgradeEquipment, itemLevel);

                topInv.setItem(33, new SoulStone(cost).build());

                return;
            }

            topInv.setItem(13, item);
            topInv.setItem(29, null);


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

            if(topInv.getItem(13) == null){
                return;
            }

            if(slot == 13){
                openUpgradeInventory(player);
                return;
            }

            if(topInv.getItem(33) == null){
                return;
            }

            if(topInv.getItem(29) == null){
                return;
            }

            List<Integer> upgradeSlots = new ArrayList<>();
            upgradeSlots.add(53);
            upgradeSlots.add(52);
            upgradeSlots.add(51);

            if(upgradeSlots.contains(slot)){

                ItemStack upgrading = topInv.getItem(13);
                assert upgrading != null;
                ItemMeta upgradeMeta = upgrading.getItemMeta();
                assert upgradeMeta != null;
                if(!upgradeMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)){
                    return;
                }

                ItemStack material = topInv.getItem(29);
                assert material != null;
                ItemMeta materialMeta = material.getItemMeta();
                assert materialMeta != null;
                if(!materialMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)){
                    return;
                }

                ItemStack costItem = topInv.getItem(33);
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
                collection.removeItemsFromMultipleBags(upgradeEquipment);

                upgradeEquipment.setLevel(newLevel);
                collection.addToFirstBag(upgradeEquipment);

                openUpgradeInventory(player);
            }

        }

    }


}
