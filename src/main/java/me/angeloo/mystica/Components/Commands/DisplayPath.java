package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.PathingManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;


public class DisplayPath implements CommandExecutor {

    private final PathingManager pathingManager;

    public DisplayPath(Mystica main){
        pathingManager = main.getPathingManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(!sender.isOp()){
            return true;
        }

        if(args.length==4){

            Player player;

            try{
                player = Bukkit.getPlayer(args[0]);
            }catch (IllegalArgumentException exception){
                return true;
            }

            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);

            assert player != null;
            Location destination = new Location(player.getWorld(), x, y, z);
            pathingManager.calculatePath(player, destination);


        }

        return true;
    }

}
