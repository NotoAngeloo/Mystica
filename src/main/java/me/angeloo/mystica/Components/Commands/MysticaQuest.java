package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Quests.LindwyrmQuest;
import me.angeloo.mystica.Components.Quests.SewerQuest;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MysticaQuest implements CommandExecutor {

    private final SewerQuest sewerQuest;
    private final LindwyrmQuest lindwyrmQuest;

    public MysticaQuest(Mystica main){
        sewerQuest = new SewerQuest();
        lindwyrmQuest = new LindwyrmQuest();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player;


        if(args.length==2){
            if(!sender.isOp()){
                return true;
            }

            try{
                player = Bukkit.getPlayer(args[0]);
            }
            catch (IllegalArgumentException e){
                return true;
            }

            if(player == null){
                return true;
            }

            switch (args[1].toLowerCase()){
                case "sewer":{
                    sewerQuest.openSewerQuest(player);
                    return true;
                }
                case "lindwyrm":{
                    lindwyrmQuest.openSewerQuest(player);
                    return true;
                }
                default:{
                    Bukkit.getLogger().info("unknown quest");
                    return true;
                }
            }

        }


        return true;
    }

}
