package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.PathingManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SavePaths implements CommandExecutor {

    private final PathingManager pathingManager;
    public SavePaths(Mystica main){
        pathingManager = main.getPathingManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        pathingManager.saveFolder();

        return true;
    }

}
