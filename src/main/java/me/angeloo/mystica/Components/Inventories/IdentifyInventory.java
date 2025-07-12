package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.Items.SoulStone;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class IdentifyInventory implements Listener {

    private final EquipmentManager equipmentManager;

    private final ItemStack exampleStone;

    public IdentifyInventory(Mystica main){
        equipmentManager = main.getEquipmentManager();
        ItemManager manager = main.getItemManager();
        exampleStone = new SoulStone().build();
    }

    //have the identify button show up, only when proper materials put in

    public Inventory openIdentifyInventory(Player player){

        Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF807" + "\uE0AE" + "\uF80D" + "\uF82B\uF829" +"\uE0AF");

        //inv.setItem(20, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(24, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));


        inv.setItem(38, exampleStone.clone());
        ItemStack stone = inv.getItem(38);
        assert stone != null;
        stone.setAmount(stoneCount(player));

        //inv.setItem(39, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
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

    private int getRequired(ItemStack equipment){

        int level = equipmentManager.getItemLevel(equipment);
        int tier = equipmentManager.getItemTier(equipment);

        return level * ((tier * 3) - 2);
    }

    @EventHandler
    public void IdentifyClicks(InventoryClickEvent event){

        if(event.getView().getTitle().contains("\uE0AE")){

            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            Inventory topInv = event.getView().getTopInventory();
            Inventory bottomInv = event.getView().getBottomInventory();
            String title = event.getView().getTitle();

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

                String name = meta.getDisplayName();
                name = name.replaceAll("§.", "");

                if(!name.toLowerCase().contains("unidentified")){
                    //Bukkit.getLogger().info("invalid item");
                    return;
                }

                topInv.setItem(20, item.clone());

                if(topInv.getItem(24) != null){
                    player.getInventory().addItem(topInv.getItem(24));
                    topInv.setItem(24, null);
                }


                int required = getRequired(item);

                topInv.setItem(39, exampleStone.clone());
                ItemStack stone = topInv.getItem(39);
                assert stone != null;
                stone.setAmount(required);

                int has = 0;
                ItemStack hasStones = topInv.getItem(38);
                if(hasStones != null){
                    has = hasStones.getAmount();
                }


                if(has >= required){
                    event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0AE" + "\uF80D" + "\uF82B\uF829" +"\uE0B0");
                }
                else{
                    event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0AE" + "\uF80D" + "\uF82B\uF829" +"\uE0AF");
                }
                return;

            }

            if(event.getClickedInventory() == topInv){

                int slot = event.getSlot();

                if(slot == 24){

                    ItemStack equipment = topInv.getItem(24);

                    if(equipment == null){
                        return;
                    }

                    topInv.setItem(24, null);
                    player.getInventory().addItem(equipment);
                    return;
                }

                if(!title.contains("\uE0B0")){
                    return;
                }


                List<Integer> identifySLots= new ArrayList<>();
                identifySLots.add(51);
                identifySLots.add(52);
                identifySLots.add(53);

                if(identifySLots.contains(slot)){
                    ItemStack blueprint = topInv.getItem(20);
                    assert blueprint != null;
                    ItemStack costItem = topInv.getItem(39);
                    assert costItem != null;
                    int cost = costItem.getAmount();

                    //remove items from players inventory
                    for(int i = 0; i < bottomInv.getSize(); i++){
                        ItemStack item = bottomInv.getItem(i);

                        if(item == null){
                            continue;
                        }

                        if(!item.isSimilar(blueprint)){
                            continue;
                        }

                        bottomInv.setItem(i, null);
                        break;
                    }
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

                    ItemStack equipment = equipmentManager.identify(player, blueprint);
                    topInv.setItem(24, equipment);
                    topInv.setItem(20, null);
                    topInv.setItem(39, null);

                    ItemStack hasStones = exampleStone.clone();
                    hasStones.setAmount(stoneCount(player));
                    topInv.setItem(38, hasStones);

                    event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0AE" + "\uF80D" + "\uF82B\uF829" +"\uE0AF");

                    return;
                }

            }

        }

    }

    @EventHandler
    public void closeIdentify(InventoryCloseEvent event){

        if(event.getView().getTitle().contains("\uE0AE")){

            Player player = (Player) event.getPlayer();

            ItemStack identified = event.getView().getTopInventory().getItem(24);

            if(identified == null){
                return;
            }

            player.getInventory().addItem(identified);
        }

    }



}
