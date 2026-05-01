package me.angeloo.mystica.Utility.MechanicCircle;

import me.angeloo.mystica.Mystica;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class CircleCommand implements CommandExecutor {

    private final Mystica main;

    public CircleCommand(Mystica main){
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        // Defaults
        float radius = 3.0f;
        int duration = 60;

        // Parse args
        if (args.length >= 1) {
            try {
                radius = Float.parseFloat(args[0]);
            } catch (Exception ignored) {}
        }

        if (args.length >= 2) {
            try {
                duration = Integer.parseInt(args[1]);
            } catch (Exception ignored) {}
        }


        // Get target location (ray trace)
        //Location loc = getTargetLocation(player);
        Location loc = player.getLocation();
        loc.setPitch(0);
        loc.setYaw(0);

        if (loc == null) {
            player.sendMessage("§cNo valid target location.");
            return true;
        }

        // Slight offset to prevent z-fighting
        loc.add(0, 0.01, 0);

        // Spawn circle
        MechanicCircle circle = MechanicCircle.spawn(
                main,
                loc,
                radius,
                duration
                // fill glyph
                // outline glyph
        );

        circle.start();

        player.sendMessage("§aSpawned circle | Radius: " + radius + " | Duration: " + duration);

        return true;
    }

    private Location getTargetLocation(Player player) {

        RayTraceResult result = player.rayTraceBlocks(50);

        if (result == null) {
            return null;
        } else {
            result.getHitPosition();
        }

        return result.getHitPosition().toLocation(player.getWorld());
    }

}
