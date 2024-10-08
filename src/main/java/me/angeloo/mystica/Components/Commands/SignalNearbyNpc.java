package me.angeloo.mystica.Components.Commands;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SignalNearbyNpc implements CommandExecutor {

    public SignalNearbyNpc(){

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args){

        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }

        //player, signal
        if(args.length == 2){

            Player player;

            //Bukkit.getLogger().info(args[0] + " " + args[1]);

            try {
                player = Bukkit.getPlayer(args[0]);
            }
            catch (IllegalArgumentException exception){
                return true;
            }

            assert player != null;

            Location loc = player.getLocation();

            String signal = args[1];

            BoundingBox hitBox = new BoundingBox(
                    loc.getX() - 20,
                    loc.getY() - 20,
                    loc.getZ() - 20,
                    loc.getX() + 20,
                    loc.getY() + 20,
                    loc.getZ() + 20
            );

            for(Entity entity : player.getWorld().getNearbyEntities(hitBox)){


                if(!MythicBukkit.inst().getAPIHelper().isMythicMob(entity.getUniqueId())){
                    continue;
                }

                AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getEntity();
                MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).signalMob(abstractEntity, signal);

            }


        }

        return true;
    }

}
