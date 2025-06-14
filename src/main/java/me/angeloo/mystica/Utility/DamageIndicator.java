package me.angeloo.mystica.Utility;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;



import javax.swing.text.html.parser.Entity;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class DamageIndicator {

    private final ProtocolManager protocolManager;

    public DamageIndicator(Mystica main){
        protocolManager = main.getProtocolManager();;
    }

    public void displayDamage(Player player, LivingEntity entity, Double damage){



    }



}
