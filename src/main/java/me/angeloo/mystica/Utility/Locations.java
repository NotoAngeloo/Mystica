package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
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
        sewer.setCustomName("sewer");


        Location lindwyrmLoc = new Location(world, 618, 102, -89);
        TextDisplay lindwyrm = world.spawn(lindwyrmLoc, TextDisplay.class);
        lindwyrm.setRotation(90,0);
        lindwyrm.setText("Cave of the Lindwyrm" +
                "\n\n" +
                "Difficulty: Easy" +
                "\n\n" +
                "Recommended Players: 5");

        lindwyrm.setCustomName("lindwyrm_cave");

        Location hoLeeDungeonLoc = new Location(world, -74, 71, 452.5);
        TextDisplay hoLeeDungeon = world.spawn(hoLeeDungeonLoc, TextDisplay.class);
        hoLeeDungeon.setRotation(90, 0);
        hoLeeDungeon.setText("Test of Might" +
                "\n\n" +
                "Difficulty: Hard" +
                "\n\n" +
                "Recommended" +
                "\n\n" +
                "Players: 10");
        hoLeeDungeon.setCustomName("ho_lee_dungeon");


        Location teleportLindwyrm = new Location(world, 549, 94, -124);
        TextDisplay lindwyrmPortal = world.spawn(teleportLindwyrm, TextDisplay.class);
        lindwyrmPortal.setCustomName("teleport");

        Location teleportOutpost = new Location(world, -107, 72, 376);
        TextDisplay outpostPortal = world.spawn(teleportOutpost, TextDisplay.class);
        outpostPortal.setCustomName("teleport");

        Location teleportWindbluff = new Location(world, -124, 70, 110);
        TextDisplay windbluffPortal = world.spawn(teleportWindbluff, TextDisplay.class);
        windbluffPortal.setCustomName("teleport");

        Location teleportStonemont = new Location(world, 76, 100, -357);
        TextDisplay stonemontPortal = world.spawn(teleportStonemont, TextDisplay.class);
        stonemontPortal.setCustomName("teleport");

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
