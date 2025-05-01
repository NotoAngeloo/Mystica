package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class UpgradeInventory implements Listener {

    private final ItemManager itemManager;
    private final EquipmentManager equipmentManager;

    private final ItemStack exampleStone;

    public UpgradeInventory (Mystica main){
        itemManager = main.getItemManager();
        equipmentManager = main.getEquipmentManager();

        exampleStone = itemManager.getSoulStone().getSoulStone();
    }

    public Inventory openUpgradeInventory(Player player){

        Inventory inv = Bukkit.createInventory(null, 9 * 6,ChatColor.WHITE + "\uF807" + "\uE0B7");

        inv.setItem(38, exampleStone.clone());
        ItemStack stone = inv.getItem(38);
        assert stone != null;
        stone.setAmount(stoneCount(player));

        //inv.setItem(22, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(24, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        return inv;
    }

    private int stoneCount(Player player){

        int count = 0;
        for(ItemStack item : player.getInventory().getContents()){
            if(item == null){
                continue;
            }

            if(!item.hasItemMeta()){
                continue;
            }

            ItemMeta meta = item.getItemMeta();

            assert meta != null;
            if(!meta.hasDisplayName()){
                continue;
            }

            ItemStack stone = exampleStone.clone();
            assert stone.hasItemMeta();
            assert stone.getItemMeta() != null;
            assert stone.getItemMeta().hasDisplayName();
            if(meta.getDisplayName().equalsIgnoreCase(stone.getItemMeta().getDisplayName())){
                count += item.getAmount();
            }

        }
        return count;
    }

    private boolean fodderHigherLevel(ItemStack upgradeItem, ItemStack fodder){

        int level = equipmentManager.getItemLevel(upgradeItem);
        int fodderLevel = equipmentManager.getItemLevel(fodder);

        return fodderLevel > level;
    }

    private int getRequired(ItemStack equipment, ItemStack fodder){

        int level = equipmentManager.getItemLevel(fodder);
        int tier = equipmentManager.getEquipmentTier(equipment);

        return level * ((tier * 3) - 2);
    }

    @EventHandler
    public void upgradeClicks(InventoryClickEvent event){

        if(event.getView().getTitle().contains("\uE0B7")){
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            Inventory topInv = event.getView().getTopInventory();
            Inventory bottomInv = event.getView().getBottomInventory();

            int[] targetSlots = {41, 42, 43, 44, 50, 51, 52, 53};

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


                if(!itemManager.getEquipmentTypes().contains(item.getType())){
                    return;
                }

                //Bukkit.getLogger().info(name);

                topInv.setItem(20, item.clone());


                for (int i = 0; i < 8; i++) {
                    topInv.setItem(targetSlots[i], null);
                }

                List<ItemStack> potentialFodder = new ArrayList<>();

                for(ItemStack playerItem : player.getInventory().getContents()){

                    if(playerItem == null){
                        continue;
                    }

                    if(playerItem.getType() != item.getType()){
                        continue;
                    }

                    if(!playerItem.hasItemMeta()){
                        continue;
                    }

                    ItemMeta playerItemMeta = playerItem.getItemMeta();
                    assert playerItemMeta != null;

                    if(playerItemMeta == meta){
                        continue;
                    }

                    if(!fodderHigherLevel(item, playerItem)){
                        continue;
                    }

                    potentialFodder.add(playerItem);
                }

                if(!potentialFodder.isEmpty()){
                    event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0B7");

                    potentialFodder.sort(Comparator.comparingInt(equipmentManager::getItemLevel).reversed());
                    List<ItemStack> firstEight = potentialFodder.subList(0, Math.min(8, potentialFodder.size()));

                    for (int i = 0; i < firstEight.size(); i++) {
                        topInv.setItem(targetSlots[i], firstEight.get(i).clone());
                    }

                    ItemStack selectedItem = topInv.getItem(20);
                    assert selectedItem != null;

                    topInv.setItem(22, firstEight.get(0));
                    ItemStack defaultFodder = topInv.getItem(22);
                    assert defaultFodder != null;
                    int fodderLevel = equipmentManager.getItemLevel(defaultFodder);

                    int cost = getRequired(selectedItem, defaultFodder);

                    int has = stoneCount(player);
                    topInv.setItem(39, exampleStone.clone());
                    ItemStack stone = topInv.getItem(39);
                    assert stone != null;
                    stone.setAmount(cost);

                    if(has>=cost){
                        topInv.setItem(24, equipmentManager.upgrade(player, selectedItem, fodderLevel));
                    }
                    else{
                        topInv.setItem(24, null);
                    }

                }
                else{
                    event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0B7" + "\uF80D" + "\uF82B\uF829" +"\uE054");
                    topInv.setItem(24, null);
                }

                return;
            }

            if(event.getClickedInventory() == topInv){

                ItemStack selectedItem = topInv.getItem(20);

                if(selectedItem == null){
                    return;
                }

                int slot = event.getSlot();
                boolean swapFodderSlot = Arrays.stream(targetSlots).anyMatch(i -> i == slot);

                if(swapFodderSlot){

                    ItemStack newFodderItem = topInv.getItem(slot);

                    if(newFodderItem == null){
                        return;
                    }

                    topInv.setItem(22, newFodderItem);
                    int cost = getRequired(selectedItem, newFodderItem);
                    int has = stoneCount(player);
                    int newFodderLevel = equipmentManager.getItemLevel(newFodderItem);

                    topInv.setItem(39, exampleStone.clone());
                    ItemStack stone = topInv.getItem(39);
                    assert stone != null;
                    stone.setAmount(cost);

                    if(has>=cost){
                        topInv.setItem(24, equipmentManager.upgrade(player, selectedItem, newFodderLevel));
                    }
                    else{
                        topInv.setItem(24, null);
                    }
                    return;
                }

                if(slot == 24){
                    //take the cost

                    ItemStack newItem = topInv.getItem(24);

                    if(newItem == null){
                        return;
                    }

                    ItemStack oldItem = topInv.getItem(20);

                    if(oldItem == null){
                        //what?
                        return;
                    }

                    ItemStack fodder = topInv.getItem(22);

                    if(fodder == null){
                        //what?
                        return;
                    }

                    ItemStack costItem = topInv.getItem(39);
                    assert costItem != null;
                    int cost = costItem.getAmount();

                    //remove cost stones
                    for(int i = 0; i < bottomInv.getSize(); i++){
                        ItemStack item = bottomInv.getItem(i);

                        if(item == null){
                            continue;
                        }

                        if(!item.isSimilar(costItem)){
                            continue;
                        }

                        int amount = item.getAmount();

                        if(amount <= cost){
                            bottomInv.setItem(i, null);
                            cost -= amount;
                        }
                        else{
                            item.setAmount(amount - cost);
                            break;
                        }

                    }

                    //remove fodder
                    for(int i = 0; i < bottomInv.getSize(); i++){
                        ItemStack item = bottomInv.getItem(i);

                        if(item == null){
                            continue;
                        }

                        if(!item.isSimilar(fodder)){
                            continue;
                        }

                        bottomInv.setItem(i, null);
                        break;
                    }

                    //remove old item
                    for(int i = 0; i < bottomInv.getSize(); i++){
                        ItemStack item = bottomInv.getItem(i);

                        if(item == null){
                            continue;
                        }

                        if(!item.isSimilar(oldItem)){
                            continue;
                        }

                        bottomInv.setItem(i, null);
                        break;
                    }

                    topInv.setItem(20, null);
                    topInv.setItem(22, null);
                    topInv.setItem(24, null);
                    topInv.setItem(39, null);

                    for (int i = 0; i < 8; i++) {
                        topInv.setItem(targetSlots[i], null);
                    }

                    player.getInventory().addItem(newItem);


                }
            }
        }

    }

}
