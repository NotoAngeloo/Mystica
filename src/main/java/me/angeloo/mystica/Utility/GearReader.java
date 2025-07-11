package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

        int attack = 0;
        int health = 0;
        int defense = 0;
        int magic_defense = 0;
        int crit = 0;

        int skill_1 = 0;
        int skill_2 = 0;
        int skill_3 = 0;
        int skill_4 = 0;
        int skill_5 = 0;
        int skill_6 = 0;
        int skill_7 = 0;
        int skill_8 = 0;

        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        ItemStack weapon = null;
        if(equipment.getWeapon() != null){
            weapon = equipment.getWeapon().build();
        }


        ItemStack helmet = null;
        if(equipment.getHelmet() != null){
            helmet = equipment.getHelmet().build();
        }

        ItemStack chestPlate = null;

        if(equipment.getChestPlate() != null){
            chestPlate = equipment.getChestPlate().build();
        }

        ItemStack leggings = null;

        if(equipment.getLeggings() != null){
            leggings = equipment.getLeggings().build();
        }

        ItemStack boots = null;

        if(equipment.getBoots() != null){
            boots = equipment.getBoots().build();
        }


        String[] valid = {"attack","health","defense","magic defense","crit","skill \\d+"};
        String regex = ".*?((?i:" + String.join("|", valid) + ")\\s*\\+\\s*(\\d+)).*";
        Pattern pattern = Pattern.compile(regex);

        if(weapon!=null && !weapon.getType().equals(Material.AIR)){
            
            ItemMeta meta = weapon.getItemMeta();
            assert meta != null;
            List<String> lores = meta.getLore();
            assert lores != null;
            for(String lore : lores){
                //Bukkit.getLogger().info(lore);
                Matcher matcher = pattern.matcher(lore);
                if (!matcher.matches()) {
                    continue;
                }
                String stat = matcher.group(1);
                stat = stat.replaceAll("\\+\\s*\\d+", "").trim();

                //Bukkit.getLogger().info(stat);

                int amount = Integer.parseInt(matcher.group(2));

                //Bukkit.getLogger().info(String.valueOf(amount));

                switch (stat.toLowerCase()){
                    case "attack":{
                        attack+=amount;
                        break;
                    }
                    case "crit":{
                        crit+=amount;
                        break;
                    }
                    case "health":{
                        health+=amount;
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
                    case "skill 1":{
                        skill_1+=amount;
                        break;
                    }
                    case "skill 2":{
                        skill_2+=amount;
                        break;
                    }
                    case "skill 3":{
                        skill_3+=amount;
                        break;
                    }
                    case "skill 4":{
                        skill_4+=amount;
                        break;
                    }
                    case "skill 5":{
                        skill_5+=amount;
                        break;
                    }
                    case "skill 6":{
                        skill_6+=amount;
                        break;
                    }
                    case "skill 7":{
                        skill_7+=amount;
                        break;
                    }
                    case "skill 8":{
                        skill_8+=amount;
                        break;
                    }
                }
            }
        }

        if(helmet!=null && !helmet.getType().equals(Material.AIR)){
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
                    case "health":{
                        health+=amount;
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
                    case "skill 1":{
                        skill_1+=amount;
                        break;
                    }
                    case "skill 2":{
                        skill_2+=amount;
                        break;
                    }
                    case "skill 3":{
                        skill_3+=amount;
                        break;
                    }
                    case "skill 4":{
                        skill_4+=amount;
                        break;
                    }
                    case "skill 5":{
                        skill_5+=amount;
                        break;
                    }
                    case "skill 6":{
                        skill_6+=amount;
                        break;
                    }
                    case "skill 7":{
                        skill_7+=amount;
                        break;
                    }
                    case "skill 8":{
                        skill_8+=amount;
                        break;
                    }
                }
            }
        }

        if(chestPlate!=null && !chestPlate.getType().equals(Material.AIR)){
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
                    case "health":{
                        health+=amount;
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
                    case "skill 1":{
                        skill_1+=amount;
                        break;
                    }
                    case "skill 2":{
                        skill_2+=amount;
                        break;
                    }
                    case "skill 3":{
                        skill_3+=amount;
                        break;
                    }
                    case "skill 4":{
                        skill_4+=amount;
                        break;
                    }
                    case "skill 5":{
                        skill_5+=amount;
                        break;
                    }
                    case "skill 6":{
                        skill_6+=amount;
                        break;
                    }
                    case "skill 7":{
                        skill_7+=amount;
                        break;
                    }
                    case "skill 8":{
                        skill_8+=amount;
                        break;
                    }
                }
            }
        }

        if(leggings!=null && !leggings.getType().equals(Material.AIR)){
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
                    case "health":{
                        health+=amount;
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
                    case "skill 1":{
                        skill_1+=amount;
                        break;
                    }
                    case "skill 2":{
                        skill_2+=amount;
                        break;
                    }
                    case "skill 3":{
                        skill_3+=amount;
                        break;
                    }
                    case "skill 4":{
                        skill_4+=amount;
                        break;
                    }
                    case "skill 5":{
                        skill_5+=amount;
                        break;
                    }
                    case "skill 6":{
                        skill_6+=amount;
                        break;
                    }
                    case "skill 7":{
                        skill_7+=amount;
                        break;
                    }
                    case "skill 8":{
                        skill_8+=amount;
                        break;
                    }
                }
            }
        }

        if(boots!=null && !boots.getType().equals(Material.AIR)){
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
                    case "health":{
                        health+=amount;
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
                    case "skill 1":{
                        skill_1+=amount;
                        break;
                    }
                    case "skill 2":{
                        skill_2+=amount;
                        break;
                    }
                    case "skill 3":{
                        skill_3+=amount;
                        break;
                    }
                    case "skill 4":{
                        skill_4+=amount;
                        break;
                    }
                    case "skill 5":{
                        skill_5+=amount;
                        break;
                    }
                    case "skill 6":{
                        skill_6+=amount;
                        break;
                    }
                    case "skill 7":{
                        skill_7+=amount;
                        break;
                    }
                    case "skill 8":{
                        skill_8+=amount;
                        break;
                    }
                }
            }
        }


        profileManager.getAnyProfile(player).getGearStats().setAllGearStats(attack,health,defense,magic_defense,crit);
        profileManager.getAnyProfile(player).getSkillLevels().setAllSkillLevelBonus(skill_1,skill_2,skill_3,skill_4,skill_5,skill_6,skill_7,skill_8);

    }

}
