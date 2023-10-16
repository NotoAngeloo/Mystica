package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.BagInventory;
import me.angeloo.mystica.Managers.InventoryIndexingManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Bag implements CommandExecutor {

    private final BagInventory bagInventory;
    private final InventoryIndexingManager inventoryIndexingManager;
    private final ProfileManager profileManager;

    public Bag(Mystica main){
        bagInventory = main.getBagInventory();
        inventoryIndexingManager = main.getInventoryIndexingManager();
        profileManager = main.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String[] args){

        if(!(sender instanceof Player)){
            sender.sendMessage("only players");
            return true;
        }

        Player player = (Player) sender;

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(combatStatus){
            player.sendMessage("You can't do this right now");
            return true;
        }

        player.openInventory(bagInventory.openBagInventory(player, inventoryIndexingManager.getBagIndex(player)));

        return true;
    }

}
