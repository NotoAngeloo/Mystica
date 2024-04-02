package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Guides.ClassGuides;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClassGuide implements CommandExecutor {

    private final ClassGuides classGuides;
    public ClassGuide(Mystica main){
        classGuides = new ClassGuides(main);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = null;

        if(args.length==0){
            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
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
            //classGuideInventory.openClassGuide(player, 0);
            classGuides.openClassGuide(player);
        }


        return true;
    }

}
