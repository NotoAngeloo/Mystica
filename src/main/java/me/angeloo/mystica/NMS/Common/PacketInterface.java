package me.angeloo.mystica.NMS.Common;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface PacketInterface {

    Entity spawnHologram(Player player, Location location, double damage, String format, Plugin plugin);

    void relEntityMove(Player player, int entityId, double y, double dy, boolean b3);

    void destroyEntity(Player player, int entityId);

    Entity spawnHologram(Location location, double damage, String format, Plugin plugin);

    void destroyEntity(Entity entity);

    String getVersionName();


}
