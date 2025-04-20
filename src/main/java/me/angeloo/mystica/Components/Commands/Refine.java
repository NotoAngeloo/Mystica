package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.RefineInventory;
import me.angeloo.mystica.Components.Inventories.ReforgeInventory;
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

public class Refine implements CommandExecutor {

    private final RefineInventory refineInventory;

    public Refine(Mystica main){
        refineInventory = main.getRefineInventory();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 0){

            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            Player player = (Player) sender;


            player.openInventory(refineInventory.openRefineInventory(player));

            return true;
        }



        return true;
    }

}
