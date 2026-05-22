package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.MysticaGui.GuiManager;

import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;



public class ClassSelectCommand implements CommandExecutor {

    private final GuiManager guiManager;

    public ClassSelectCommand(GuiManager guiManager){
        this.guiManager = guiManager;
    }

    //figure out how to check player status before doing so, dont want them opening a dungeon

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = null;

        if(args.length==0){
            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            if(!sender.isOp()){
                return true;
            }

            player = (Player) sender;
        }

        if(args.length==1){
            if(!sender.isOp()){
                return true;
            }

            try{
                player = Bukkit.getPlayer(args[0]);
            }
            catch (IllegalArgumentException e){
                return true;
            }

        }

        if(player != null){
            guiManager.open(player, new ClassSelect(guiManager));
        }


        return true;
    }
}


