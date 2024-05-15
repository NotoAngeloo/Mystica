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
import org.bukkit.scheduler.BukkitRunnable;
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
        String subfolderName = "paths"; // Subfolder name

        File subfolder = new File(dataFolder, subfolderName); // Path to the subfolder
        if (!subfolder.exists()) {
            subfolder.mkdirs(); // Create the subfolder if it doesn't exist
        }

        String fileName = "pathing.yml";
        File file = new File(subfolder, fileName); // Path to the YAML file inside the subfolder

        if (!file.exists()) {
            // If the file doesn't exist, save the default resource
            main.saveResource(subfolderName + "/" + fileName, false);
            Bukkit.getLogger().info("Making a new yml file");
        } else {
            Bukkit.getLogger().info("Loading " + fileName);
        }

        try {
            config.load(file);
            paths = (List<Location>) config.getList("paths", new ArrayList<>());
        } catch (IOException | InvalidConfigurationException exception) {
            Bukkit.getLogger().info("Error loading " + fileName);
            exception.printStackTrace();
        }
    }

    public void saveFolder(){
        String subfolderName = "paths"; // Subfolder name
        File subfolder = new File(dataFolder, subfolderName); // Path to the subfolder
        if (!subfolder.exists()) {
            subfolder.mkdirs(); // Create the subfolder if it doesn't exist
        }

        String fileName = "pathing.yml";
        File file = new File(subfolder, fileName); // Path to the YAML file inside the subfolder

        config.set("paths", paths);

        try {
            config.save(file);
            //Bukkit.getLogger().info("Saved pathing file");
        } catch (IOException exception) {
            Bukkit.getLogger().info("Error saving pathing file");
            exception.printStackTrace();
        }
    }

    public void calculatePath(Player player, Location destination){

        List<Location> paths = this.paths;
        List<Location> calculatedPath = new ArrayList<>();

        if(paths.size() == 0){
            Bukkit.getLogger().info("paths is empty");
            return;
        }

        Location start = player.getLocation();
        Block blockStart = player.getWorld().getBlockAt(start);
        start = blockStart.getLocation().subtract(0,1,0);
        Block blockEnd = player.getWorld().getBlockAt(destination);
        destination = blockEnd.getLocation().subtract(0,1,0);
        Location finalStart = start;
        paths.sort(Comparator.comparingDouble(l -> l.distance(finalStart)));
        Location pathStart = paths.get(0);
        Location finalDestination = destination;
        paths.sort(Comparator.comparingDouble(l -> l.distance(finalDestination)));
        Location pathEnd = paths.get(0);


        //calculate a line from the player to the start of the path
        Location current = start.clone();
        Vector directionToPath = pathStart.toVector().subtract(current.toVector()).setY(0);
        boolean nearPath = false;
        while (!nearPath){

            double distanceToPath = pathStart.distance(current);

            if(distanceToPath>1){
                current.add(directionToPath.normalize().multiply(1));
            }


            //need to make sure 1. the block at the location is not air, 2. the block above is air
            boolean validBlock = true;
            while (validBlock){

                Block currentBlock = current.getBlock();
                Block blockAbove = current.clone().add(0,1,0).getBlock();

                //was is air
                if(currentBlock.isPassable()){
                    current.subtract(0,1,0);
                    continue;
                }

                if(!blockAbove.isPassable()){
                    current.add(0,1,0);
                    continue;
                }

                validBlock = false;
            }

            distanceToPath = pathStart.distance(current);

            if(distanceToPath<=1){
                nearPath=true;
                continue;
            }

            Location blockLocAtCurrent = current.getBlock().getLocation();

            calculatedPath.add(blockLocAtCurrent.clone());
        }

        //calulate a line from the destination to the end of the path
        current = destination.clone();
        directionToPath = pathEnd.toVector().subtract(current.toVector()).setY(0);
        nearPath = false;
        while (!nearPath){

            double distanceToPath = pathEnd.distance(current);

            if(distanceToPath > 1){
                current.add(directionToPath.normalize().multiply(1));
            }


            boolean validBlock = true;
            while (validBlock){

                Block currentBlock = current.getBlock();
                Block blockAbove = current.clone().add(0,1,0).getBlock();

                if(currentBlock.isPassable()){
                    current.subtract(0,1,0);
                    continue;
                }

                if(!blockAbove.isPassable()){
                    current.add(0,1,0);
                    continue;
                }

                validBlock = false;
            }

            distanceToPath = pathEnd.distance(current);

            if(distanceToPath<=1){
                nearPath=true;
                continue;
            }

            Location blockLocAtCurrent = current.getBlock().getLocation();

            calculatedPath.add(blockLocAtCurrent.clone());
        }

        if(pathStart != pathEnd){
            Set<Location> pathBetweenStartAndEnd = new HashSet<>();
            Set<Set<Location>> branches = new HashSet<>();
            Set<Location> extremities = new HashSet<>();
            Set<Location> checked = new HashSet<>();
            boolean stopChecking = false;

            extremities.add(pathStart);

            while (!extremities.isEmpty() && !stopChecking) {
                Set<Location> nextExtremities = new HashSet<>();

                for (Location loc : extremities) {
                    Set<Location> currentBranch = null;

                    // Find the current branch containing the loc
                    for (Set<Location> branch : branches) {
                        if (branch.contains(loc)) {
                            currentBranch = branch;
                            break;
                        }
                    }

                    // If no current branch found, create a new one
                    if (currentBranch == null) {
                        currentBranch = new HashSet<>();
                        currentBranch.add(loc);
                        branches.add(currentBranch);
                    }

                    checked.add(loc);
                    Set<Location> neighbors = getNeighbors(player, loc);
                    neighbors.removeAll(checked);
                    nextExtremities.addAll(neighbors);

                    if (neighbors.size() >= 2) {
                        // Create a new branch for each neighbor
                        for (Location neighbor : neighbors) {
                            Set<Location> newBranch = new HashSet<>(currentBranch); // Clone the current branch
                            newBranch.add(neighbor); // Add the neighbor to the branch
                            branches.add(newBranch); // Add the new branch to the set of branches
                        }
                    }

                    if (neighbors.size() == 1) {
                        // Extend the current branch
                        currentBranch.addAll(neighbors);
                    }

                    if(neighbors.isEmpty()){
                        //figure out better logic for here
                        //Bukkit.getLogger().info("path interrupted at " + loc);
                    }

                    if (neighbors.contains(pathEnd)) {
                        stopChecking = true;
                        break;
                    }
                }

                extremities = nextExtremities;
            }

// Find the branch that contains both pathStart and pathEnd
            for (Set<Location> branch : branches) {
                if (branch.contains(pathStart) && branch.contains(pathEnd)) {
                    pathBetweenStartAndEnd.addAll(branch);
                    break;
                }
            }

            calculatedPath.addAll(pathBetweenStartAndEnd);
        }


        playerPaths.put(player.getUniqueId(), calculatedPath);
        destinations.put(player.getUniqueId(), destination);
        startPathDisplayTask(player);

    }


    private Set<Location> getNeighbors(Player player, Location origin) {
        int radius = 1;
        Set<Location> neighbors = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x != 0 && z != 0) {
                        // Skip diagonals
                        continue;
                    }
                    // Skip the origin location
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    Location blockLocation = origin.clone().add(x, y, z);
                    Block block = player.getWorld().getBlockAt(blockLocation);
                    blockLocation = block.getLocation();
                    if (paths.contains(blockLocation)) {
                        neighbors.add(blockLocation);
                    }
                }
            }
        }
        return neighbors;
    }


    private void startPathDisplayTask(Player player){

        if(displayTask.containsKey(player.getUniqueId())){
            displayTask.get(player.getUniqueId()).cancel();
        }

        if(!destinations.containsKey(player.getUniqueId())){
            Bukkit.getLogger().info(player.getName() + " destination not set");
            return;
        }

        if(!playerPaths.containsKey(player.getUniqueId())){
            Bukkit.getLogger().info(player.getName() + "path unset");
            return;
        }

        Location destination = destinations.get(player.getUniqueId());
        List<Location> paths = playerPaths.get(player.getUniqueId());

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(player.getWorld() != destination.getWorld()){
                    cancelTask();
                    return;
                }

                double distanceDestination = player.getLocation().distance(destination);

                if(distanceDestination<5){
                    cancelTask();
                    return;
                }

                paths.sort(Comparator.comparingDouble(p -> p.distance(player.getLocation())));
                Location closestPath = paths.get(0);

                double distanceClosestPath = player.getLocation().distance(closestPath);

                if(distanceClosestPath>20){
                    cancelTask();
                    return;
                }

                displayPlayerPath(player);

            }

            private void cancelTask(){
                this.cancel();
                destinations.remove(player.getUniqueId());
                playerPaths.remove(player.getUniqueId());
                displayTask.remove(player.getUniqueId());
            }

        }.runTaskTimer(main, 0, 20);

        displayTask.put(player.getUniqueId(), task);
    }

    private void displayPlayerPath(Player player){

        if(!playerPaths.containsKey(player.getUniqueId())){
            Bukkit.getLogger().info(player.getName() + " doesn't have a path");
            return;
        }

        if(!destinations.containsKey(player.getUniqueId())){
            Bukkit.getLogger().info(player.getName() +" doesn't have a destination");
            return;
        }

        List<Location> playerPath = playerPaths.get(player.getUniqueId());
        Location destination = destinations.get(player.getUniqueId());

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

        double distancePlayerDestination = player.getLocation().distance(destination);

        for(Location nearbyLocation : nearby){

            if(playerPath.contains(nearbyLocation)){
                player.spawnParticle(Particle.REDSTONE, nearbyLocation.add(0,1,0), 20, .1, .1, .1, 0, new DustOptions(Color.ORANGE, 2.0F));
            }
        }

        if(distancePlayerDestination <= 20){
            for(double i = 0; i < 20; i+=.5){
                player.spawnParticle(Particle.REDSTONE, destinations.get(player.getUniqueId()).clone().add(0,i,0), 20, .1, .1, .1, 0, new DustOptions(Color.GREEN, 2.0F));
            }
        }
    }

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
