package me.angeloo.mystica.Managers;

import io.r2dbc.spi.Parameter;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FirstClearManager {

    private final Mystica main;
    private final YamlConfiguration config = new YamlConfiguration();
    private final File dataFolder;

    private final Map<String, List<Integer>> firstClears = new HashMap<>();

    public FirstClearManager(Mystica main){
        this.main = main;
        this.dataFolder = main.getDataFolder();
    }

    public boolean getIfBossHasBeenClearedAtThisLevel(String name, int level){

        if(firstClears.containsKey(name)){

            List<Integer> bossLevelClears = firstClears.get(name);

            return bossLevelClears.contains(level);
        }

        return false;
    }

    public void createOrLoadFolder() {
        String folderName = "firstclears";
        String fileName = folderName + ".yml";

        File folder = new File(dataFolder, folderName);
        File file = new File(folder, fileName);

        if (!file.exists()) {
            folder.mkdirs();
            main.saveResource(folderName + "/" + fileName, false);
            Bukkit.getLogger().info("Making a new yml file");
        } else {
            Bukkit.getLogger().info("Loading " + fileName);
        }

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            Bukkit.getLogger().info("Error loading " + fileName);
            exception.printStackTrace();
        }

        // Parse existing data from the file
        ConfigurationSection section = config.getConfigurationSection("");
        if (section != null) {
            for (String bossName : section.getKeys(false)) {
                ConfigurationSection bossSection = section.getConfigurationSection(bossName);
                if (bossSection != null) {
                    List<Integer> firstClearsForBoss = new ArrayList<>();
                    for (String level : bossSection.getKeys(false)) {
                        firstClearsForBoss.add(Integer.parseInt(level));

                        List<String> playerNames = bossSection.getStringList(level);

                        // Do something with playerNames later
                    }
                    firstClears.put(bossName, firstClearsForBoss);
                }
            }
        }
    }

    public void markCleared(String name, int level, Set<Player> players) {
        String folderName = "firstclears";
        String fileName = folderName + ".yml";

        File folder = new File(dataFolder, folderName);
        File file = new File(folder, fileName);

        // Load existing data from the file
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            Bukkit.getLogger().info("Error loading " + fileName);
            exception.printStackTrace();
        }

        // Update firstClears map with the new cleared level
        List<Integer> clears = firstClears.computeIfAbsent(name, k -> new ArrayList<>());
        clears.add(level);

        // Update the player names for the specific boss level
        config.set(name + "." + level, players.stream().map(Player::getName).collect(Collectors.toList()));

        // Save the updated data to the file
        try {
            config.save(file);
        } catch (IOException exception) {
            Bukkit.getLogger().info("Error saving " + fileName);
            exception.printStackTrace();
        }
    }


    /*public void createOrLoadFolder(){
        String fileName = "firstclears.yml";

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

        ConfigurationSection section = config.getConfigurationSection("");
        if (section != null) {
            for (String bossName : section.getKeys(false)) {
                ConfigurationSection bossSection = section.getConfigurationSection(bossName);
                if (bossSection != null) {
                    List<Integer> firstClearsForBoss = new ArrayList<>();
                    for (String level : bossSection.getKeys(false)) {
                        firstClearsForBoss.add(Integer.parseInt(level));

                        List<String> playerNames = bossSection.getStringList(level);

                        //do something with this later
                    }
                    firstClears.put(bossName, firstClearsForBoss);

                }
            }
        }

    }




    public void markCleared(String name, int level, Set<Player> players) {
        String fileName = "firstclears.yml";
        File file = new File(dataFolder, fileName);

        // Load existing data from the file
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            Bukkit.getLogger().info("Error loading " + fileName);
            exception.printStackTrace();
        }

        List<Integer> clears = firstClears.computeIfAbsent(name, k -> new ArrayList<>());

        // Add the new level to the list of clears
        clears.add(level);

        // Update the player names for the specific boss level
        config.set(name + "." + level, players.stream().map(Player::getName).collect(Collectors.toList()));

        // Save the updated data to the file
        try {
            config.save(file);
        } catch (IOException exception) {
            Bukkit.getLogger().info("Error saving " + fileName);
            exception.printStackTrace();
        }
    }*/



}
