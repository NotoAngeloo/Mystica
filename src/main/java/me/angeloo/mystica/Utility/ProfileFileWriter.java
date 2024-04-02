package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ProfileFileWriter {

    private final Mystica main;

    private final File dataFolder;

    public ProfileFileWriter(Mystica main) {
        this.main = main;
        this.dataFolder = main.getDataFolder();
    }

    public YamlConfiguration createOrLoadProfileFile(UUID playerId) {
        String folderName = "playerdata";
        String fileName = playerId.toString() + ".yml";

        File folder = new File(dataFolder, folderName);
        File file = new File(folder, fileName);
        YamlConfiguration profileConfig = new YamlConfiguration();

        if (!file.exists()) {
            try {
                folder.mkdirs();
                file.createNewFile();
                main.saveResource(folderName + "/" + "default-profile.yml", false);
                //main.saveResource(folderName + "/" + fileName, false); // Adjust the resource path here
                //Bukkit.getLogger().info("Created a new profile file for " + playerId);
            } catch (IOException exception) {
                //Bukkit.getLogger().info("Error creating profile file for " + playerId);
                exception.printStackTrace();
            }
        }

        try {
            profileConfig.load(file);
            //Bukkit.getLogger().info("Loaded profile file for " + playerId);
        } catch (IOException | InvalidConfigurationException exception) {
            //Bukkit.getLogger().info("Error loading profile file for " + playerId);
            exception.printStackTrace();
        }

        return profileConfig;
    }

    public void saveProfileFile(UUID playerId, YamlConfiguration profileConfig) {
        String folderName = "playerdata";
        String fileName = playerId.toString() + ".yml";

        File folder = new File(dataFolder, folderName);
        File file = new File(folder, fileName);

        try {
            profileConfig.save(file);
            Bukkit.getLogger().info("Saved profile file for " + playerId);
        } catch (IOException exception) {
            Bukkit.getLogger().info("Error saving profile file for " + playerId);
            exception.printStackTrace();
        }
    }

    /*public YamlConfiguration createOrLoadProfileFile(UUID playerId) {
        String fileName = playerId.toString() + ".yml"; // Use UUID as file name

        File file = new File(dataFolder, fileName);
        YamlConfiguration profileConfig = new YamlConfiguration();

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                main.saveResource("default-profile.yml", false); // Adjust the resource path here
                //Bukkit.getLogger().info("Created a new profile file for " + playerId);
            } catch (IOException exception) {
                //Bukkit.getLogger().info("Error creating profile file for " + playerId);
                exception.printStackTrace();
            }
        }

        try {
            profileConfig.load(file);
            //Bukkit.getLogger().info("Loaded profile file for " + playerId);
        } catch (IOException | InvalidConfigurationException exception) {
            //Bukkit.getLogger().info("Error loading profile file for " + playerId);
            exception.printStackTrace();
        }

        return profileConfig;
    }

    public void saveProfileFile(UUID playerId, YamlConfiguration profileConfig) {
        String fileName = playerId.toString() + ".yml";
        File file = new File(dataFolder, fileName);

        try {
            profileConfig.save(file);
            Bukkit.getLogger().info("Saved profile file for " + playerId);
        } catch (IOException exception) {
            Bukkit.getLogger().info("Error saving profile file for " + playerId);
            exception.printStackTrace();
        }
    }*/
}
