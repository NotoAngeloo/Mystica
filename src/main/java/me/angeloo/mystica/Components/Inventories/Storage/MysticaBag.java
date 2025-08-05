package me.angeloo.mystica.Components.Inventories.Storage;

import com.google.gson.Gson;
import me.angeloo.mystica.Components.Items.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MysticaBag{

    private final MysticaBagCollection collection;

    private final List<MysticaItem> bag = new ArrayList<>();

    public MysticaBag(MysticaBagCollection collection){
        this.collection = collection;
    }

    public void open(Player player){


        //nothing, -8, bag png
        Inventory inventory = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF808" + "\uE05C");
        player.openInventory(inventory);
        displayBagItems(player);

    }

    public void displayBagItems(Player player){


        //depending on bag unlocks, give a bag in bottom
        player.getInventory().clear();

        for(int i = 0; i<8;i++){

            if(collection.getMysticaBagAmount()-1 >= i){
                player.getInventory().setItem(i, new BagItem(1).build());
                continue;
            }

            break;
        }


        for(int i = 0; i<26;i++){

            if(bag.size()<=i){
                break;
            }

            player.getInventory().setItem(i+9, bag.get(i).build());
        }

    }

    public void addItem(MysticaItem mysticaItem){

        //check if stackable first
        if(mysticaItem.format().equals(MysticaItemFormat.STACKABLE)){

            Map<String, Object> data = mysticaItem.serialize();
            int addAmount = (int) data.getOrDefault("amount", 0);

            //check if player has already
            MysticaItem alreadyItem = null;
            for(MysticaItem bagItem : bag){

                if(bagItem.identifier().equals(mysticaItem.identifier())){
                    alreadyItem = bagItem;
                    break;
                }

            }

            if(alreadyItem == null){
                bag.add(mysticaItem);
                return;
            }

            Map<String, Object> alreadyData = alreadyItem.serialize();
            int alreadyAmount = (int) alreadyData.getOrDefault("amount", 0);

            if(alreadyItem instanceof StackableItem stackableItem){
                stackableItem.setAmount(alreadyAmount + addAmount);
            }

            return;
        }

        bag.add(mysticaItem);

    }


    public boolean isFull(){

        return bag.size() >= 27;
    }

    public List<MysticaItem> getBag(){
        return this.bag;
    }

    public boolean removeFromBag(MysticaItem item){

        Map<String, Object> itemData = item.serialize();

        for(MysticaItem bagItem : bag){


            Map<String, Object> bagItemData = bagItem.serialize();

            if(bagItemData == null){
                continue;
            }

            if(bagItemData.equals(itemData)){
                bag.remove(bagItem);
                return true;
            }

        }

        return false;
    }

    public int removeAnAmountOfStackables(StackableItem item, int toRemove){

        int successfullyRemoved = 0;


        for(MysticaItem bagItem : bag){

            if(bagItem instanceof StackableItem stackableItem){

                if(item.identifier().equals(bagItem.identifier())){
                    int current = stackableItem.getAmount();

                    if(current <= toRemove){
                        bag.remove(bagItem);
                        successfullyRemoved += current;
                        return successfullyRemoved;
                    }

                    stackableItem.setAmount(current-toRemove);
                    return toRemove;
                }

            }

        }

        return successfullyRemoved;
    }


    public int getSoulStoneAmount(){

        int amount = 0;

        for(MysticaItem item : bag){

            if(item instanceof StackableItem stackableItem){
                if(!item.identifier().equalsIgnoreCase("soul stone")){
                    continue;
                }

                amount += stackableItem.getAmount();
            }


        }

        return amount;
    }

    public List<MysticaEquipment> getEquipment(){

        List<MysticaEquipment> equipment = new ArrayList<>();

        for(MysticaItem item : bag){
            if(item instanceof MysticaEquipment valid){
                equipment.add(valid);
            }
        }

        return equipment;
    }



}
