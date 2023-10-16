package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.ProfileComponents.PlayerBag;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.CustomItemConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BagInventory {

    private final ProfileManager profileManager;
    private final CustomItemConverter customItemConverter;

    public BagInventory(Mystica main){
        profileManager = main.getProfileManager();
        customItemConverter = new CustomItemConverter();
    }

    public Inventory openBagInventory(Player player, Integer index){

        PlayerBag playerBag = profileManager.getAnyProfile(player).getPlayerBag();

        player.sendMessage("page: " + (index + 1));

        Inventory inv = Bukkit.createInventory(player, 9 * 6, player.getName() + "'s Bag");

        //scroller
        inv.setItem(8, getItem(new ItemStack(Material.ARROW), "Scroll Up"));
        inv.setItem(53, getItem(new ItemStack(Material.ARROW), "Scroll Down"));


        ArrayList<ItemStack> items = playerBag.getItems();

        int numItems = items.size();
        int numSubLists = (numItems + 51) / 52;

        if (index >= 0 && index < numSubLists) {
            int startIndex = index * 52;
            int endIndex = Math.min(startIndex + 52, numItems);
            List<ItemStack> subItemList = items.subList(startIndex, endIndex);
            inv.addItem(subItemList.toArray(new ItemStack[0]));
        }

        return inv;

    }

    public void addItemsToPlayerBagByPickup(Player player, List<ItemStack> itemsAdded){

        if(itemsAdded == null){
            return;
        }

        //Bukkit.getLogger().info("items added " + itemsAdded);

        ArrayList<ItemStack> items = profileManager.getAnyProfile(player).getPlayerBag().getItems();

        ArrayList<ItemStack> itemsMinusTemp = new ArrayList<>();
        for(ItemStack item : items){
            if(item.getType() == Material.AIR){
                continue;
            }
            itemsMinusTemp.add(item);
        }
        setItemsInProfile(player, itemsMinusTemp);

        ItemStack currentItem = null;
        int amount = 1;
        int i = 0;
        for(ItemStack item : itemsAdded){

            //Bukkit.getLogger().info("This item is " + item);

            i++;

            if(item.isSimilar(currentItem)){
                amount ++;
            }
            else{
                amount = 1;
            }

            //Bukkit.getLogger().info("amount" + amount);

            if(currentItem == null){
                currentItem = item;
            }

            boolean full = false;

            int bagUnlocks = profileManager.getAnyProfile(player).getPlayerBag().getNumUnlocks();

            int maxSize = 52 * (1 + bagUnlocks);
            int size = itemsMinusTemp.size();

            if(size >= maxSize){
                full = true;
            }

            if(full){
                player.sendMessage("your bag is full");
                break;
            }

            //Bukkit.getLogger().info("i is " + i +" size is " + itemsAdded.size());


            if((!item.isSimilar(currentItem)) || i==(itemsAdded.size()-1)){

                currentItem = item;

                currentItem.setAmount(amount);


                if(item.hasItemMeta()){
                    player.sendMessage(amount + " " + currentItem.getItemMeta().getDisplayName() + " added to your bag");
                }
                else{
                    player.sendMessage(amount + " " + currentItem.getType() + " added to your bag");
                }

                itemsMinusTemp.add(customItemConverter.convert(currentItem, currentItem.getAmount()));

            }

        }

        //merge them here

        for (int k = 0; k < itemsMinusTemp.size(); k++) {
            ItemStack thisItem = itemsMinusTemp.get(k);
            if (thisItem == null || thisItem.getType() == Material.AIR) {
                continue;
            }
            for (int j = k + 1; j < itemsMinusTemp.size(); j++) {
                ItemStack comparedItem = itemsMinusTemp.get(j);
                if (comparedItem != null) {
                    if (thisItem.isSimilar(comparedItem)) {
                        int mergedAmount = thisItem.getAmount() + comparedItem.getAmount();
                        thisItem.setAmount(mergedAmount);
                        comparedItem.setAmount(0);
                    }
                }
            }
        }

// Remove items with amount 0
        itemsMinusTemp.removeIf(item -> item == null || item.getAmount() == 0);

        //Bukkit.getLogger().info(String.valueOf(itemsMinusTemp));

        setItemsInProfile(player, itemsMinusTemp);
    }



    public void addItemsToPlayerBagByInventoryClose(Player player, ArrayList<ItemStack> items, Integer index){

        ArrayList<ItemStack> oldItems = profileManager.getAnyProfile(player).getPlayerBag().getItems();

        //old was 48, new 52
        int sublistStart = index * 52;
        int sublistEnd = Math.min((index + 1) * 52, oldItems.size());

        oldItems.subList(sublistStart, sublistEnd).clear();
        oldItems.addAll(sublistStart, items);

        setItemsInProfile(player, oldItems);
    }

    private void setItemsInProfile(Player player, ArrayList<ItemStack> items){

        profileManager.getAnyProfile(player).getPlayerBag().setItems(items);
    }

    private ItemStack getItem(ItemStack item, String name, String... lore){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));

        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

}
