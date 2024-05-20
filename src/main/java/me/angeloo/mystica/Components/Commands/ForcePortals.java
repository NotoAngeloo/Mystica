package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.DailyEventManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ForcePortals implements CommandExecutor {

    private final DailyEventManager dailyEventManager;

    public ForcePortals(Mystica main){
        dailyEventManager = main.getDailyEventManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        dailyEventManager.getDemonInvasion().spawnDemonPortals();

        return true;
    }
}
