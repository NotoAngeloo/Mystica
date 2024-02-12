package me.angeloo.mystica.Components.Commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ClearStands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(!sender.isOp()){
            return true;
        }


        World world = Bukkit.getWorld("world");

        assert world != null;
        for (LivingEntity entity : world.getLivingEntities()) {

            if(!(entity instanceof ArmorStand)){
                continue;
            }


            entity.remove();
        }

        return true;
    }
}
