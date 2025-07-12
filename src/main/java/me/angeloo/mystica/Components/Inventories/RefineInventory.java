package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RefineInventory implements Listener {

    private final EquipmentManager equipmentManager;

    private final ItemStack exampleStone;

    public RefineInventory(Mystica main){
        equipmentManager = new EquipmentManager(main);
        ItemManager manager = main.getItemManager();
        exampleStone = new SoulStone().build();
    }

    public Inventory openRefineInventory(Player player){

        Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF807" + "\uE0B4" + "\uF80D" + "\uF82B\uF829" +"\uE0B5");

        inv.setItem(38, exampleStone.clone());
        ItemStack stone = inv.getItem(38);
        assert stone != null;
        stone.setAmount(stoneCount(player));

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

    private boolean validItem(ItemStack item){

        if(!item.hasItemMeta()){
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if(!meta.hasLore()){
            return false;
        }

        List<String> lores = meta.getLore();
        assert lores != null;
        for(String loreLine : lores){
            if(loreLine.contains("Special Attribute")){
                return true;
            }
        }

        return false;
    }

    private int getRequired(ItemStack equipment){

        int tier = equipmentManager.getEquipmentTier(equipment);

        return tier * 2;
    }


    @EventHandler
    public void refineClicks(InventoryClickEvent event){

        if(event.getView().getTitle().contains("\uE0B4")){

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

                if(!validItem(item)){
                    return;
                }


                topInv.setItem(24, null);
                topInv.setItem(20, item.clone());

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
                    event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0B4" + "\uF80D" + "\uF82B\uF829" +"\uE0B6");
                }
                else{
                    event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0B4" + "\uF80D" + "\uF82B\uF829" +"\uE0B5");
                }
                return;

            }

            if(event.getClickedInventory() == topInv){

                int slot = event.getSlot();

                if(slot == 22 && title.contains("\uE0B6")){

                    ItemStack blueprint = topInv.getItem(20);
                    assert blueprint != null;
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

                    ItemStack refined = equipmentManager.refine(blueprint);
                    topInv.setItem(24, refined);

                    ItemStack hasStones = exampleStone.clone();
                    hasStones.setAmount(stoneCount(player));
                    topInv.setItem(38, hasStones);
                    int has = hasStones.getAmount();
                    int required = getRequired(blueprint);

                    if(has >= required){
                        event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0B4" + "\uF80D" + "\uF82B\uF829" +"\uE0B6");
                    }
                    else{
                        event.getView().setTitle(ChatColor.WHITE + "\uF807" + "\uE0B4" + "\uF80D" + "\uF82B\uF829" +"\uE0B5");
                    }

                    return;
                }

                if(slot == 24){

                    ItemStack equipment = topInv.getItem(24);
                    ItemStack oldItem = topInv.getItem(20);

                    if(equipment == null){
                        return;
                    }

                    if(oldItem == null){
                        //what?
                        return;
                    }


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


                    topInv.setItem(24, null);
                    topInv.setItem(20, null);
                    topInv.setItem(39, null);
                    player.getInventory().addItem(equipment);

                    return;
                }

            }

        }

    }

}
