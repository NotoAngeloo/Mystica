package me.angeloo.mystica.Utility.DamageIndicator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class DamageIndicator {

    private static DamageIndicator instance;

    private final JavaPlugin plugin;

    public DamageIndicator(JavaPlugin plugin){
        instance = this;
        this.plugin = plugin;

    }

    public JavaPlugin getPlugin(){
        return plugin;
    }

    //location of player, location of entity, amount,

    public abstract Entity spawnDamageIndicator(Player player, Location location, double damage, String format);


    public static DamageIndicator getInstance(){
        return instance;
    }

}
