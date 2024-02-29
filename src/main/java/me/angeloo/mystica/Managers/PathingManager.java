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

            Location blockLocAtCurrent = current.getBlock().getLocation();

            calculatedPath.add(blockLocAtCurrent.clone());
        }

        //calulate a line from the destination to the end of the path
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

            Location blockLocAtCurrent = current.getBlock().getLocation();

            calculatedPath.add(blockLocAtCurrent.clone());
        }


        Location source = pathStart.clone();

        Set<Location> extremities = new HashSet<>();
        Set<Set<Location>> crossroadPaths = new HashSet<>();
        Set<Location> checked = new HashSet<>();
        Set<Location> invalid = new HashSet<>();
        Set<Location> pathBetweenStartAndEnd = new HashSet<>();

        extremities.add(source);
        while (true){

            Set<Location> changedExtremities = new HashSet<>(extremities);

            if(changedExtremities.isEmpty()){
                Bukkit.getLogger().info("error, no valid path");
                break;
            }

            for(Location thisLoc : changedExtremities){
                checked.add(thisLoc);
                extremities.remove(thisLoc);
                Set<Location> neighbors = getNeighbors(player, thisLoc);
                neighbors.removeAll(checked);
                extremities.addAll(neighbors);

                if(neighbors.size() >= 2){
                    for(Location neighbor : neighbors){
                        Set<Location> newCrossroadPath = new HashSet<>();
                        newCrossroadPath.add(neighbor);
                        crossroadPaths.add(newCrossroadPath);
                    }
                }

                if(neighbors.size() == 1){
                    for(Set<Location> specificPath : crossroadPaths){
                        if(specificPath.contains(thisLoc)){
                            specificPath.addAll(neighbors);
                        }
                    }

                }

                if(neighbors.isEmpty()){
                    //Bukkit.getLogger().info("dead end");

                    Set<Set<Location>> allPathsItsIn = new HashSet<>();

                    for(Set<Location> specificPath : crossroadPaths){
                        if(specificPath.contains(thisLoc)){
                            allPathsItsIn.add(specificPath);
                        }
                    }

                    Set<Location> uniqueLocations = new HashSet<>();
                    Set<Location> commonLocations = new HashSet<>();

                    for(Set<Location> oneOfThePaths : allPathsItsIn){
                        for (Location location : oneOfThePaths) {
                            // If the location is not in the common locations set, add it to the unique locations set
                            if (!commonLocations.contains(location)) {
                                if (!uniqueLocations.add(location)) {
                                    uniqueLocations.remove(location); // Remove if it's already in uniqueLocations
                                    commonLocations.add(location); // Add to commonLocations
                                }
                            }
                        }
                    }

                    invalid.addAll(uniqueLocations);
                }

                if(neighbors.contains(pathEnd)){
                    pathBetweenStartAndEnd.addAll(checked);
                    pathBetweenStartAndEnd.removeAll(invalid);
                    break;
                }

            }

        }

        calculatedPath.addAll(pathBetweenStartAndEnd);

        for(Location loc : calculatedPath){
            player.spawnParticle(Particle.REDSTONE, loc.clone().add(0,1,0), 20, .1, .1, .1, 0, new DustOptions(Color.ORANGE, 2.0F));
        }
    }

    private Set<Location> getNeighbors(Player player, Location origin){
        int radius = 1;
        Set<Location> neighbors = new HashSet<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <=radius; z++) {
                    Location blockLocation = origin.clone().add(x, y, z);
                    Block block = player.getWorld().getBlockAt(blockLocation);
                    blockLocation = block.getLocation();
                    if (paths.contains(blockLocation)) {
                        neighbors.add(blockLocation);
                    }
                }
            }
        }
        neighbors.remove(origin);
        return neighbors;
    }


    public void calculatePathOld(Player player, Location destination){

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

        Location current = start.clone();
        Vector directionToPath = pathStart.toVector().subtract(current.toVector()).setY(0);
        Location finalCurrent = current;
        Vector finalDirectionToPath = directionToPath;
        new BukkitRunnable(){
            @Override
            public void run(){
                finalCurrent.add(finalDirectionToPath.normalize().multiply(1));
                boolean validBlock = true;
                while (validBlock){

                    Block currentBlock = finalCurrent.getBlock();
                    Block blockAbove = finalCurrent.clone().add(0,1,0).getBlock();

                    if(currentBlock.getType().isAir()){
                        finalCurrent.subtract(0,1,0);
                        continue;
                    }

                    if(!blockAbove.getType().isAir()){
                        finalCurrent.add(0,1,0);
                        continue;
                    }

                    validBlock = false;
                }

                double distanceToPath = pathStart.distance(finalCurrent);

                if(distanceToPath<=1){
                    this.cancel();
                    return;
                }

                Location blockLocAtCurrent = finalCurrent.getBlock().getLocation();
                calculatedPath.add(blockLocAtCurrent.clone());

            }


        }.runTaskTimerAsynchronously(main,0,1);

        current = destination.clone();
        directionToPath = pathEnd.toVector().subtract(current.toVector()).setY(0);
        Location finalCurrent1 = current;
        Vector finalDirectionToPath1 = directionToPath;
        new BukkitRunnable(){
            @Override
            public void run(){
                finalCurrent1.add(finalDirectionToPath1.normalize().multiply(1));

                boolean validBlock = true;
                while (validBlock){

                    Block currentBlock = finalCurrent1.getBlock();
                    Block blockAbove = finalCurrent1.clone().add(0,1,0).getBlock();

                    if(currentBlock.getType().isAir()){
                        finalCurrent1.subtract(0,1,0);
                        continue;
                    }

                    if(!blockAbove.getType().isAir()){
                        finalCurrent1.add(0,1,0);
                        continue;
                    }

                    validBlock = false;
                }

                double distanceToPath = pathEnd.distance(finalCurrent1);

                if(distanceToPath<=1){
                    this.cancel();
                    return;
                }

                Location blockLocAtCurrent = finalCurrent1.getBlock().getLocation();

                calculatedPath.add(blockLocAtCurrent.clone());

            }
        }.runTaskTimerAsynchronously(main, 0,1);


        final Location[] currentPath = {pathEnd.clone()};
        int radius = 1;
        new BukkitRunnable(){
            @Override
            public void run(){
                List<Location> valid = new ArrayList<>();
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <=radius; z++) {
                            Location blockLocation = currentPath[0].clone().add(x, y, z);
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
                    Bukkit.getLogger().info("interrupted at " + currentPath[0]);
                    this.cancel();
                    return;
                }


                valid.sort(Comparator.comparingDouble(l -> l.distance(pathStart)));
                calculatedPath.add(valid.get(0));
                currentPath[0] = valid.get(0);

                if(currentPath[0].distance(pathStart) < 1){
                    this.cancel();
                    destinations.put(player.getUniqueId(), finalDestination);
                    playerPaths.put(player.getUniqueId(), calculatedPath);
                    startPathDisplayTask(player);
                }
            }
        }.runTaskTimerAsynchronously(main,0,20);



        //only put after all the tasks are done



        /*for(Location loc : calculatedPath){
            player.spawnParticle(Particle.REDSTONE, loc.clone().add(0,1,0), 20, .1, .1, .1, 0, new DustOptions(Color.ORANGE, 2.0F));
        }*/
    }

    private void startPathDisplayTask(Player player){

        /*if(displayTask.containsKey(player.getUniqueId())){
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
        }*/
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
