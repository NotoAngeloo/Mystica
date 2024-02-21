package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMilestone implements CommandExecutor {

    private final ProfileManager profileManager;

    public SetMilestone(Mystica main){
        profileManager = main.getProfileManager();
    }
    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){

        if(!sender.isOp()){
            sender.sendMessage("you do not have permissions");
            return true;
        }


        if(args.length == 3){

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                sender.sendMessage("can't find player");
                return true;
            }

            if(!player.isOnline()){
                sender.sendMessage("not online");
                return true;
            }

            String milestone = args[1];

            boolean setTo = Boolean.parseBoolean(args[2]);

            profileManager.getAnyProfile(player).getMilestones().setMilestone(milestone, setTo);


            return true;
        }

        return true;
    }
}
