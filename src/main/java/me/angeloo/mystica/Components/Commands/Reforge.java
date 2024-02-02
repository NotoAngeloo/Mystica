package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.ReforgeInventory;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Reforge implements CommandExecutor {

    private final ReforgeInventory reforgeInventory;

    public Reforge(Mystica main){
        reforgeInventory = new ReforgeInventory(main);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(sender instanceof Player)){
            sender.sendMessage("only players");
            return true;
        }

        Player player = (Player) sender;

        player.openInventory(reforgeInventory.openReforgeInventory(player, null, false));

        return true;
    }
}
