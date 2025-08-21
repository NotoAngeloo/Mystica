package me.angeloo.mystica.Utility.Hud;

import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossWarningSender {


    private final Map<String, String> warningCache = new HashMap<>();

    private final Map<Character, String> characterStringMap = Map.<Character, String>ofEntries(
            Map.entry(' ', "\uE2DF"), Map.entry('!',"\uE280"), Map.entry('"',"\uE281"), Map.entry('#',"\uE282"),
            Map.entry('$', "\uE283"), Map.entry('%', "\uE284"), Map.entry('&', "\uE285"), Map.entry('\'', "\uE286"),
            Map.entry('(', "\uE287"), Map.entry(')', "\uE288"), Map.entry('*', "\uE289"), Map.entry('+', "\uE28A"),
            Map.entry(',', "\uE28B"), Map.entry('-', "\uE28C"), Map.entry('.', "\uE28D"), Map.entry('/', "\uE28E"),
            Map.entry('0', "\uE28F"), Map.entry('1', "\uE290"), Map.entry('2', "\uE291"), Map.entry('3', "\uE292"),
            Map.entry('4', "\uE293"), Map.entry('5', "\uE294"), Map.entry('6', "\uE295"), Map.entry('7', "\uE296"),
            Map.entry('8', "\uE297"), Map.entry('9', "\uE298"), Map.entry(':', "\uE299"), Map.entry(';', "\uE29A"),
            Map.entry('<', "\uE29B"), Map.entry('=', "\uE29C"), Map.entry('>', "\uE29D"), Map.entry('?', "\uE29E"),
            Map.entry('@', "\uE29F"), Map.entry('A', "\uE2A0"), Map.entry('B', "\uE2A1"), Map.entry('C', "\uE2A2"),
            Map.entry('D', "\uE2A3"), Map.entry('E', "\uE2A4"), Map.entry('F', "\uE2A5"), Map.entry('G', "\uE2A6"),
            Map.entry('H', "\uE2A7"), Map.entry('I', "\uE2A8"), Map.entry('J', "\uE2A9"), Map.entry('K', "\uE2AA"),
            Map.entry('L', "\uE2AB"), Map.entry('M', "\uE2AC"), Map.entry('N', "\uE2AD"), Map.entry('O', "\uE2AE"),
            Map.entry('P', "\uE2AF"), Map.entry('Q', "\uE2B0"), Map.entry('R', "\uE2B1"), Map.entry('S', "\uE2B2"),
            Map.entry('T', "\uE2B3"), Map.entry('U', "\uE2B4"), Map.entry('V', "\uE2B5"), Map.entry('W', "\uE2B6"),
            Map.entry('X', "\uE2B7"), Map.entry('Y', "\uE2B8"), Map.entry('Z', "\uE2B9"), Map.entry('[', "\uE2BA"),
            Map.entry('\\', "\uE2BB"), Map.entry(']', "\uE2BC"), Map.entry('^', "\uE2BD"), Map.entry('_', "\uE2BE"),
            Map.entry('`', "\uE2BF"), Map.entry('a', "\uE2C0"), Map.entry('b', "\uE2C1"), Map.entry('c', "\uE2C2"),
            Map.entry('d', "\uE2C3"), Map.entry('e', "\uE2C4"), Map.entry('f', "\uE2C5"), Map.entry('g', "\uE2C6"),
            Map.entry('h', "\uE2C7"), Map.entry('i', "\uE2C8"), Map.entry('j', "\uE2C9"), Map.entry('k', "\uE2CA"),
            Map.entry('l', "\uE2CB"), Map.entry('m', "\uE2CC"), Map.entry('n', "\uE2CD"), Map.entry('o', "\uE2CE"),
            Map.entry('p', "\uE2CF"), Map.entry('q', "\uE2D0"), Map.entry('r', "\uE2D1"), Map.entry('s', "\uE2D2"),
            Map.entry('t', "\uE2D3"), Map.entry('u', "\uE2D4"), Map.entry('v', "\uE2D5"), Map.entry('w', "\uE2D6"),
            Map.entry('x', "\uE2D7"), Map.entry('y', "\uE2D8"), Map.entry('z', "\uE2D9"), Map.entry('{', "\uE2DA"),
            Map.entry('|', "\uE2DB"), Map.entry('}', "\uE2DC"), Map.entry('~', "\uE2DD")
    );


    private final Mystica main;

    private final BossWarning defaultWarning = new BossWarning(" ", true);

    private final Map<UUID, BossWarning> warningMap = new HashMap<>();
    private final Map<UUID, BukkitTask> removalTaskMap = new HashMap<>();

    public BossWarningSender(Mystica main){
        this.main = main;
    }

    public String getWarning(Player player){

        if(warningMap.containsKey(player.getUniqueId())){
            return warningMap.get(player.getUniqueId()).getWarning();
        }

        return " ";
    }


    //make so some cannot be overwritten
    public void setWarning(Player player, String string, int time, boolean overridable){

        if(removalTaskMap.containsKey(player.getUniqueId())){

            if(warningMap.containsKey(player.getUniqueId())){
                BossWarning warning = warningMap.get(player.getUniqueId());
                if(!warning.getIfOverridable()){
                    return;
                }
            }

            removalTaskMap.get(player.getUniqueId()).cancel();
            removalTaskMap.remove(player.getUniqueId());
        }

        if(warningCache.containsKey(string)){
            string = warningCache.get(string);

            BossWarning warning = new BossWarning(string, overridable);

            warningMap.put(player.getUniqueId(), warning);
            BukkitTask task = new BukkitRunnable(){
                @Override
                public void run(){
                    removeWarning(player);
                    removalTaskMap.remove(player.getUniqueId());
                }

            }.runTaskLaterAsynchronously(main, time);

            removalTaskMap.put(player.getUniqueId(), task);
            return;
        }

        StringBuilder text = new StringBuilder();
        //left banner
        text.append("\uE2DE");

        //-1
        text.append("\uF801");

        //for each letter, get from a pre saved map
        for(char c : string.toCharArray()){
            text.append(getChar(c));
            //-1
            text.append("\uF801");

            if(c=='i'||c=='!'||c=='l'){
                //-1
                text.append("\uF801");
            }
        }


        //right banner
        text.append("\uE2F0");






        warningCache.put(string, String.valueOf(text));

        string = String.valueOf(text);

        BossWarning warning = new BossWarning(string, overridable);

        //remove after time
        warningMap.put(player.getUniqueId(), warning);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                removeWarning(player);
                removalTaskMap.remove(player.getUniqueId());
            }

        }.runTaskLaterAsynchronously(main, time);

        removalTaskMap.put(player.getUniqueId(), task);

    }

    public void removeWarning(Player player){
        warningMap.put(player.getUniqueId(), defaultWarning);
    }

    //make it blank later
    private String getChar(char c){
        return characterStringMap.getOrDefault(c, "");
    }



}
