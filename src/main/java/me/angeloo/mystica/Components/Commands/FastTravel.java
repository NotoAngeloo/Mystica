package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.FastTravelInv;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FastTravel implements CommandExecutor {

    private final FastTravelInv fastTravelInv;

    public FastTravel(Mystica main){
        fastTravelInv = new FastTravelInv(main);
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args){

        if(!(sender.isOp())){
            sender.sendMessage("you do not have permissions");
            return true;
        }

        if(args.length==0){

            if(!(sender instanceof Player)){
                return true;
            }

            Player player = (Player) sender;
            player.openInventory(fastTravelInv.openFastTravelInv(player, new ItemStack(Material.AIR)));
        }

        if(args.length==1){

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                sender.sendMessage("can't find player");
                return true;
            }

            if(!player.isOnline()){
                sender.sendMessage("not online");
                return true;
            }

            player.openInventory(fastTravelInv.openFastTravelInv(player, new ItemStack(Material.AIR)));
            //player.getWorld().setSpawnLocation(player.getLocation());
        }

        return true;
    }

}
