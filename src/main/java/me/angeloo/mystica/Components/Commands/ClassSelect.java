package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Inventories.ClassSelectInventory;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;



public class ClassSelect implements CommandExecutor {

    private final ProfileManager profileManager;
    private final ClassSelectInventory classSelectInventory;

    public ClassSelect(Mystica main){
        profileManager = main.getProfileManager();
        classSelectInventory = new ClassSelectInventory();
    }

    //figure out how to check player status before doing so, dont want them opening a dungeon

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(sender instanceof Player)){
            sender.sendMessage("only players");
            return true;
        }

        Player player = (Player) sender;

        player.openInventory(classSelectInventory.openClassSelect(profileManager.getAnyProfile(player).getPlayerClass()));

        return true;
    }
}


