package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.Quests.QuestAcceptInventory;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TestQuestGui implements CommandExecutor {

    private final QuestAcceptInventory questAcceptInventory;

    public TestQuestGui(Mystica main){
        questAcceptInventory = main.getQuestAcceptInventory();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length >= 1) {

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                return true;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]);
                if (i < args.length - 1) {
                    sb.append(" ");
                }
            }

            String text = sb.toString();

            questAcceptInventory.openQuestAccept(player, text);

        }



        return true;
    }
}
