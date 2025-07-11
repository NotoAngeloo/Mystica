package me.angeloo.mystica.Components.Inventories.Storage;

import me.angeloo.mystica.Components.Items.BagItem;
import me.angeloo.mystica.Components.Items.MysticaItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MysticaBag {

    private final MysticaBagCollection collection;

    private final List<MysticaItem> bag = new ArrayList<>();

    public MysticaBag(MysticaBagCollection collection){
        this.collection = collection;
    }

    public void open(Player player){

        // a nothing inventory
        Inventory inventory = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + "\uF808" + "\uE05C");
        player.openInventory(inventory);

        displayBagItems(player);

    }

    public void displayBagItems(Player player){



        //depending on bag unlocks, give a bag in bottom

        for(int i = 0; i<8;i++){

            if(collection.getMysticaBagAmount()-1 >= i){
                player.getInventory().setItem(i, new BagItem().build());
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
        bag.add(mysticaItem);
    }


    public boolean isFull(){

        return bag.size() >= 27;
    }

    public List<MysticaItem> getBag(){
        return this.bag;
    }



}
