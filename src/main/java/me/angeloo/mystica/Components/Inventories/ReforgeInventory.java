package me.angeloo.mystica.Components.Inventories;

import com.google.gson.Gson;
import me.angeloo.mystica.Components.Inventories.Storage.MysticaBag;
import me.angeloo.mystica.Components.Inventories.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Components.Items.UnidentifiedItem;
import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
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

import java.util.ArrayList;
import java.util.List;

import static me.angeloo.mystica.Mystica.uncommonColor;

public class ReforgeInventory implements Listener {

    private final ProfileManager profileManager;
    private final CustomInventoryManager inventoryManager;


    public ReforgeInventory(Mystica main){
        profileManager = main.getProfileManager();
        inventoryManager = main.getInventoryManager();
    }

    public void openReforgeInventory(Player player){

        String title = ChatColor.WHITE + "\uF807" + "\uE0B1" + "\uF828";


        title = inventoryManager.addBagPng(title);

        Inventory inv = Bukkit.createInventory(null, 9*6, title);


        player.openInventory(inv);

        profileManager.getAnyProfile(player).getMysticaBagCollection().getBag(inventoryManager.getBagIndex(player)).displayBagItems(player);
    }



    private int getRequired(MysticaEquipment equipment){

        return equipment.getTier() * 2;
    }

    @EventHandler
    public void reforgeClick(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE0B1")){
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
            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
            if(!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING) ){
                return;
            }

            String json = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

            Gson gson = new Gson();
            MysticaEquipment equipment = gson.fromJson(json, MysticaEquipment.class);

            topInv.setItem(24, null);

            topInv.setItem(20, item);

            int cost = getRequired(equipment);

            SoulStone soulStone = new SoulStone(cost);

            topInv.setItem(31, soulStone.build());

            return;

        }

        if(event.getClickedInventory() == topInv){

            List<Integer> reforgeSlots = new ArrayList<>();
            reforgeSlots.add(53);
            reforgeSlots.add(52);
            reforgeSlots.add(51);


            if(reforgeSlots.contains(slot)){

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
                NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
                if(!item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING) ){
                    return;
                }

                String json = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

                Gson gson = new Gson();
                MysticaEquipment equipment = gson.fromJson(json, MysticaEquipment.class);

                int required = getRequired(equipment);

                int amount = profileManager.getAnyProfile(player).getMysticaBagCollection().getSoulStoneAmount();

                if(required > amount){

                    //player doesn't have enough

                    return;
                }

                MysticaEquipment reforged = new MysticaEquipment(
                        equipment.getEquipmentSlot(),
                        equipment.getPlayerClass(),
                        equipment.getLevel(),
                        equipment.getTier(),
                        equipment.getSkillOne(),
                        equipment.getSkillTwo()

                );

                topInv.setItem(24, reforged.build());

                //subtract items from bag
                collection.removeItemsFromMultipleBags(new SoulStone(required));

                profileManager.getAnyProfile(player).getMysticaBagCollection().getBag(inventoryManager.getBagIndex(player)).displayBagItems(player);
                return;
            }

            if(slot == 24){

                if(topInv.getItem(24) == null){
                    return;
                }

                ItemStack newItem = topInv.getItem(24);

                assert newItem != null;
                ItemMeta meta = newItem.getItemMeta();

                assert meta != null;
                if(!meta.hasDisplayName()){
                    return;
                }

                //is unidentified item
                NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
                if(!newItem.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING) ){
                    return;
                }

                String json = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

                Gson gson = new Gson();
                MysticaEquipment equipment = gson.fromJson(json, MysticaEquipment.class);

                //remove cost
                if(topInv.getItem(20) == null){
                    return;
                }

                //of which it SHOULDN'T unless something went wrong
                ItemStack oldItem = topInv.getItem(20);
                assert oldItem != null;
                ItemMeta oldMeta = oldItem.getItemMeta();
                assert oldMeta != null;
                if(!oldMeta.hasDisplayName()){
                    return;
                }

                NamespacedKey oldkey = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
                if(!oldItem.getItemMeta().getPersistentDataContainer().has(oldkey, PersistentDataType.STRING) ){
                    return;
                }
                String json2 = oldMeta.getPersistentDataContainer().get(oldkey, PersistentDataType.STRING);

                Gson gson2 = new Gson();
                MysticaEquipment oldEquipment = gson2.fromJson(json2, MysticaEquipment.class);

                collection.removeItemsFromMultipleBags(oldEquipment);

                currentBag.addItem(equipment);
                openReforgeInventory(player);
            }

        }

    }



}
