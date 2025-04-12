package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.IdentifyInventory;
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

public class Identify implements CommandExecutor {

    private final EquipmentManager equipmentManager;
    private final IdentifyInventory identifyInventory;

    public Identify(Mystica main){
        identifyInventory = new IdentifyInventory();
        equipmentManager = main.getEquipmentManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 0){

            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            Player player = (Player) sender;

            ItemStack equipment = player.getInventory().getItemInMainHand();

            player.getInventory().addItem(equipmentManager.identify(player, equipment));

            //player.openInventory(identifyInventory.openIdentifyInventory(new ItemStack(Material.AIR)));

            return true;
        }

        if(args.length == 1){

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                sender.sendMessage("player doesn't exist");
                return true;
            }

            if(!player.isOnline()){
                sender.sendMessage("player not online");
                return true;
            }

            //player.openInventory(identifyInventory.openIdentifyInventory(new ItemStack(Material.AIR)));
            return true;
        }

        return true;
    }

}
