package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class DeleteProfile implements CommandExecutor {

    private final Mystica main;
    private final ProfileManager profileManager;

    public DeleteProfile(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(!sender.isOp()){
            return true;
        }

        if(args.length != 1){

            sender.sendMessage("improper usage");

            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if(player == null){
            sender.sendMessage("player doesn't exist");
            return true;
        }

        new BukkitRunnable(){
            @Override
            public void run(){
                profileManager.removePlayerProfile(player);
            }
        }.runTaskLater(main, 20);


        return true;
    }
}
