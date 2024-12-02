package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Generate implements CommandExecutor {

    private final EquipmentManager equipmentManager;

    public Generate(Mystica main){
        equipmentManager = new EquipmentManager(main);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 1){
            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            Player player = (Player) sender;

            int level = Integer.parseInt(args[0]);

            //testing purposes only, will increase level later
            player.getInventory().addItem(equipmentManager.generate(player, level, -1));
            return true;
        }

        if(args.length == 2){

            Player player = Bukkit.getPlayer(args[0]);

            int level = Integer.parseInt(args[1]);

            if(player == null){
                sender.sendMessage("player doesn't exist");
                return true;
            }

            if(!player.isOnline()){
                sender.sendMessage("player not online");
                return true;
            }

            player.getInventory().addItem(equipmentManager.generate(player, level, -1));
            return true;
        }


        return true;
    }
}
