package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Guis.Misc.DevBoxInventory;
import me.angeloo.mystica.Components.Items.MysticalCrystal;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Components.Items.UnidentifiedItem;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DevBox implements CommandExecutor {

    private final DevBoxInventory devBoxInventory;

    public DevBox(Mystica main){
        devBoxInventory = main.getDevBoxInventory();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 0){

            if(!(sender instanceof Player player)){
                sender.sendMessage("only players");
                return true;
            }

            devBoxInventory.open(player);
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


            devBoxInventory.open(player);
            return true;
        }


        return true;
    }

}
