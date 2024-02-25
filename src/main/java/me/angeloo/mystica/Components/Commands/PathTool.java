package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Items.PathToolItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PathTool implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(!sender.isOp()){
            return true;
        }

        if(!(sender instanceof Player)){
            return true;
        }

        Player player = (Player) sender;

        player.getInventory().addItem(new PathToolItem());

        return true;
    }

}
