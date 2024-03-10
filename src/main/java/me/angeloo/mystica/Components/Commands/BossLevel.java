package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.BossLevelInv;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BossLevel implements CommandExecutor {

    private final BossLevelInv bossLevelInv;

    public BossLevel(Mystica main){
        bossLevelInv = new BossLevelInv(main);
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args){

        if(!(sender.isOp())){
            sender.sendMessage("you do not have permissions");
            return true;
        }


        if(args.length == 0){
            sender.sendMessage("improper usage");
            return true;
        }

        if(args.length == 1){

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                sender.sendMessage("can't find player");
                return true;
            }

            if(!player.isOnline()){
                sender.sendMessage("not online");
                return true;
            }

            player.openInventory(bossLevelInv.openBossLevelInv(player));

            return true;
        }

        return true;
    }
}
