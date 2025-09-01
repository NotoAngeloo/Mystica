package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Quests.Quest;
import me.angeloo.mystica.Components.Quests.QuestAcceptInventory;
import me.angeloo.mystica.Components.Quests.QuestManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MysticaQuest implements CommandExecutor {

    private final QuestManager questManager;
    private final QuestAcceptInventory questAcceptInventory;

    public MysticaQuest(Mystica main){
        questManager = main.getQuestManager();
        questAcceptInventory = main.getQuestAcceptInventory();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 1){

            if(args[0].equals("reload") || args[0].equals("r")){

                questManager.loadQuests();

                return true;
            }

        }

        if (args.length == 2) {

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                return true;
            }

            Quest quest = questManager.getQuest(args[1]);

            if(quest == null){
                sender.sendMessage("no quest " + args[1] + " found");
                return true;
            }

            questAcceptInventory.openQuestAccept(player, quest);


        }



        return true;
    }
}
