package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MysticaInteractions implements CommandExecutor {

    private final ProfileManager profileManager;

    public MysticaInteractions(Mystica main){
        profileManager = main.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }

        //Bukkit.getLogger().info("calling");

        if(args.length==2){

            Server server = Bukkit.getServer();

            String conversation = args[0];

            Player player;

            //Bukkit.getLogger().info(args[0] + " " + args[1]);

            try {
                player = Bukkit.getPlayer(args[1]);
            }
            catch (IllegalArgumentException exception){
                return true;
            }

            assert player != null;


            if(profileManager.getAnyProfile(player).getIfInCombat()){
                player.sendMessage("Can't do this in combat");
                return true;
            }

            if(profileManager.getAnyProfile(player).getIfDead()){
                player.sendMessage("Can't do this while dead");
                return true;
            }


            server.dispatchCommand(server.getConsoleSender(), "interactions start " + conversation + " " + player.getName());


        }

        return true;
    }

}
