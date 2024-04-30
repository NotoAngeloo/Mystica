package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static me.angeloo.mystica.Mystica.levelColor;
import static me.angeloo.mystica.Mystica.questColor;


public class DailyData {


    private int maxLevel = 1;

    private int daysTilIncrease = 0;

    private final Mystica main;
    private final YamlConfiguration config = new YamlConfiguration();
    private final File dataFolder;


    public DailyData(Mystica main){
        this.main = main;
        this.dataFolder = main.getDataFolder();
    }

    public void createOrLoadFolder(){
        String subfolderName = "dailydata"; // Subfolder name

        File subfolder = new File(dataFolder, subfolderName); // Path to the subfolder
        if (!subfolder.exists()) {
            subfolder.mkdirs(); // Create the subfolder if it doesn't exist
        }

        String fileName = "dailydata.yml";
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
            maxLevel = config.getInt("max_level");
            daysTilIncrease = config.getInt("days_til_increase");

            if(maxLevel == 0){
                maxLevel = 1;
            }


        } catch (IOException | InvalidConfigurationException exception) {
            Bukkit.getLogger().info("Error loading " + fileName);
            exception.printStackTrace();
        }
    }

    public void saveFolder(){
        String subfolderName = "dailydata"; // Subfolder name
        File subfolder = new File(dataFolder, subfolderName); // Path to the subfolder
        if (!subfolder.exists()) {
            subfolder.mkdirs(); // Create the subfolder if it doesn't exist
        }

        String fileName = "dailydata.yml";
        File file = new File(subfolder, fileName); // Path to the YAML file inside the subfolder

        config.set("max_level", maxLevel);
        config.set("days_til_increase", daysTilIncrease);

        try {
            config.save(file);
        } catch (IOException exception) {
            Bukkit.getLogger().info("Error saving daily file");
            exception.printStackTrace();
        }
    }


    public int getDaysTilIncrease(){
        return daysTilIncrease;
    }

    public void decreaseDays(){
        daysTilIncrease--;
    }

    public void resetDayClock(int newDays){
        daysTilIncrease = newDays;
    }

    public int getMaxLevel(){
        return maxLevel;
    }

    public void increaseMaxLevel(){
        maxLevel++;
    }

    public String getLevelAnnouncement(){
        return ChatColor.of(questColor) + "Max level is currently " + ChatColor.of(levelColor) + maxLevel + ChatColor.GRAY + " (" + daysTilIncrease + " days until increased)";
    }

}
