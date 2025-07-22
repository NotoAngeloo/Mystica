package me.angeloo.mystica.Components.Inventories.Storage;

import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Items.StackableItem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MysticaBagCollection {

    private final List<MysticaBag> bags;

    public MysticaBagCollection(List<MysticaBag> bags){
        this.bags = bags;
    }

    public void addBag(){

        if(bags.size()>=9){
            return;
        }

        this.bags.add(new MysticaBag(this));
    }

    public void openMysticaBag(Player player, int number){

        bags.get(number).open(player);

    }

    public MysticaBag getBag(int index){

        if(bags.size() > index){
            return bags.get(index);
        }

        return bags.get(0);
    }

    public List<MysticaBag> getBags(){
        return this.bags;
    }

    public int getMysticaBagAmount(){
        return bags.size();
    }

    public boolean addToSpecificBag(MysticaItem mysticaItem, int index){

        if(this.bags.size() < index){
            return false;
        }

        MysticaBag bag = bags.get(index);

        if(bag.isFull()){
            return false;
        }

        bag.addItem(mysticaItem);

        return true;
    }

    public void addToFirstBag(MysticaItem mysticaItem){

        for(MysticaBag bag : bags){

            //find a way to check if ALL full later

            if(bag.isFull()){
                continue;
            }

            bag.addItem(mysticaItem);


        }
    }

    public void removeItemsFromMultipleBags(MysticaItem item){

        if(item instanceof StackableItem stackableItem){

            int amountToRemove = stackableItem.getAmount();

            for(MysticaBag bag : bags){

                int removed = bag.removeAnAmountOfStackables(stackableItem, amountToRemove);

                amountToRemove -= removed;

                if(amountToRemove == 0){
                    break;
                }

            }

            return;
        }

        for(MysticaBag bag : bags){

            if(bag.removeFromBag(item)){
                break;
            }

        }

    }

    public int getSoulStoneAmount(){

        int amount = 0;

        for(MysticaBag bag : getBags()){

            amount += bag.getSoulStoneAmount();

        }

        return amount;
    }

    public List<MysticaEquipment> getBagEquipment(){

        List<MysticaEquipment> allEquipment = new ArrayList<>();

        for(MysticaBag bag : bags){
            allEquipment.addAll(bag.getEquipment());
        }

        return allEquipment;
    }

}
