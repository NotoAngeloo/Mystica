package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.Party.DungeonSelect;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DungeonFinder implements CommandExecutor {

    private final DungeonSelect dungeonSelect;

    public DungeonFinder(Mystica main){
        dungeonSelect = main.getDungeonSelect();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 0){

            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            Player player = (Player) sender;

            dungeonSelect.openDungeonSelect(player);

            return true;
        }

        if(args.length == 1){

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                sender.sendMessage("player doesn't exist");
                return true;
            }

            if(!player.isOnline()){
                sender.sendMessage("player not online");
                return true;
            }

            dungeonSelect.openDungeonSelect(player);
            return true;
        }

        return true;

    }

}
