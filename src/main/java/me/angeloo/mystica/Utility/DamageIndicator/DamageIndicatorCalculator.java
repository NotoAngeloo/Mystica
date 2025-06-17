package me.angeloo.mystica.Utility.DamageIndicator;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.Random;


public class DamageIndicatorCalculator {

    private static final Random RANDOM = new Random();


    public DamageIndicatorCalculator(){
    }

    public void displayDamage(Player player, LivingEntity entity, Double damage, boolean crit){

        //depending on crit or not, change the color of the string

        double xOffset = 1.25d;
        double yOffset = 0.65d;
        double zOffset = 1.25d;

        Location targetLocation = entity.getLocation().add(2 * xOffset * RANDOM.nextDouble() - xOffset, yOffset - 0.2d, 2*zOffset*RANDOM.nextDouble() - zOffset);

        double scale = 10;
        double scaledDamage = Math.ceil(damage * scale) / scale;

        DamageIndicator.getInstance().spawnDamageIndicator(player, targetLocation, scaledDamage, getFormat(scaledDamage, crit));


    }

    public String getFormat(double damage, boolean crit){

        StringBuilder damageNumbers = new StringBuilder();

        damageNumbers.append("42069");

        return String.valueOf(damageNumbers);
    }



}
