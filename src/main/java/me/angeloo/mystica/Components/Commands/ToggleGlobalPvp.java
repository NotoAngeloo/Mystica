package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ToggleGlobalPvp implements CommandExecutor {

    private final PvpManager pvpManager;

    public ToggleGlobalPvp(Mystica main){
        pvpManager = main.getPvpManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {


        if(sender.isOp()){
            pvpManager.toggleGlobalPvp();
        }
        else {
            sender.sendMessage("no permission");
        }
        return true;
    }
}
