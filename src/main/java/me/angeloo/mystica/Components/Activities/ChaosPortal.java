package me.angeloo.mystica.Components.Activities;


import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

public class ChaosPortal {

    private final Location location;
    private final Entity portal;
    private final TextDisplay text;

    public ChaosPortal(Location location, Entity portal, TextDisplay text){
        this.location = location;
        this.portal = portal;
        this.text = text;
    }

    public Location getLocation(){
        return location;
    }

    public Entity getPortal(){
        return portal;
    }

    public TextDisplay getText(){
        return text;
    }

    public void despawn(){
        portal.remove();
        text.remove();
    }

}
