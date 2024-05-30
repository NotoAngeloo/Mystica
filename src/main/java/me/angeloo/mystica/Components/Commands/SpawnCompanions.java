package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.CreaturesAndCharactersManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCompanions implements CommandExecutor {

    private final CreaturesAndCharactersManager creaturesAndCharactersManager;

    public SpawnCompanions(Mystica main){
        creaturesAndCharactersManager = new CreaturesAndCharactersManager(main);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){

        if(args.length==1){
            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                sender.sendMessage("can't find player");
                return true;
            }

            if(!player.isOnline()){
                sender.sendMessage("not online");
                return true;
            }

            creaturesAndCharactersManager.spawnCompanions(player);
        }



        return true;
    }
}
