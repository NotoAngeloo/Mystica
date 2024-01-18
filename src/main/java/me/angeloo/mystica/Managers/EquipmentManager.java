package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassEquipment.*;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EquipmentManager {

    private final ProfileManager profileManager;


    public EquipmentManager(Mystica main){
        profileManager = main.getProfileManager();

    }


    //upgrade and reforge are two things i need to accomplish

    //perhaps make this return the upgrade
    public void upgrade(ItemStack equipment, int newLevel){

        //example, catalyst +3 magic +18 health base stats per level

        //get the level first, then increase it


    }

    public void reforge(Player player, ItemStack equipment){

        boolean magic = profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("mystic")
                || profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("elementalist");

        ItemMeta meta = equipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;

        boolean hasReforgeStats = false;


        int level = 0;
        String levelRegex = ".*\\b(?i:level:)\\s*(\\d+).*";
        Pattern levelPattern = Pattern.compile(levelRegex);
        for(String lore : lores){
            String colorlessString = lore.replaceAll("§.", "");
            Matcher levelMatcher = levelPattern.matcher(colorlessString);
            if(levelMatcher.matches()){
                level = Integer.parseInt(levelMatcher.group(1));
                continue;
            }

            if(!lore.startsWith("§") && !lore.equalsIgnoreCase("")){
                hasReforgeStats = true;
            }
        }

        ItemStack newItem = equipment.clone();
        ItemMeta newMeta = newItem.getItemMeta();
        assert newMeta != null;

        if(hasReforgeStats){


            for (NamespacedKey key : newMeta.getPersistentDataContainer().getKeys()) {
                newMeta.getPersistentDataContainer().remove(key);
            }


            List<String> editedLore = new ArrayList<>();

            int index = 0;
            for(String lore : lores){

                if(lore.startsWith("§")){
                    editedLore.add(lore);
                }

                if(lore.equalsIgnoreCase("") && lores.get(index+1).startsWith("§")){
                    editedLore.add(lore);
                }

                index++;
            }

            newMeta.setLore(editedLore);
            newItem.setItemMeta(newMeta);

        }

        List<String> newLores = newMeta.getLore();

        int whichLine = 0;
        String requiresRegex = "(?i)requires ";
        Pattern requiresPattern = Pattern.compile(requiresRegex);
        assert newLores != null;
        for(String lore : newLores){
            String colorlessString = lore.replaceAll("§.", "");
            Matcher requiresMatcher = requiresPattern.matcher(colorlessString);
            if(requiresMatcher.find()){
                whichLine = newLores.indexOf(lore) - 1;
            }
        }


        //Bukkit.getLogger().info("line " + whichLine);


        int offenceCounter = 0;
        int critCounter = 0;
        int healthCounter = 0;
        int manaCounter = 0;
        int defenseCounter = 0;
        int magicDefenseCounter = 0;
        int regenCounter = 0;
        int manaRegenCounter = 0;
        int skillCounter = 0;

        List<String> availableStats = new ArrayList<>();
        availableStats.add("offense");
        availableStats.add("crit");
        availableStats.add("health");
        availableStats.add("mana");
        availableStats.add("defense");
        availableStats.add("magic defense");
        availableStats.add("regen");
        availableStats.add("mana regen");
        availableStats.add("skill");

        int randomStatAmount = new Random().nextInt(6);
        for (int i = 0; i<=randomStatAmount; i++) {

            Collections.shuffle(availableStats);
            String stat = availableStats.get(0);
            //Bukkit.getLogger().info(stat);
            switch (stat.toLowerCase()){
                case "offense":{
                    offenceCounter++;
                    if(offenceCounter==2){
                        availableStats.remove("offense");
                    }
                    break;
                }
                case "crit":{
                    critCounter++;
                    if(critCounter==2){
                        availableStats.remove("crit");
                    }
                    break;
                }
                case "health":{
                    healthCounter++;
                    if(healthCounter==2){
                        availableStats.remove("health");
                    }
                    break;
                }
                case "mana":{
                    manaCounter++;
                    if(manaCounter==2){
                        availableStats.remove("mana");
                    }
                    break;
                }
                case "defense":{
                    defenseCounter++;
                    if(defenseCounter==2){
                        availableStats.remove("defense");
                    }
                    break;
                }
                case "magic defense":{
                    magicDefenseCounter++;
                    if(magicDefenseCounter==2){
                        availableStats.remove("magic defense");
                    }
                    break;
                }
                case "regen":{
                    regenCounter++;
                    if(regenCounter==2){
                        availableStats.remove("regen");
                    }
                    break;
                }
                case "mana regen":{
                    manaRegenCounter++;
                    if(manaRegenCounter==2){
                        availableStats.remove("mana regen");
                    }
                    break;
                }
                case "skill":{
                    skillCounter++;
                    if(skillCounter==2){
                        availableStats.remove("skill");
                    }
                    break;
                }
            }

        }

        //add these values to namespace keys instead
        List<String> newRandomStats = new ArrayList<>();
        newRandomStats.add("");
        PersistentDataContainer statRolls = newMeta.getPersistentDataContainer();

        for(int i=0;i<offenceCounter;i++){
            int statPercent = new Random().nextInt(101);

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "offense_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statPercent);

            if(magic){
                newRandomStats.add("Magic + " + statCalculatorOffenseDefense(level, statPercent));
            }
            else{
                newRandomStats.add("Attack + " + statCalculatorOffenseDefense(level, statPercent));
            }

        }

        for(int i=0;i<critCounter;i++){
            int statPercent = new Random().nextInt(101);

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "crit_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statPercent);

            newRandomStats.add("Crit + " + statCalculatorCrit(level, statPercent));
        }

        for(int i=0;i<healthCounter;i++){
            int statPercent = new Random().nextInt(101);

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "health_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statPercent);

            newRandomStats.add("Health + " + statCalculatorHealthMana(level, statPercent));
        }

        for(int i=0;i<manaCounter;i++){
            int statPercent = new Random().nextInt(101);

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "mana_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statPercent);

            newRandomStats.add("Mana + " + statCalculatorHealthMana(level, statPercent));
        }

        for(int i=0;i<defenseCounter;i++){
            int statPercent = new Random().nextInt(101);

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "defense_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statPercent);

            newRandomStats.add("Defense + " + statCalculatorOffenseDefense(level, statPercent));
        }

        for(int i=0;i<magicDefenseCounter;i++){
            int statPercent = new Random().nextInt(101);

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "magic_defense_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statPercent);

            newRandomStats.add("Magic Defense + " + statCalculatorOffenseDefense(level, statPercent));
        }

        for(int i=0;i<regenCounter;i++){
            int statPercent = new Random().nextInt(101);

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "regen_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statPercent);

            newRandomStats.add("Regen + " + statCalculatorRegen(level, statPercent));
        }

        for(int i=0;i<manaRegenCounter;i++){
            int statPercent = new Random().nextInt(101);

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "mana_regen_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statPercent);

            newRandomStats.add("Mana Regen + " + statCalculatorRegen(level, statPercent));
        }

        for(int i=0;i<skillCounter;i++){
            int statAmount = new Random().nextInt(5) + 1;

            int skillNumber = new Random().nextInt(8) + 1;

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "skill_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statAmount);

            newRandomStats.add("Skill " + skillNumber + " + " + statAmount);
        }

        //Bukkit.getLogger().info(String.valueOf(newRandomStats));

        newLores.addAll(whichLine, newRandomStats);
        newMeta.setLore(newLores);
        newItem.setItemMeta(newMeta);


        //do something else with this later
        player.getInventory().addItem(newItem);


        //(attack/magic) health, mana, defense, magic defense, regen, mana regen, crit, skill level.

        //each stat can be rolled a maximum of twice. skill level rolls another number to determine which skill

        //each rolls a number between 1-100 for percentage, this value is stores as a namespacekey
        //skill level instead rolls a number 1-5. this stays the same and is not upgraded with each upgrade



    }

    //offense/defense 1-5 base. +2 min, +4 max per level
    //crit 1-5 base. +1 min +2 max per level
    //health/mana 5-10 base. +2 min +4 max per level
    //regen/mana regen. 1-3 base. +1 min +2 max per level
    //level 1-5 flat

    private int statCalculatorOffenseDefense(int level, double percent){

        level--;

        double minBase = 1;
        double maxBase = 5;

        minBase += 2*level;
        maxBase += 4*level;

        int rawStat;

        double convert = (percent * ((maxBase-minBase) / 100)) + minBase;

        rawStat = (int) convert;

        return rawStat;
    }

    private int statCalculatorCrit(int level, double percent){

        level--;

        double minBase = 1;
        double maxBase = 5;

        minBase += level;
        maxBase += 2*level;

        int rawStat;

        double convert = (percent * ((maxBase-minBase) / 100)) + minBase;

        rawStat = (int) convert;

        return rawStat;
    }

    private int statCalculatorHealthMana(int level, double percent){

        level--;

        double minBase = 5;
        double maxBase = 10;

        minBase += 2*level;
        maxBase += 4*level;

        int rawStat;

        double convert = (percent * ((maxBase-minBase) / 100)) + minBase;

        rawStat = (int) convert;

        return rawStat;
    }

    private int statCalculatorRegen(int level, double percent){

        level--;

        double minBase = 1;
        double maxBase = 3;

        minBase += level;
        maxBase += 2*level;

        int rawStat;

        double convert = (percent * ((maxBase-minBase) / 100)) + minBase;

        rawStat = (int) convert;

        return rawStat;
    }

}
