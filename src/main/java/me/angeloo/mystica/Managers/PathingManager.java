package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.bukkit.Color;
import org.bukkit.Particle.DustOptions;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;


public class PathingManager {

    private final Mystica main;
    private final YamlConfiguration config = new YamlConfiguration();
    private final File dataFolder;

    private List<Location> paths = new ArrayList<>();

    private final Map<UUID, Location> destinations = new HashMap<>();
    private final Map<UUID, List<Location>> playerPaths = new HashMap<>();
    private final Map<UUID, BukkitTask> displayTask = new HashMap<>();

    public PathingManager(Mystica main){
        this.main = main;
        this.dataFolder = main.getDataFolder();
    }

    public void createOrLoadFolder(){

        String fileName = "pathing.yml";

        File file = new File(dataFolder, fileName);

        if(!file.exists()){
            file.getParentFile().mkdirs();
            main.saveResource(fileName, false);
            Bukkit.getLogger().info("Making a new yml file");
        }
        else{
            Bukkit.getLogger().info("Loading " + fileName);
        }
        try{
            config.load(file);
        } catch (IOException | InvalidConfigurationException exception){
            Bukkit.getLogger().info("Error loading " + fileName);
            exception.printStackTrace();
        }

        paths = (List<Location>) config.getList("paths", new ArrayList<Location>());

    }

    public void saveFolder(){

        config.set("paths", paths);

        String fileName = "pathing.yml";
        File file = new File(dataFolder, fileName);

        try {
            config.save(file);
            Bukkit.getLogger().info("Saved pathing file");
        } catch (IOException exception) {
            Bukkit.getLogger().info("Error saving pathing file");
            exception.printStackTrace();
        }
    }

    public void setDestination(Player player, Location destination){
        destinations.put(player.getUniqueId(), destination);
        calculatePath(player);
    }

    private void calculatePath(Player player){

        List<Location> paths = this.paths;
        List<Location> calculatedPath = new ArrayList<>();

        if(paths.size() == 0){
            Bukkit.getLogger().info("paths is empty");
            return;
        }

        Location start = player.getLocation();
        Block blockStart = player.getWorld().getBlockAt(start);
        start = blockStart.getLocation().subtract(0,1,0);
        Location destination = destinations.get(player.getUniqueId());
        Block blockEnd = player.getWorld().getBlockAt(destination);
        destination = blockEnd.getLocation().subtract(0,1,0);
        Location finalStart = start;
        paths.sort(Comparator.comparingDouble(l -> l.distance(finalStart)));
        Location pathStart = paths.get(0);
        Location finalDestination = destination;
        paths.sort(Comparator.comparingDouble(l -> l.distance(finalDestination)));
        Location pathEnd = paths.get(0);


        Location current = start.clone();
        Vector directionToPath = pathStart.toVector().subtract(current.toVector()).setY(0);
        boolean nearPath = false;
        while (!nearPath){

            current.add(directionToPath.normalize().multiply(1));
            //need to make sure 1. the block at the location is not air, 2. the block above is air
            boolean validBlock = true;
            while (validBlock){

                Block currentBlock = current.getBlock();
                Block blockAbove = current.clone().add(0,1,0).getBlock();

                if(currentBlock.getType().isAir()){
                    current.subtract(0,1,0);
                    continue;
                }

                if(!blockAbove.getType().isAir()){
                    current.add(0,1,0);
                    continue;
                }

                validBlock = false;
            }

            double distanceToPath = pathStart.distance(current);

            if(distanceToPath<=1){
                nearPath=true;
                continue;
            }

            calculatedPath.add(current.clone());
        }

        current = destination.clone();
        directionToPath = pathEnd.toVector().subtract(current.toVector()).setY(0);
        nearPath = false;
        while (!nearPath){

            current.add(directionToPath.normalize().multiply(1));
            //need to make sure 1. the block at the location is not air, 2. the block above is air
            boolean validBlock = true;
            while (validBlock){

                Block currentBlock = current.getBlock();
                Block blockAbove = current.clone().add(0,1,0).getBlock();

                if(currentBlock.getType().isAir()){
                    current.subtract(0,1,0);
                    continue;
                }

                if(!blockAbove.getType().isAir()){
                    current.add(0,1,0);
                    continue;
                }

                validBlock = false;
            }

            double distanceToPath = pathEnd.distance(current);

            if(distanceToPath<=1){
                nearPath=true;
                continue;
            }

            calculatedPath.add(current.clone());
        }


       Location currentPath = pathEnd.clone();

        int radius = 1;
        while (currentPath.distance(pathStart) > 1){
            List<Location> valid = new ArrayList<>();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <=radius; z++) {
                        Location blockLocation = currentPath.clone().add(x, y, z);
                        Block block = player.getWorld().getBlockAt(blockLocation);
                        blockLocation = block.getLocation();

                        if (paths.contains(blockLocation) && !calculatedPath.contains(blockLocation)) {
                            valid.add(blockLocation);
                        }
                    }
                }
            }


            if(valid.isEmpty()){
                Bukkit.getLogger().info("error, path interrupted");
                return;
            }

            valid.sort(Comparator.comparingDouble(l -> l.distance(pathStart)));

            currentPath = valid.get(0);
            calculatedPath.add(currentPath);

        }





        for(Location loc : calculatedPath){
            player.spawnParticle(Particle.REDSTONE, loc.clone().add(0,1,0), 20, .1, .1, .1, 0, new DustOptions(Color.ORANGE, 2.0F));
        }
    }

    /*public void toggleDisplayingPaths(Player player){

        if(displayTask.containsKey(player.getUniqueId())){
            displayTask.get(player.getUniqueId()).cancel();
            displayTask.remove(player.getUniqueId());
            return;
        }

        BukkitTask task = new BukkitRunnable(){

            @Override
            public void run(){
                //.getLogger().info("test");
                displayNearbyPaths(player);
            }

        }.runTaskTimer(main, 0, 20);

        displayTask.put(player.getUniqueId(), task);

    }*/

    public void displayAllNearbyPaths(Player player){

        Location current = player.getLocation();

        Set<Location> nearby = new HashSet<>();

        int radius = 20;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location blockLocation = current.clone().add(x, y, z);
                    Block block = player.getWorld().getBlockAt(blockLocation);
                    blockLocation = block.getLocation();

                    if (blockLocation.distance(current) <= radius && !block.getType().isAir()) {
                        nearby.add(blockLocation);
                    }
                }
            }
        }


        //instead, run a calculation to determine which line should be displayed

        for(Location nearbyLocation : nearby){
            if(paths.contains(nearbyLocation)){
                player.spawnParticle(Particle.REDSTONE, nearbyLocation.add(0,1,0), 20, .1, .1, .1, 0, new DustOptions(Color.ORANGE, 2.0F));
            }
        }

    }

    public void createPath(Location location){

        if(!paths.contains(location)){
            paths.add(location);
        }

    }

    public void deletePath(Location location){
        paths.remove(location);
    }

}
