package me.angeloo.mystica.Utility.DamageIndicator;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;


public class DamageIndicatorCalculator {

    private static final Random RANDOM = new Random();

    //0-9
    private final String[] critNumbers = {"\uE039","\uE03A","\uE03B","\uE03C","\uE03D","\uE03E","\uE03F","\uE048","\uE04C","\uE04D"};

    //0-9
    private final String[] nonCritNumbers = {"\uE02F","\uE030","\uE031","\uE032","\uE033","\uE034","\uE035","\uE036","\uE037","\uE038"};

    public DamageIndicatorCalculator(){
    }

    public void displayDamage(Player player, LivingEntity entity, Double damage, boolean crit){

        double xOffset = 1.25d;
        double yOffset = 0.65d;
        double zOffset = 1.25d;

        Location targetLocation = entity.getLocation().add(2 * xOffset * RANDOM.nextDouble() - xOffset, yOffset - 0.2d, 2*zOffset*RANDOM.nextDouble() - zOffset);

        //my addition to make it appears in front of target always intead of on top
        Location playerloc = player.getLocation();
        double distance = playerloc.distance(targetLocation);
        if(distance != 0){
            Vector direction = playerloc.toVector().subtract(targetLocation.toVector());
            direction.normalize().multiply(1);
            targetLocation = targetLocation.add(direction);
        }


        /*double scale = 10;
        double scaledDamage = Math.ceil(damage * scale) / scale;*/
        int damageInt = (int) Math.ceil(damage);

        DamageIndicator.getInstance().spawnDamageIndicator(player, targetLocation, damage, getFormat(damageInt, crit));


    }

    public String getFormat(int damage, boolean crit){

        StringBuilder damageNumbers = new StringBuilder();

        String damageAsString = String.valueOf(damage);

        if(crit){
            damageNumbers.append("\uE02E");

            for(char c : damageAsString.toCharArray()){
                int i = Integer.parseInt(String.valueOf(c));
                damageNumbers.append(critNumbers[i]);
            }
        }
        else{
            for(char c : damageAsString.toCharArray()){
                int i = Integer.parseInt(String.valueOf(c));
                damageNumbers.append(nonCritNumbers[i]);
            }
        }






        return String.valueOf(damageNumbers);
    }



}
