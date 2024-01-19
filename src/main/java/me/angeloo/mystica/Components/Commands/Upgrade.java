package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Upgrade implements CommandExecutor {

    private final EquipmentManager equipmentManager;

    public Upgrade(Mystica main){
        equipmentManager = new EquipmentManager(main);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(sender instanceof Player)){
            sender.sendMessage("only players");
            return true;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType().equals(Material.AIR)){
            return true;
        }

        //just for testing purposes, 2 can be changed in the ui
        equipmentManager.upgrade(player, item, 2);

        return true;
    }
}
