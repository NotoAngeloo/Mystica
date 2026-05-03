package me.angeloo.mystica.Utility.MechanicCircle;

import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CircleCommand implements CommandExecutor {

    private final Mystica main;

    private static final Pattern HEX_PATTERN =
            Pattern.compile("^<?#?([A-Fa-f0-9]{6})>?$");

    public CircleCommand(Mystica main){
        this.main = main;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // =========================
        // DEFAULTS
        // =========================
        float radius = 3.0f;
        int duration = 60;
        int linger = 0;
        ChatColor fillColor = ChatColor.RED;

        boolean hasLocation = hasLocation(args);
        int paramEnd = args.length;

        if (hasLocation) {
            paramEnd -= 3;
        }

        // =========================
        // PARAMETER PARSING (LEFT SIDE)
        // =========================
        int index = 0;

        // radius
        if (index < paramEnd) {
            try {
                radius = Float.parseFloat(args[index]);
                index++;
            } catch (Exception ignored) {}
        }

        // duration
        if (index < paramEnd) {
            try {
                duration = Integer.parseInt(args[index]);
                index++;
            } catch (Exception ignored) {}
        }

        // flexible args (linger / color / etc.)
        for (int i = index; i < paramEnd; i++) {
            String arg = args[i];

            if (isInteger(arg)) {
                linger = Integer.parseInt(arg);
                continue;
            }

            ChatColor parsed = tryParseColor(arg);
            if (parsed != null) {
                fillColor = parsed;
            }
        }

        // =========================
        // LOCATION PARSING (RIGHT SIDE)
        // =========================
        Location loc;

        if (hasLocation) {

            int len = args.length;

            try {
                double x = Double.parseDouble(args[len - 3]);
                double y = Double.parseDouble(args[len - 2]);
                double z = Double.parseDouble(args[len - 1]);

                World world = (sender instanceof Player player)
                        ? player.getWorld()
                        : Bukkit.getWorlds().get(0);

                loc = new Location(world, x, y, z);

            } catch (Exception e) {
                sender.sendMessage("§cInvalid location.");
                return false;
            }

        } else {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cConsole must specify location.");
                return false;
            }

            loc = player.getLocation();
        }

        // =========================
        // NORMALIZE LOCATION
        // =========================
        loc.setPitch(0);
        loc.setYaw(0);
        loc.add(0, 0.01, 0);

        // =========================
        // SPAWN CIRCLE
        // =========================
        MechanicCircle circle = MechanicCircle.spawn(
                main,
                loc,
                radius,
                duration,
                linger,
                fillColor
        );

        circle.start();

        // =========================
        // FEEDBACK
        // =========================
        if (sender instanceof Player player) {
            player.sendMessage("§aCircle spawned | R:" + radius +
                    " D:" + duration +
                    " L:" + linger);
        }

        return true;
    }



    private boolean hasLocation(String[] args) {
        if (args.length < 3) return false;

        return isDouble(args[args.length - 3]) &&
                isDouble(args[args.length - 2]) &&
                isDouble(args[args.length - 1]);
    }


    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private ChatColor tryParseColor(String input) {

        Matcher matcher = HEX_PATTERN.matcher(input);

        if (matcher.matches()) {
            String hex = matcher.group(1);

            try {
                return ChatColor.of("#" + hex);
            } catch (Exception ignored) {}
        }

        return null;
    }

}
