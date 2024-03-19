package me.angeloo.mystica.Managers;

import io.r2dbc.spi.Parameter;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FirstClearManager {

    private final Mystica main;
    private final YamlConfiguration config = new YamlConfiguration();
    private final File dataFolder;

    private final Map<String, List<Integer>> firstClears = new HashMap<>();

    public FirstClearManager(Mystica main){
        this.main = main;
        this.dataFolder = main.getDataFolder();
    }

    public void createOrLoadFolder(){
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

        //apply to map from file here
        ConfigurationSection section = config.getConfigurationSection("");
        if (section != null) {
            for (String bossName : section.getKeys(false)) {
                List<Integer> firstClearsForBoss = new ArrayList<>(section.getIntegerList(bossName));
                firstClears.put(bossName, firstClearsForBoss);
            }
        }

    }

    public void saveFolder(){

        for(String bossName : firstClears.keySet()){
            config.set(bossName, firstClears.get(bossName));
        }

        String fileName = "firstclears.yml";
        File file = new File(dataFolder, fileName);

        try {
            config.save(file);
        } catch (IOException exception) {
            Bukkit.getLogger().info("Error saving firstclears file");
            exception.printStackTrace();
        }

    }

    public boolean getIfBossHasBeenClearedAtThisLevel(String name, int level){

        if(firstClears.containsKey(name)){

            List<Integer> bossLevelClears = firstClears.get(name);

            return bossLevelClears.contains(level);
        }

        return false;
    }

    public void markCleared(String name, int level){

        List<Integer> clears = new ArrayList<>();

        if(firstClears.containsKey(name)){
            clears = firstClears.get(name);
        }

        clears.add(level);

        firstClears.put(name, clears);

    }


}
