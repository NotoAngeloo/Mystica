package me.angeloo.mystica.Components.Guis.Storage;

import me.angeloo.mystica.Components.Guis.CustomInventoryManager;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Items.MysticaItemFormat;
import me.angeloo.mystica.Components.Items.StackableItem;
import me.angeloo.mystica.Components.Items.StackableItemRegistry;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class GenericDiscard implements Listener {

    private final ProfileManager profileManager;
    private final BagEquipmentFunctions bagEquipmentFunctions;
    private final CustomInventoryManager inventoryManager;

    public GenericDiscard(Mystica main){
        profileManager = main.getProfileManager();
        inventoryManager = main.getInventoryManager();
        bagEquipmentFunctions = main.getBagEquipmentFunctions();
    }

    public void open(Player player, ItemStack item, ItemStack[] oldContents){

        String title = ChatColor.WHITE + "\uF807" + "\uE05D" + "\uF827";
        title = inventoryManager.addBagPng(title);

        Inventory inv = Bukkit.createInventory(null, 9*6, title);

        inv.setItem(22, item);

        player.openInventory(inv);

        player.getInventory().setContents(oldContents);
    }

    @EventHandler
    public void discardClicks(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE05D")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();


        int slot = event.getSlot();

        MysticaBagCollection collection = profileManager.getAnyProfile(player).getMysticaBagCollection();
        MysticaBag currentBag = collection.getBag(inventoryManager.getBagIndex(player));

        //TODO:for bottom slot, check item click as well

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

            }

            return;
        }

        if(event.getClickedInventory() == bottomInv){

            if(slot < 9){
                return;
            }

            ItemStack item = event.getCurrentItem();

            if(item == null){
                return;
            }

            ItemStack[] oldContents = player.getInventory().getContents();


            //get the type of item it is, then switch statement for type
            MysticaItemFormat type = getItemType(item);

            //pass through inventory to not change the uuids of the items displayed
            switch (type){
                case EQUIPMENT -> {
                    bagEquipmentFunctions.open(player, item, oldContents);
                    return;
                }
                default -> {
                    open(player, item, oldContents);
                    return;
                }
            }

        }

    }

    private MysticaItemFormat getItemType(ItemStack item){

        if(!item.hasItemMeta()){
            return MysticaItemFormat.OTHER;
        }

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        NamespacedKey equipmentKey = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
        if(item.getItemMeta().getPersistentDataContainer().has(equipmentKey, PersistentDataType.STRING) ){
            return MysticaItemFormat.EQUIPMENT;
        }

        NamespacedKey unidentifiedKey = new NamespacedKey(Mystica.getPlugin(), "unidentified_data");
        if(item.getItemMeta().getPersistentDataContainer().has(unidentifiedKey, PersistentDataType.STRING) ){
            return MysticaItemFormat.UNIDENTIFIED;
        }

        return MysticaItemFormat.OTHER;
    }
}
