package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Locations;
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
    private final Locations locations;

    public MysticaInteractions(Mystica main){
        profileManager = main.getProfileManager();
        locations = main.getLocations();
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


            switch (conversation.toLowerCase()){
                case "fasttravel":{
                    //check location

                    Location closest = locations.getNearestLocation(player);

                    if (closest.equals(locations.caveOfLindwyrm())) {
                        profileManager.getAnyProfile(player).getMilestones().setMilestone("lindwyrm.visit", true);
                    } else if (closest.equals(locations.windbluff())) {
                        profileManager.getAnyProfile(player).getMilestones().setMilestone("windbluff.visit", true);
                    } else if (closest.equals(locations.outpost())) {
                        profileManager.getAnyProfile(player).getMilestones().setMilestone("tradecamp.visit", true);
                    }
                    else if(closest.equals(locations.stonemont())){
                        return true;
                    }

                    return true;
                }
                default:{
                    server.dispatchCommand(server.getConsoleSender(), "interactions start " + conversation + " " + player.getName());
                }
            }


        }

        return true;
    }

}
