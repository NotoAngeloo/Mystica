package me.angeloo.mystica.Utility.DamageIndicator;

import net.md_5.bungee.api.ChatColor;
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

        String damageAsString = String.valueOf(damage);

        if(crit){
            damageNumbers.append("\uE02E");

            for(char c : damageAsString.toCharArray()){

                switch (c) {
                    case '0' -> {
                        damageNumbers.append("\uE039");
                    }
                    case '1' -> {
                        damageNumbers.append("\uE03A");
                    }
                    case '2' -> {
                        damageNumbers.append("\uE03B");
                    }
                    case '3' -> {
                        damageNumbers.append("\uE03C");
                    }
                    case '4' -> {
                        damageNumbers.append("\uE03D");
                    }
                    case '5' -> {
                        damageNumbers.append("\uE03E");
                    }
                    case '6' -> {
                        damageNumbers.append("\uE03F");
                    }
                    case '7' -> {
                        damageNumbers.append("\uE048");
                    }
                    case '8' -> {
                        damageNumbers.append("\uE04C");
                    }
                    case '9' -> {
                        damageNumbers.append("\uE04D");
                    }
                }

            }
        }
        else{
            for(char c : damageAsString.toCharArray()){

                switch (c) {
                    case '0' -> {
                        damageNumbers.append("\uE02F");
                    }
                    case '1' -> {
                        damageNumbers.append("\uE030");
                    }
                    case '2' -> {
                        damageNumbers.append("\uE031");
                    }
                    case '3' -> {
                        damageNumbers.append("\uE032");
                    }
                    case '4' -> {
                        damageNumbers.append("\uE033");
                    }
                    case '5' -> {
                        damageNumbers.append("\uE034");
                    }
                    case '6' -> {
                        damageNumbers.append("\uE035");
                    }
                    case '7' -> {
                        damageNumbers.append("\uE036");
                    }
                    case '8' -> {
                        damageNumbers.append("\uE037");
                    }
                    case '9' -> {
                        damageNumbers.append("\uE038");
                    }
                }

            }
        }






        return String.valueOf(damageNumbers);
    }



}
