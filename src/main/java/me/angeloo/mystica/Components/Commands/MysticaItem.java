package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MysticaItem implements CommandExecutor {

    private final ItemManager itemManager;

    public MysticaItem(Mystica main){
        itemManager = main.getItemManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 0){

            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            Player player = (Player) sender;

            player.getInventory().addItem(itemManager.getUnidentifiedWeapon().getUnidentifiedT1Weapon(1));
            player.getInventory().addItem(itemManager.getUnidentifiedWeapon().getUnidentifiedT2Weapon(1));
            player.getInventory().addItem(itemManager.getUnidentifiedWeapon().getUnidentifiedT3Weapon(1));

            player.getInventory().addItem(itemManager.getUnidentifiedWeapon().getUnidentifiedT1Weapon(2));
            player.getInventory().addItem(itemManager.getUnidentifiedWeapon().getUnidentifiedT1Weapon(3));

            player.getInventory().addItem(itemManager.getUnidentifiedHelmet().getUnidentifiedT1Helmet(1));
            player.getInventory().addItem(itemManager.getUnidentifiedChestplate().getUnidentifiedT1Chestplate(1));
            player.getInventory().addItem(itemManager.getUnidentifiedLeggings().getUnidentifiedT1Leggings(1));
            player.getInventory().addItem(itemManager.getUnidentifiedBoots().getUnidentifiedT1Boots(1));

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


            return true;
        }


        return true;
    }

}
