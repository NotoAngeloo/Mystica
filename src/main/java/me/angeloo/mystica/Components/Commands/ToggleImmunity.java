package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleImmunity implements CommandExecutor {

    private final BuffAndDebuffManager buffAndDebuffManager;

    public ToggleImmunity(Mystica main){
        buffAndDebuffManager = main.getBuffAndDebuffManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(sender instanceof Player)){
            sender.sendMessage("only players");
            return true;
        }

        Player player = (Player) sender;

        if(buffAndDebuffManager.getImmune().getImmune(player)){
            buffAndDebuffManager.getImmune().removeImmune(player);
            player.sendMessage("removed immunity");
            return true;
        }

        buffAndDebuffManager.getImmune().applyImmune(player, 0);
        player.sendMessage("applied immunity");

        return true;
    }
}
