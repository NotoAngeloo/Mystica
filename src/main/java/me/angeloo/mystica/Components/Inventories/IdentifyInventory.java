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
import java.util.List;

public class IdentifyInventory implements Listener {

    private final EquipmentManager equipmentManager;

    private final ItemStack exampleStone;

    public IdentifyInventory(Mystica main){
        equipmentManager = main.getEquipmentManager();
        ItemManager manager = main.getItemManager();
        exampleStone = manager.getSoulStone().getSoulStone();
    }

    //have the identify button show up, only when proper materials put in

    public Inventory openIdentifyInventory(Player player, boolean button){

        Inventory inv;

        if(button){
            inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF807" + "\uE0AE" + "\uF80D" + "\uF82B\uF829" +"\uE0B0");
        }else{
            inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF807" + "\uE0AE" + "\uF80D" + "\uF82B\uF829" +"\uE0AF");
        }

        //inv.setItem(20, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(24, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));


        inv.setItem(37, exampleStone);
        ItemStack stone = inv.getItem(37);
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

            assert exampleStone.hasItemMeta();
            assert exampleStone.getItemMeta() != null;
            assert exampleStone.getItemMeta().hasDisplayName();
            if(meta.getDisplayName().equalsIgnoreCase(exampleStone.getItemMeta().getDisplayName())){
                count += item.getAmount();
            }

        }
        return count;
    }

    private int getRequired(ItemStack equipment){

        int level = equipmentManager.getItemLevel(equipment);
        int tier = equipmentManager.getItemTier(equipment);

        Bukkit.getLogger().info("level " + level + " tier " + tier);

        return level * ((tier * 3) - 2);
    }

    @EventHandler
    public void IdentifyClicks(InventoryClickEvent event){

        if(event.getView().getTitle().contains("\uE0AE")){

            event.setCancelled(true);

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
            Inventory topInv = event.getView().getTopInventory();
            Inventory bottomInv = event.getView().getBottomInventory();
            String title = event.getView().getTitle();

            if(event.getClickedInventory() == bottomInv){
                String name = meta.getDisplayName();
                name = name.replaceAll("§.", "");

                if(!name.toLowerCase().contains("unidentified")){
                    Bukkit.getLogger().info("invalid item");
                    return;
                }

                topInv.setItem(20, item.clone());

                int required = getRequired(item);

                topInv.setItem(39, exampleStone);
                ItemStack stone = topInv.getItem(39);
                assert stone != null;
                stone.setAmount(required);

            }



        }

    }



}
