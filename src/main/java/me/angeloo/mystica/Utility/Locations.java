package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;


public class Locations {

    private final Mystica main;

    public Locations(Mystica main){
        this.main = main;
    }

    public void initializeLocationals(){

        World world = Bukkit.getWorld("world");
        assert world != null;

        /*Location spawnLoc = new Location(world,409,68,-564,25,5);
        TextDisplay spawn = world.spawn(spawnLoc, TextDisplay.class);
        spawn.setCustomName("spawn");

        /*Location sewerLoc = new Location(world, 57, 93, -295.5);
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


         */
    }

    
}
