package me.angeloo.mystica.Utility.DamageIndicator;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.NMS.Common.PacketInterface;
import me.angeloo.mystica.NMS.Tasks.AsyncHologramTask;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class DamageIndicatorApi extends DamageIndicator {

    private final PacketInterface packetInterface;

    public DamageIndicatorApi(Mystica main){
        super(main);

        this.packetInterface = main.getPacketInterface();
    }


    @Override
    public Entity spawnDamageIndicator(Player player, Location location, double damage, String format) {
        Entity armorStand =  packetInterface.spawnHologram(player, location, damage, format, getPlugin());
        ArmorStand entityArmorStand = (ArmorStand) armorStand;
        entityArmorStand.setGravity(true);
        entityArmorStand.setMetadata("_mystica", new FixedMetadataValue(Mystica.getPlugin(), 1));
        entityArmorStand.setArms(false);
        entityArmorStand.setMarker(true);

        //task to remove stands, after a task to sim falling
        AsyncHologramTask.createHologramTask(player, armorStand, location);

        //here just in case i no longer want damage indicators to fall
        //AsyncDestroyTask.createDestroyingTask(player, entityArmorStand);

        return armorStand;
    }


}
