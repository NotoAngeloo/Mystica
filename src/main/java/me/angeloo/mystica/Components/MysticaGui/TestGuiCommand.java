package me.angeloo.mystica.Components.MysticaGui;

import me.angeloo.mystica.Components.MysticaGui.Guis.TestGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestGuiCommand implements CommandExecutor {

    private final GuiManager guiManager;

    public TestGuiCommand(GuiManager guiManager){
        this.guiManager = guiManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if(!(sender instanceof Player player))
            return true;

        guiManager.open(
                player,
                new TestGui()
        );

        return true;
    }

}
