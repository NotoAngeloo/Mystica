package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.BossManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class StartFuryTimer implements CommandExecutor {

    private final BossManager bossManager;

    public StartFuryTimer(Mystica main) {
        bossManager = main.getBossManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }

        if(args.length==2){

            UUID uuid = UUID.fromString(args[0]);

            int time = Integer.parseInt(args[1]);

            bossManager.startFuryTimer(uuid, time);

        }


        return true;
    }

}
