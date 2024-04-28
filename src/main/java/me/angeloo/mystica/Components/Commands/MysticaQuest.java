package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.QuestInventory;
import me.angeloo.mystica.Components.Quests.LindwyrmQuest;
import me.angeloo.mystica.Components.Quests.SewerQuest;
import me.angeloo.mystica.Managers.InventoryIndexingManager;
import me.angeloo.mystica.Managers.ProfileManager;
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
    private final QuestInventory questInventory;
    private final InventoryIndexingManager inventoryIndexingManager;

    public MysticaQuest(Mystica main){
        questManager = main.getQuestManager();
        questInventory = main.getQuestInventory();
        inventoryIndexingManager = main.getInventoryIndexingManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player;

        if(args.length==0){
            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            player = (Player) sender;

            player.openInventory(questInventory.openQuestInventory(player, inventoryIndexingManager.getQuestIndex(player)));

            return true;
        }

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

            if (args[1].equalsIgnoreCase("navigate")) {
                questManager.navigateQuest(player);
                return true;
            }

            questManager.setQueuedQuest(player, args[1]);
            return true;

        }

        if(args.length==3){

            try{
                player = Bukkit.getPlayer(args[0]);
            }
            catch (IllegalArgumentException e){
                return true;
            }

            if(player == null){
                return true;
            }

            if(!sender.isOp()){
                return true;
            }

            if(args[1].equalsIgnoreCase("reward")){
                questManager.rewardQuest(player,args[2]);
                return true;
            }

            if(args[1].equalsIgnoreCase("complete")){
                questManager.completeQuest(player, args[2]);
                return true;
            }


        }


        return true;
    }

}
