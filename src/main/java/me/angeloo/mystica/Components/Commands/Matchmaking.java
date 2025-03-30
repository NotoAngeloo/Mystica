package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.MatchmakingInventory;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Matchmaking implements CommandExecutor {

    private final MatchmakingInventory matchmakingInventory;

    public Matchmaking(Mystica main){
        matchmakingInventory = main.getMatchmakingInventory();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 1){

            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            Player player = (Player) sender;

            player.openInventory(matchmakingInventory.openDungeonEnter(args[0]));

            return true;
        }

        if(args.length == 2){

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                sender.sendMessage("player doesn't exist");
                return true;
            }

            if(!player.isOnline()){
                sender.sendMessage("player not online");
                return true;
            }

            player.openInventory(matchmakingInventory.openDungeonEnter(args[1]));
            return true;
        }

        return true;

    }

}
