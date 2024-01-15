package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GearReader {

    private final ProfileManager profileManager;

    public GearReader(Mystica main){
        profileManager = main.getProfileManager();
    }

    public void setGearStats(Player player){

        boolean ignoreMana = profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("shadow knight");

        int attack = 0;
        int magic = 0;
        int health = 0;
        int mana = 0;
        int regen = 0;
        int mana_regen = 0;
        int defense = 0;
        int magic_defense = 0;
        int crit = 0;

        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        ItemStack weapon = equipment.getWeapon();
        ItemStack offHand = equipment.getOffhand();
        ItemStack helmet = equipment.getHelmet();
        ItemStack chestPlate = equipment.getChestPlate();
        ItemStack leggings = equipment.getLeggings();
        ItemStack boots = equipment.getBoots();

        String[] valid = {"attack","magic","health","mana","regen","mana regen","defense","magic defense","crit"};
        String regex = ".*?((?i:" + String.join("|", valid) + ")\\s*\\+\\s*(\\d+)).*";
        Pattern pattern = Pattern.compile(regex);

        if(weapon!=null){
            ItemMeta meta = weapon.getItemMeta();
            assert meta != null;
            List<String> lores = meta.getLore();
            assert lores != null;
            for(String lore : lores){
                Matcher matcher = pattern.matcher(lore);
                if (!matcher.matches()) {
                    continue;
                }
                String stat = matcher.group(1);
                stat = stat.replaceAll("\\+\\s*\\d+", "").trim();
                int amount = Integer.parseInt(matcher.group(2));

                switch (stat.toLowerCase()){
                    case "attack":{
                        attack+=amount;
                        break;
                    }
                    case "magic":{
                        magic+=amount;
                        break;
                    }
                    case "health":{
                        health+=amount;
                        break;
                    }
                    case "mana":{
                        mana+=amount;
                        break;
                    }
                    case "regen":{
                        regen+=amount;
                        break;
                    }
                    case "mana regen":{
                        mana_regen+=amount;
                        break;
                    }
                    case "defense":{
                        defense+=amount;
                        break;
                    }
                    case "magic defense":{
                        magic_defense+=amount;
                        break;
                    }
                    case "crit":{
                        crit+=amount;
                        break;
                    }
                }
            }
        }

        if(offHand!=null){
            ItemMeta meta = offHand.getItemMeta();
            assert meta != null;
            List<String> lores = meta.getLore();
            assert lores != null;
            for(String lore : lores){
                Matcher matcher = pattern.matcher(lore);
                if (!matcher.matches()) {
                    continue;
                }
                String stat = matcher.group(1);
                stat = stat.replaceAll("\\+\\s*\\d+", "").trim();
                int amount = Integer.parseInt(matcher.group(2));

                switch (stat.toLowerCase()){
                    case "attack":{
                        attack+=amount;
                        break;
                    }
                    case "magic":{
                        magic+=amount;
                        break;
                    }
                    case "health":{
                        health+=amount;
                        break;
                    }
                    case "mana":{
                        mana+=amount;
                        break;
                    }
                    case "regen":{
                        regen+=amount;
                        break;
                    }
                    case "mana regen":{
                        mana_regen+=amount;
                        break;
                    }
                    case "defense":{
                        defense+=amount;
                        break;
                    }
                    case "magic defense":{
                        magic_defense+=amount;
                        break;
                    }
                    case "crit":{
                        crit+=amount;
                        break;
                    }
                }
            }
        }

        if(helmet!=null){
            ItemMeta meta = helmet.getItemMeta();
            assert meta != null;
            List<String> lores = meta.getLore();
            assert lores != null;
            for(String lore : lores){
                Matcher matcher = pattern.matcher(lore);
                if (!matcher.matches()) {
                    continue;
                }
                String stat = matcher.group(1);
                stat = stat.replaceAll("\\+\\s*\\d+", "").trim();
                int amount = Integer.parseInt(matcher.group(2));

                switch (stat.toLowerCase()){
                    case "attack":{
                        attack+=amount;
                        break;
                    }
                    case "magic":{
                        magic+=amount;
                        break;
                    }
                    case "health":{
                        health+=amount;
                        break;
                    }
                    case "mana":{
                        mana+=amount;
                        break;
                    }
                    case "regen":{
                        regen+=amount;
                        break;
                    }
                    case "mana regen":{
                        mana_regen+=amount;
                        break;
                    }
                    case "defense":{
                        defense+=amount;
                        break;
                    }
                    case "magic defense":{
                        magic_defense+=amount;
                        break;
                    }
                    case "crit":{
                        crit+=amount;
                        break;
                    }
                }
            }
        }

        if(chestPlate!=null){
            ItemMeta meta = chestPlate.getItemMeta();
            assert meta != null;
            List<String> lores = meta.getLore();
            assert lores != null;
            for(String lore : lores){
                Matcher matcher = pattern.matcher(lore);
                if (!matcher.matches()) {
                    continue;
                }
                String stat = matcher.group(1);
                stat = stat.replaceAll("\\+\\s*\\d+", "").trim();
                int amount = Integer.parseInt(matcher.group(2));

                switch (stat.toLowerCase()){
                    case "attack":{
                        attack+=amount;
                        break;
                    }
                    case "magic":{
                        magic+=amount;
                        break;
                    }
                    case "health":{
                        health+=amount;
                        break;
                    }
                    case "mana":{
                        mana+=amount;
                        break;
                    }
                    case "regen":{
                        regen+=amount;
                        break;
                    }
                    case "mana regen":{
                        mana_regen+=amount;
                        break;
                    }
                    case "defense":{
                        defense+=amount;
                        break;
                    }
                    case "magic defense":{
                        magic_defense+=amount;
                        break;
                    }
                    case "crit":{
                        crit+=amount;
                        break;
                    }
                }
            }
        }

        if(leggings!=null){
            ItemMeta meta = leggings.getItemMeta();
            assert meta != null;
            List<String> lores = meta.getLore();
            assert lores != null;
            for(String lore : lores){
                Matcher matcher = pattern.matcher(lore);
                if (!matcher.matches()) {
                    continue;
                }
                String stat = matcher.group(1);
                stat = stat.replaceAll("\\+\\s*\\d+", "").trim();
                int amount = Integer.parseInt(matcher.group(2));

                switch (stat.toLowerCase()){
                    case "attack":{
                        attack+=amount;
                        break;
                    }
                    case "magic":{
                        magic+=amount;
                        break;
                    }
                    case "health":{
                        health+=amount;
                        break;
                    }
                    case "mana":{
                        mana+=amount;
                        break;
                    }
                    case "regen":{
                        regen+=amount;
                        break;
                    }
                    case "mana regen":{
                        mana_regen+=amount;
                        break;
                    }
                    case "defense":{
                        defense+=amount;
                        break;
                    }
                    case "magic defense":{
                        magic_defense+=amount;
                        break;
                    }
                    case "crit":{
                        crit+=amount;
                        break;
                    }
                }
            }
        }

        if(boots!=null){
            ItemMeta meta = boots.getItemMeta();
            assert meta != null;
            List<String> lores = meta.getLore();
            assert lores != null;
            for(String lore : lores){
                Matcher matcher = pattern.matcher(lore);
                if (!matcher.matches()) {
                    continue;
                }
                String stat = matcher.group(1);
                stat = stat.replaceAll("\\+\\s*\\d+", "").trim();
                int amount = Integer.parseInt(matcher.group(2));

                switch (stat.toLowerCase()){
                    case "attack":{
                        attack+=amount;
                        break;
                    }
                    case "magic":{
                        magic+=amount;
                        break;
                    }
                    case "health":{
                        health+=amount;
                        break;
                    }
                    case "mana":{
                        mana+=amount;
                        break;
                    }
                    case "regen":{
                        regen+=amount;
                        break;
                    }
                    case "mana regen":{
                        mana_regen+=amount;
                        break;
                    }
                    case "defense":{
                        defense+=amount;
                        break;
                    }
                    case "magic defense":{
                        magic_defense+=amount;
                        break;
                    }
                    case "crit":{
                        crit+=amount;
                        break;
                    }
                }
            }
        }

        if(ignoreMana){
            mana = 0;
            mana_regen = 0;
        }

        profileManager.getAnyProfile(player).getGearStats().setAllGearStats(attack,magic,health,mana,regen,mana_regen,defense,magic_defense,crit);

    }

}
