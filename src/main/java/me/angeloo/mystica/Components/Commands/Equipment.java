package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.EquipmentInventory;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Equipment implements CommandExecutor {

    private final EquipmentInventory equipmentInventory;
    private final ProfileManager profileManager;

    public Equipment(Mystica main){
        equipmentInventory = main.getEquipmentInventory();
        profileManager = main.getProfileManager();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){

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

        equipmentInventory.openEquipmentInventory(player);


        return true;
    }
}