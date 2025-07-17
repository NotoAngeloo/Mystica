package me.angeloo.mystica.Components.Inventories;

import com.google.gson.Gson;
import me.angeloo.mystica.Components.Inventories.Storage.MysticaBag;
import me.angeloo.mystica.Components.Inventories.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Components.Items.UnidentifiedItem;
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
import java.util.List;

public class IdentifyInventory implements Listener {

    private final ProfileManager profileManager;
    private final CustomInventoryManager inventoryManager;


    public IdentifyInventory(Mystica main){
        profileManager = main.getProfileManager();
        inventoryManager = main.getInventoryManager();
    }

    //have the identify button show up, only when proper materials put in

    public void openIdentifyInventory(Player player){

        //+8 space at the end
        String title = ChatColor.WHITE + "\uF807" + "\uE0AE" + "\uF828";


        title = inventoryManager.addBagPng(title);

        Inventory inv = Bukkit.createInventory(null, 9*6, title);


        player.openInventory(inv);

        profileManager.getAnyProfile(player).getMysticaBagCollection().getBag(inventoryManager.getBagIndex(player)).displayBagItems(player);

    }

    @EventHandler
    public void identifyClicks(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE0AE")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        int slot = event.getSlot();

        MysticaBagCollection collection = profileManager.getAnyProfile(player).getMysticaBagCollection();
        MysticaBag currentBag = collection.getBag(inventoryManager.getBagIndex(player));

        if(event.getClickedInventory() == bottomInv){

            //check what they clicked
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

            //is unidentified item
            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "unidentified_data");
            if(!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING) ){
                return;
            }

            String json = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

            Gson gson = new Gson();
            UnidentifiedItem unidentifiedItem = gson.fromJson(json, UnidentifiedItem.class);


            topInv.setItem(20, item);

            int cost = getRequired(unidentifiedItem);

            SoulStone soulStone = new SoulStone(cost);

            topInv.setItem(24, soulStone.build());

            return;
        }

        if(event.getClickedInventory() == topInv){

            List<Integer> identifySlots = new ArrayList<>();
            identifySlots.add(53);
            identifySlots.add(52);
            identifySlots.add(51);


            if(identifySlots.contains(slot)){

                //check if has enough

                if(topInv.getItem(20) == null){
                    return;
                }

                ItemStack item = topInv.getItem(20);

                assert item != null;
                ItemMeta meta = item.getItemMeta();

                assert meta != null;
                if(!meta.hasDisplayName()){
                    return;
                }

                //is unidentified item
                NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "unidentified_data");
                if(!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING) ){
                    return;
                }

                String json = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

                Gson gson = new Gson();
                UnidentifiedItem unidentifiedItem = gson.fromJson(json, UnidentifiedItem.class);

                int required = getRequired(unidentifiedItem);

                int amount = profileManager.getAnyProfile(player).getMysticaBagCollection().getSoulStoneAmount();

                if(required > amount){

                    //player doesn't have enough

                    return;
                }

                //remove blueprint and stones from bag
                collection.removeItemsFromMultipleBags(unidentifiedItem);
                collection.removeItemsFromMultipleBags(new SoulStone(required));

                MysticaEquipment newItem = new MysticaEquipment(
                        unidentifiedItem.getEquipmentSlot(),
                        profileManager.getAnyProfile(player).getPlayerClass(),
                        unidentifiedItem.getLevel(),
                        unidentifiedItem.getTier());

                currentBag.addItem(newItem);

                openIdentifyInventory(player);
            }


        }

    }



    private int getRequired(UnidentifiedItem item){

        int tier = item.getTier();

        if(tier == 1){
            return 0;
        }

        int level = item.getLevel();

        return level * ((tier * 3) - 2);
    }





}
