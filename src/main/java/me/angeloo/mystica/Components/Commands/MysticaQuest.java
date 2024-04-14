package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Quests.LindwyrmQuest;
import me.angeloo.mystica.Components.Quests.SewerQuest;
import me.angeloo.mystica.Managers.QuestManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MysticaQuest implements CommandExecutor {

    private final QuestManager questManager;


    public MysticaQuest(Mystica main){
        questManager = main.getQuestManager();

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player;


        //uuid, <quest/accept>

        if(args.length==2){

            try{
                player = Bukkit.getPlayer(args[0]);
            }
            catch (IllegalArgumentException e){
                return true;
            }

            if(player == null){
                return true;
            }

            if(sender instanceof Player){
                if(player != sender){
                    if(!sender.isOp()){
                        return true;
                    }
                }
            }

            if (args[1].equalsIgnoreCase("accept")) {
                questManager.acceptQuest(player);
                return true;
            }

            questManager.setQueuedQuest(player, args[1]);
            return true;

        }


        return true;
    }

}
