package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Locations {

    private final Mystica main;

    public Locations(Mystica main){
        this.main = main;
    }

    public void displayDungeonEnters(){

        World world = Bukkit.getWorld("world");
        assert world != null;

        Location sewerLoc = new Location(world, 57, 93, -295.5);
        TextDisplay sewer = world.spawn(sewerLoc, TextDisplay.class);
        sewer.setRotation(180,0);
        sewer.setText("Corrupted Sewer" +
                "\n\n" +
                "Difficulty: Easy" +
                "\n\n" +
                "Recommended Players: 1");

        new BukkitRunnable(){
            final Set<Player> affected = new HashSet<>();
            @Override
            public void run(){

                Set<Player> hitByBox = new HashSet<>();

                BoundingBox hitBox = new BoundingBox(
                        sewerLoc.getX() - 3,
                        sewerLoc.getY() - 3,
                        sewerLoc.getZ() - 3,
                        sewerLoc.getX() + 3,
                        sewerLoc.getY() + 3,
                        sewerLoc.getZ() + 3
                );

                for (Entity entity : world.getNearbyEntities(hitBox)){
                    if(entity instanceof Player){
                        hitByBox.add((Player)entity);
                    }
                }

                for(Player thisPlayer : hitByBox){
                    if(!affected.contains(thisPlayer)){
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mysticainteractions sewerenter " + thisPlayer.getName());
                        affected.add(thisPlayer);
                    }
                }

                affected.removeIf(thisPlayer -> !hitByBox.contains(thisPlayer));

            }
        }.runTaskTimer(main, 0, 20);

        Location lindwyrmLoc = new Location(world, 618, 102, -89);
        TextDisplay lindwyrm = world.spawn(lindwyrmLoc, TextDisplay.class);
        lindwyrm.setRotation(90,0);
        lindwyrm.setText("Cave of the Lindwyrm" +
                "\n\n" +
                "Difficulty: Easy" +
                "\n\n" +
                "Recommended Players: 5");

        new BukkitRunnable(){
            final Set<Player> affected = new HashSet<>();
            @Override
            public void run(){

                Set<Player> hitByBox = new HashSet<>();

                BoundingBox hitBox = new BoundingBox(
                        lindwyrmLoc.getX() - 3,
                        lindwyrmLoc.getY() - 3,
                        lindwyrmLoc.getZ() - 3,
                        lindwyrmLoc.getX() + 3,
                        lindwyrmLoc.getY() + 3,
                        lindwyrmLoc.getZ() + 3
                );

                for (Entity entity : world.getNearbyEntities(hitBox)){
                    if(entity instanceof Player){
                        hitByBox.add((Player)entity);
                    }
                }

                for(Player thisPlayer : hitByBox){
                    if(!affected.contains(thisPlayer)){
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mysticainteractions lindwyrmenter " + thisPlayer.getName());
                        affected.add(thisPlayer);
                    }
                }

                affected.removeIf(thisPlayer -> !hitByBox.contains(thisPlayer));

            }
        }.runTaskTimer(main, 0, 20);

    }

    public Location getNearestLocation(Player player){
        Location current = player.getLocation();

        List<Location> locations = new ArrayList<>();
        locations.add(stonemont());
        locations.add(caveOfLindwyrm());
        locations.add(windbluff());
        locations.add(outpost());

        double minDistance = Double.MAX_VALUE;
        Location closestLocation = null;

        for (Location location : locations) {
            double distance = current.distanceSquared(location);
            if (distance < minDistance) {
                minDistance = distance;
                closestLocation = location;
            }
        }

        return closestLocation;
    }

    public Location stonemont(){
        return new Location(Bukkit.getWorld("world"), 74, 100, -357, 90, 0);
    }

    public Location caveOfLindwyrm(){
        return new Location(Bukkit.getWorld("world"), 549, 94, -121, 0, 0);
    }

    public Location windbluff(){
        return new Location(Bukkit.getWorld("world"), -124, 70, 113, 0, 0);
    }

    public Location outpost(){
        return new Location(Bukkit.getWorld("world"), -104, 72, 375, -90, 0);
    }

}
