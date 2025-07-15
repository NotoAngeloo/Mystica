package me.angeloo.mystica.Components.Inventories;

import com.google.gson.Gson;
import me.angeloo.mystica.Components.Inventories.Storage.MysticaBag;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
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

        MysticaBag currentBag = profileManager.getAnyProfile(player).getMysticaBagCollection().getBag(inventoryManager.getBagIndex(player));

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

            return;
        }

    }



    private int getRequired(MysticaEquipment equipment){

        int level = equipment.getLevel();
        int tier = equipment.getTier();

        return level * ((tier * 3) - 2);
    }





}
