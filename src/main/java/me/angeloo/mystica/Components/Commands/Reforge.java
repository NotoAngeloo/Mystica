package me.angeloo.mystica.Components.Commands;

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

    private final ProfileManager profileManager;
    private final EquipmentManager equipmentManager;

    public Reforge(Mystica main){
        profileManager = main.getProfileManager();
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

        equipmentManager.reforge(player, item);

        return true;
    }
}
