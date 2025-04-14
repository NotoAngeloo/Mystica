package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.BagInventory;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiveSoulStone implements CommandExecutor {

    private final ProfileManager profileManager;
    private final BagInventory bagInventory;
    private final ItemManager itemManager;

    public GiveSoulStone(Mystica main){
        profileManager = main.getProfileManager();
        itemManager = main.getItemManager();
        bagInventory = main.getBagInventory();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if(!sender.isOp()){
            sender.sendMessage("you do not have permissions");
            return true;
        }



        if(args.length != 2){
            sender.sendMessage("improper usage");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if(player == null){
            sender.sendMessage("can't find player");
            return true;
        }

        if(!player.isOnline()){
            sender.sendMessage("not online");
            return true;
        }

        int amount = Integer.parseInt(args[1]);

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        ItemStack stone = itemManager.getSoulStone().getSoulStone();

        if(!combatStatus){
            for(int i = 0; i < amount; i++){

                player.getInventory().addItem(stone);
            }
        }

        else{

            List<ItemStack> items = new ArrayList<>();

            for(int i = 0; i < amount + 1; i++){
                player.getInventory().addItem(stone);
            }

            bagInventory.addItemsToPlayerBagByPickup(player, items);
        }



        return true;

    }
}
