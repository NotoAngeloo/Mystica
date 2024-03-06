package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageHealthBoard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleBoardType implements CommandExecutor {

    private final DamageHealthBoard damageHealthBoard;

    public ToggleBoardType(Mystica main){
        damageHealthBoard = main.getDamageHealthBoard();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){

        if(!(sender instanceof Player)){
            sender.sendMessage("only players");
            return true;
        }

        Player player = (Player) sender;

        damageHealthBoard.toggle(player);

        return true;
    }
}
