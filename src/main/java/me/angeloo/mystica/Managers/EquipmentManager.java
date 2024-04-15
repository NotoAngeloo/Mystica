package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassEquipment.*;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.angeloo.mystica.Mystica.menuColor;

public class EquipmentManager {

    private final ProfileManager profileManager;

    private final NoneEquipment noneEquipment;
    private final AssassinEquipment assassinEquipment;
    private final ElementalistEquipment elementalistEquipment;
    private final MysticEquipment mysticEquipment;
    private final PaladinEquipment paladinEquipment;
    private final RangerEquipment rangerEquipment;
    private final ShadowKnightEquipment shadowKnightEquipment;
    private final WarriorEquipment warriorEquipment;

    public EquipmentManager(Mystica main){
        profileManager = main.getProfileManager();
        noneEquipment = new NoneEquipment();
        assassinEquipment = new AssassinEquipment();
        elementalistEquipment = new ElementalistEquipment();
        mysticEquipment = new MysticEquipment();
        paladinEquipment = new PaladinEquipment();
        rangerEquipment = new RangerEquipment();
        shadowKnightEquipment = new ShadowKnightEquipment();
        warriorEquipment = new WarriorEquipment();
    }

    public int getItemLevel(ItemStack equipment){

        ItemMeta meta = equipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;

        int level = 0;
        String levelRegex = ".*\\b(?i:level:)\\s*(\\d+).*";
        Pattern levelPattern = Pattern.compile(levelRegex);
        for(String lore : lores){
            String colorlessString = lore.replaceAll("§.", "");
            Matcher levelMatcher = levelPattern.matcher(colorlessString);
            if(levelMatcher.matches()){
                level = Integer.parseInt(levelMatcher.group(1));
                break;
            }

        }
        return level;
    }

    public ItemStack swap(Player player, ItemStack oldItem) {


        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        ItemMeta selectedMeta = oldItem.getItemMeta();
        assert selectedMeta != null;
        List<String> lore = selectedMeta.getLore();
        assert lore != null;
        String equipSlot = lore.get(1);
        equipSlot = equipSlot.replaceAll("§.", "");

        switch (clazz.toLowerCase()) {
            case "assassin": {
                switch (equipSlot.toLowerCase()){
                    case "main hand":{
                        selectedMeta.setDisplayName(assassinEquipment.getBaseWeapon().getItemMeta().getDisplayName());
                        oldItem.setType(Material.FLINT);
                        break;
                    }
                    case "secondary":{
                        selectedMeta.setDisplayName(assassinEquipment.getBaseOffhand().getItemMeta().getDisplayName());
                        oldItem.setType(Material.FLINT);
                        break;
                    }
                    case "helmet":{
                        selectedMeta.setDisplayName(assassinEquipment.getBaseHelmet().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(7);
                        break;
                    }
                    case "chestplate":{
                        selectedMeta.setDisplayName(assassinEquipment.getBaseChestPlate().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(7);
                        break;
                    }
                    case "leggings":{
                        selectedMeta.setDisplayName(assassinEquipment.getBaseLeggings().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(7);
                        break;
                    }
                    case "boots":{
                        selectedMeta.setDisplayName(assassinEquipment.getBaseBoots().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(7);
                        break;
                    }
                }

                int requireLine = -1;
                for(String loreline : selectedMeta.getLore()){
                    if(loreline.contains("Requires")){
                        requireLine = selectedMeta.getLore().indexOf(loreline);
                        break;
                    }
                }
                lore.set(requireLine, assassinEquipment.getBaseWeapon().getItemMeta().getLore().get(6));
                break;
            }
            case "elementalist": {
                switch (equipSlot.toLowerCase()){
                    case "main hand":{
                        selectedMeta.setDisplayName(elementalistEquipment.getBaseWeapon().getItemMeta().getDisplayName());
                        oldItem.setType(Material.STICK);
                        break;
                    }
                    case "secondary":{
                        selectedMeta.setDisplayName(elementalistEquipment.getBaseOffhand().getItemMeta().getDisplayName());
                        oldItem.setType(Material.STICK);
                        break;
                    }
                    case "helmet":{
                        selectedMeta.setDisplayName(elementalistEquipment.getBaseHelmet().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(1);
                        break;
                    }
                    case "chestplate":{
                        selectedMeta.setDisplayName(elementalistEquipment.getBaseChestPlate().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(1);
                        break;
                    }
                    case "leggings":{
                        selectedMeta.setDisplayName(elementalistEquipment.getBaseLeggings().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(1);
                        break;
                    }
                    case "boots":{
                        selectedMeta.setDisplayName(elementalistEquipment.getBaseBoots().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(1);
                        break;
                    }
                }
                int requireLine = -1;
                for(String loreline : selectedMeta.getLore()){
                    if(loreline.contains("Requires")){
                        requireLine = selectedMeta.getLore().indexOf(loreline);
                        break;
                    }
                }
                lore.set(requireLine, elementalistEquipment.getBaseWeapon().getItemMeta().getLore().get(6));
                break;
            }
            case "mystic": {
                switch (equipSlot.toLowerCase()){
                    case "main hand":{
                        selectedMeta.setDisplayName(mysticEquipment.getBaseWeapon().getItemMeta().getDisplayName());
                        oldItem.setType(Material.BLAZE_ROD);
                        break;
                    }
                    case "secondary":{
                        selectedMeta.setDisplayName(mysticEquipment.getBaseOffhand().getItemMeta().getDisplayName());
                        oldItem.setType(Material.BLAZE_ROD);
                        break;
                    }
                    case "helmet":{
                        selectedMeta.setDisplayName(mysticEquipment.getBaseHelmet().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(3);
                        selectedMeta.setCustomModelData(3);
                        break;
                    }
                    case "chestplate":{
                        selectedMeta.setDisplayName(mysticEquipment.getBaseChestPlate().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(3);
                        break;
                    }
                    case "leggings":{
                        selectedMeta.setDisplayName(mysticEquipment.getBaseLeggings().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(3);
                        break;
                    }
                    case "boots":{
                        selectedMeta.setDisplayName(mysticEquipment.getBaseBoots().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(3);
                        break;
                    }
                }
                int requireLine = -1;
                for(String loreline : selectedMeta.getLore()){
                    if(loreline.contains("Requires")){
                        requireLine = selectedMeta.getLore().indexOf(loreline);
                        break;
                    }
                }
                lore.set(requireLine, mysticEquipment.getBaseWeapon().getItemMeta().getLore().get(6));
                break;
            }
            case "paladin": {
                switch (equipSlot.toLowerCase()){
                    case "main hand":{
                        selectedMeta.setDisplayName(paladinEquipment.getBaseWeapon().getItemMeta().getDisplayName());
                        oldItem.setType(Material.IRON_SWORD);
                        break;
                    }
                    case "secondary":{
                        selectedMeta.setDisplayName(paladinEquipment.getBaseOffhand().getItemMeta().getDisplayName());
                        oldItem.setType(Material.IRON_SWORD);
                        break;
                    }
                    case "helmet":{
                        selectedMeta.setDisplayName(paladinEquipment.getBaseHelmet().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(5);
                        break;
                    }
                    case "chestplate":{
                        selectedMeta.setDisplayName(paladinEquipment.getBaseChestPlate().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(5);
                        break;
                    }
                    case "leggings":{
                        selectedMeta.setDisplayName(paladinEquipment.getBaseLeggings().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(5);
                        break;
                    }
                    case "boots":{
                        selectedMeta.setDisplayName(paladinEquipment.getBaseBoots().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(5);
                        break;
                    }
                }
                int requireLine = -1;
                for(String loreline : selectedMeta.getLore()){
                    if(loreline.contains("Requires")){
                        requireLine = selectedMeta.getLore().indexOf(loreline);
                        break;
                    }
                }
                lore.set(requireLine, paladinEquipment.getBaseWeapon().getItemMeta().getLore().get(6));
                break;
            }
            case "ranger": {
                switch (equipSlot.toLowerCase()){
                    case "main hand":{
                        selectedMeta.setDisplayName(rangerEquipment.getBaseWeapon().getItemMeta().getDisplayName());
                        oldItem.setType(Material.FEATHER);
                        break;
                    }
                    case "secondary":{
                        selectedMeta.setDisplayName(rangerEquipment.getBaseOffhand().getItemMeta().getDisplayName());
                        oldItem.setType(Material.FEATHER);
                        break;
                    }
                    case "helmet":{
                        selectedMeta.setDisplayName(rangerEquipment.getBaseHelmet().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(2);
                        break;
                    }
                    case "chestplate":{
                        selectedMeta.setDisplayName(rangerEquipment.getBaseChestPlate().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(2);
                        break;
                    }
                    case "leggings":{
                        selectedMeta.setDisplayName(rangerEquipment.getBaseLeggings().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(2);
                        break;
                    }
                    case "boots":{
                        selectedMeta.setDisplayName(rangerEquipment.getBaseBoots().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(2);
                        break;
                    }
                }
                int requireLine = -1;
                for(String loreline : selectedMeta.getLore()){
                    if(loreline.contains("Requires")){
                        requireLine = selectedMeta.getLore().indexOf(loreline);
                        break;
                    }
                }
                lore.set(requireLine, rangerEquipment.getBaseWeapon().getItemMeta().getLore().get(6));
                break;
            }
            case "shadow knight": {
                switch (equipSlot.toLowerCase()){
                    case "main hand":{
                        selectedMeta.setDisplayName(shadowKnightEquipment.getBaseWeapon().getItemMeta().getDisplayName());
                        oldItem.setType(Material.DIAMOND_SWORD);
                        break;
                    }
                    case "secondary":{
                        selectedMeta.setDisplayName(shadowKnightEquipment.getBaseOffhand().getItemMeta().getDisplayName());
                        oldItem.setType(Material.DIAMOND_SWORD);
                        break;
                    }
                    case "helmet":{
                        selectedMeta.setDisplayName(shadowKnightEquipment.getBaseHelmet().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(4);
                        break;
                    }
                    case "chestplate":{
                        selectedMeta.setDisplayName(shadowKnightEquipment.getBaseChestPlate().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(4);
                        break;
                    }
                    case "leggings":{
                        selectedMeta.setDisplayName(shadowKnightEquipment.getBaseLeggings().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(4);
                        break;
                    }
                    case "boots":{
                        selectedMeta.setDisplayName(shadowKnightEquipment.getBaseBoots().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(4);
                        break;
                    }
                }
                int requireLine = -1;
                for(String loreline : selectedMeta.getLore()){
                    if(loreline.contains("Requires")){
                        requireLine = selectedMeta.getLore().indexOf(loreline);
                        break;
                    }
                }
                lore.set(requireLine, shadowKnightEquipment.getBaseWeapon().getItemMeta().getLore().get(6));
                break;
            }
            case "warrior": {
                switch (equipSlot.toLowerCase()){
                    case "main hand":{
                        selectedMeta.setDisplayName(warriorEquipment.getBaseWeapon().getItemMeta().getDisplayName());
                        oldItem.setType(Material.BRICK);
                        break;
                    }
                    case "secondary":{
                        selectedMeta.setDisplayName(warriorEquipment.getBaseOffhand().getItemMeta().getDisplayName());
                        oldItem.setType(Material.BRICK);
                        break;
                    }
                    case "helmet":{
                        selectedMeta.setDisplayName(warriorEquipment.getBaseHelmet().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(6);
                        break;
                    }
                    case "chestplate":{
                        selectedMeta.setDisplayName(warriorEquipment.getBaseChestPlate().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(6);
                        break;
                    }
                    case "leggings":{
                        selectedMeta.setDisplayName(warriorEquipment.getBaseLeggings().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(6);
                        break;
                    }
                    case "boots":{
                        selectedMeta.setDisplayName(warriorEquipment.getBaseBoots().getItemMeta().getDisplayName());
                        selectedMeta.setCustomModelData(6);
                        break;
                    }
                }
                int requireLine = -1;
                for(String loreline : selectedMeta.getLore()){
                    if(loreline.contains("Requires")){
                        requireLine = selectedMeta.getLore().indexOf(loreline);
                        break;
                    }
                }
                lore.set(requireLine, warriorEquipment.getBaseWeapon().getItemMeta().getLore().get(6));
                break;
            }
        }

        selectedMeta.setLore(lore);
        oldItem.setItemMeta(selectedMeta);

        return oldItem;
    }

    public ItemStack generate(Player player, int level){

        ItemStack baseGear = new ItemStack(Material.AIR);

        //randomly generate
        int random = new Random().nextInt(6);

        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        switch (clazz.toLowerCase()){
            case "assassin":{
                switch (random){
                    case 0:{
                        baseGear = assassinEquipment.getBaseWeapon();
                        break;
                    }
                    case 1:{
                        baseGear = assassinEquipment.getBaseOffhand();
                        break;
                    }
                    case 2:{
                        baseGear = assassinEquipment.getBaseHelmet();
                        break;
                    }
                    case 3:{
                        baseGear = assassinEquipment.getBaseChestPlate();
                        break;
                    }
                    case 4:{
                        baseGear = assassinEquipment.getBaseLeggings();
                        break;
                    }
                    case 5:{
                        baseGear = assassinEquipment.getBaseBoots();
                        break;
                    }
                }
                break;
            }
            case "elementalist":{
                switch (random){
                    case 0:{
                        baseGear = elementalistEquipment.getBaseWeapon();
                        break;
                    }
                    case 1:{
                        baseGear = elementalistEquipment.getBaseOffhand();
                        break;
                    }
                    case 2:{
                        baseGear = elementalistEquipment.getBaseHelmet();
                        break;
                    }
                    case 3:{
                        baseGear = elementalistEquipment.getBaseChestPlate();
                        break;
                    }
                    case 4:{
                        baseGear = elementalistEquipment.getBaseLeggings();
                        break;
                    }
                    case 5:{
                        baseGear = elementalistEquipment.getBaseBoots();
                        break;
                    }
                }
                break;
            }
            case "mystic":{
                switch (random){
                    case 0:{
                        baseGear = mysticEquipment.getBaseWeapon();
                        break;
                    }
                    case 1:{
                        baseGear = mysticEquipment.getBaseOffhand();
                        break;
                    }
                    case 2:{
                        baseGear = mysticEquipment.getBaseHelmet();
                        break;
                    }
                    case 3:{
                        baseGear = mysticEquipment.getBaseChestPlate();
                        break;
                    }
                    case 4:{
                        baseGear = mysticEquipment.getBaseLeggings();
                        break;
                    }
                    case 5:{
                        baseGear = mysticEquipment.getBaseBoots();
                        break;
                    }
                }
                break;
            }
            case "paladin":{
                switch (random){
                    case 0:{
                        baseGear = paladinEquipment.getBaseWeapon();
                        break;
                    }
                    case 1:{
                        baseGear = paladinEquipment.getBaseOffhand();
                        break;
                    }
                    case 2:{
                        baseGear = paladinEquipment.getBaseHelmet();
                        break;
                    }
                    case 3:{
                        baseGear = paladinEquipment.getBaseChestPlate();
                        break;
                    }
                    case 4:{
                        baseGear = paladinEquipment.getBaseLeggings();
                        break;
                    }
                    case 5:{
                        baseGear = paladinEquipment.getBaseBoots();
                        break;
                    }
                }

                break;
            }
            case "ranger":{
                switch (random){
                    case 0:{
                        baseGear = rangerEquipment.getBaseWeapon();
                        break;
                    }
                    case 1:{
                        baseGear = rangerEquipment.getBaseOffhand();
                        break;
                    }
                    case 2:{
                        baseGear = rangerEquipment.getBaseHelmet();
                        break;
                    }
                    case 3:{
                        baseGear = rangerEquipment.getBaseChestPlate();
                        break;
                    }
                    case 4:{
                        baseGear = rangerEquipment.getBaseLeggings();
                        break;
                    }
                    case 5:{
                        baseGear = rangerEquipment.getBaseBoots();
                        break;
                    }
                }
                break;
            }
            case "shadow knight":{
                switch (random){
                    case 0:{
                        baseGear = shadowKnightEquipment.getBaseWeapon();
                        break;
                    }
                    case 1:{
                        baseGear = shadowKnightEquipment.getBaseOffhand();
                        break;
                    }
                    case 2:{
                        baseGear = shadowKnightEquipment.getBaseHelmet();
                        break;
                    }
                    case 3:{
                        baseGear = shadowKnightEquipment.getBaseChestPlate();
                        break;
                    }
                    case 4:{
                        baseGear = shadowKnightEquipment.getBaseLeggings();
                        break;
                    }
                    case 5:{
                        baseGear = shadowKnightEquipment.getBaseBoots();
                        break;
                    }
                }
                break;
            }
            case "warrior":{
                switch (random){
                    case 0:{
                        baseGear = warriorEquipment.getBaseWeapon();
                        break;
                    }
                    case 1:{
                        baseGear = warriorEquipment.getBaseOffhand();
                        break;
                    }
                    case 2:{
                        baseGear = warriorEquipment.getBaseHelmet();
                        break;
                    }
                    case 3:{
                        baseGear = warriorEquipment.getBaseChestPlate();
                        break;
                    }
                    case 4:{
                        baseGear = warriorEquipment.getBaseLeggings();
                        break;
                    }
                    case 5:{
                        baseGear = warriorEquipment.getBaseBoots();
                        break;
                    }
                }
                break;
            }
            case "none":{
                baseGear = noneEquipment.getBaseWeapon();
                break;
            }
        }

        if(baseGear.getType().equals(Material.AIR)){
            return baseGear;
        }

        return upgrade(baseGear, level);
    }

    public ItemStack upgrade(ItemStack equipment, int newLevel){

        if(equipment.getType().equals(Material.AIR)){
            return equipment;
        }


        ItemMeta meta = equipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;

        boolean hasReforgeStats = false;
        for(String lore : lores){
            if (!lore.startsWith("§") && !lore.equalsIgnoreCase("")) {
                hasReforgeStats = true;
                break;
            }
        }

        List<String> newLore = new ArrayList<>();
        List<String> randomStats = new ArrayList<>();
        newLore.add(ChatColor.of(menuColor) + "Level: " + ChatColor.of(Color.WHITE) + newLevel);

        //get the slot of the item
        String slot = lores.get(1).replaceAll("§.", "");
        newLore.add(ChatColor.of(menuColor) + slot);
        newLore.add("");

        //get what the base stats are
        String[] valid = {"attack","health","mana","defense","magic defense","crit"};
        String regex = ".*?((?i:" + String.join("|", valid) + ")\\s*\\+\\s*(\\d+)).*";
        Pattern pattern = Pattern.compile(regex);
        for (String lore : lores){

            if(!lore.startsWith("§") && !lore.equalsIgnoreCase("")){
                randomStats.add(lore);
                continue;
            }

            Matcher matcher = pattern.matcher(lore);
            if (!matcher.matches()) {
                continue;
            }

            String stat = matcher.group(1);
            stat = stat.replaceAll("\\+\\s*\\d+", "").trim();

            newLore.add(getNewBaseStatString(slot, stat, newLevel));
        }

        newLore.add("");

        if(hasReforgeStats){

            PersistentDataContainer container = meta.getPersistentDataContainer();

            for(String lore : randomStats){
                String name = lore.replaceAll("\\s*\\+\\s*\\d+\\s*", "").toLowerCase();

                if(name.startsWith("skill")){
                    int skillNumber = Integer.parseInt(name.replaceAll("skill ", ""));
                    int skillLevel = Integer.parseInt(lore.replaceAll(".*\\+\\s*(\\d+).*", "$1"));
                    newLore.add("Skill " + skillNumber + " + " + skillLevel);
                    continue;
                    //Bukkit.getLogger().info(skillNumber + " " + skillLevel);
                }

                if(name.equalsIgnoreCase("attack")){
                    name = "offense";
                }

                name = name.replaceAll(" ", "_");

                //because loop within loop and a lore appears twice, it also detects two different namespace keys. remove one of them after its detected

                for (NamespacedKey key : container.getKeys()) {
                    String stat = key.getKey();
                    stat = stat.replaceAll("_[0-9]+$", "");

                    int value = container.get(key, PersistentDataType.INTEGER);

                    //Bukkit.getLogger().info(stat);

                    if(name.equalsIgnoreCase(stat)){
                        //remove from container
                        container.remove(key);

                        //calculate the stat based on the value
                        switch(name.toLowerCase()){
                            case "offense":{
                                newLore.add("Attack + " + statCalculatorOffenseDefense(newLevel, value));
                                break;
                            }
                            case "crit":{
                                newLore.add("Crit + " + statCalculatorCrit(newLevel, value));
                                break;
                            }
                            case "health":{
                                newLore.add("Health + " + statCalculatorHealthMana(newLevel, value));
                                break;
                            }
                            case "mana":{
                                newLore.add("Mana + " + statCalculatorHealthMana(newLevel, value));
                                break;
                            }
                            case "defense":{
                                newLore.add("Defense + " + statCalculatorOffenseDefense(newLevel, value));
                                break;
                            }
                            case "magic_defense":{
                                newLore.add("Magic Defense + " + statCalculatorOffenseDefense(newLevel, value));
                                break;
                            }
                        }
                    }

                }

            }

            newLore.add("");
        }

        int whichLine = 0;
        String requiresRegex = "(?i)requires ";
        Pattern requiresPattern = Pattern.compile(requiresRegex);
        for(String lore : lores){
            String colorlessString = lore.replaceAll("§.", "");
            Matcher requiresMatcher = requiresPattern.matcher(colorlessString);
            if(requiresMatcher.find()){
                whichLine = lores.indexOf(lore);
            }
        }

        newLore.add(lores.get(whichLine));

        /*for(String lore : newLore){
            Bukkit.getLogger().info(lore);
        }*/

        ItemStack newItem = equipment.clone();
        ItemMeta newMeta = newItem.getItemMeta();
        assert newMeta != null;
        newMeta.setLore(newLore);
        newItem.setItemMeta(newMeta);

        return newItem;
    }

    public ItemStack reforge(ItemStack equipment){


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

        int offenceCounter = 0;
        int critCounter = 0;
        int healthCounter = 0;
        int manaCounter = 0;
        int defenseCounter = 0;
        int magicDefenseCounter = 0;
        int skillCounter = 0;

        List<String> availableStats = new ArrayList<>();
        availableStats.add("offense");
        availableStats.add("crit");
        availableStats.add("health");
        availableStats.add("mana");
        availableStats.add("defense");
        availableStats.add("magic defense");
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
                case "skill":{
                    skillCounter++;
                    if(skillCounter==2){
                        availableStats.remove("skill");
                    }
                    break;
                }
            }

        }

        List<String> newRandomStats = new ArrayList<>();
        newRandomStats.add("");
        PersistentDataContainer statRolls = newMeta.getPersistentDataContainer();

        for(int i=0;i<offenceCounter;i++){
            int statPercent = new Random().nextInt(101);

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "offense_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statPercent);
            newRandomStats.add("Attack + " + statCalculatorOffenseDefense(level, statPercent));
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


        for(int i=0;i<skillCounter;i++){
            int statAmount = new Random().nextInt(5) + 1;

            int skillNumber = new Random().nextInt(8) + 1;

            NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "skill_" + i);
            statRolls.set(key, PersistentDataType.INTEGER, statAmount);

            newRandomStats.add("Skill " + skillNumber + " + " + statAmount);
        }

        newLores.addAll(whichLine, newRandomStats);
        newMeta.setLore(newLores);
        newItem.setItemMeta(newMeta);

        return newItem;
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


    private String getNewBaseStatString(String slot, String stat, int level){

        StringBuilder statString = new StringBuilder();

        statString.append(ChatColor.of(new Color(255,255,255))).append(stat).append(" + ");

        int base = 0;

        switch (slot.toLowerCase()){
            case "main hand":{
                switch (stat.toLowerCase()){
                    case "attack":
                    case "magic":{
                        base = 3;
                        break;
                    }
                    case "health":{
                        base = 18;
                        break;
                    }
                }
                break;
            }
            case "secondary":{
                switch (stat.toLowerCase()){
                    case "magic defense":
                    case "defense":{
                        base = 4;
                        break;
                    }
                    case "health":{
                        base = 18;
                        break;
                    }
                }
                break;
            }
            case "helmet":{
                switch (stat.toLowerCase()){
                    case "health":{
                        base = 50;
                        break;
                    }
                }
                break;
            }
            case "chestplate":{
                switch (stat.toLowerCase()){
                    case "magic defense":
                    case "defense":{
                        base = 4;
                        break;
                    }
                    case "health":{
                        base = 31;
                        break;
                    }
                }
                break;
            }
            case "leggings":{
                switch (stat.toLowerCase()){
                    case "attack":
                    case "magic":{
                        base = 4;
                        break;
                    }
                }
                break;
            }
            case "boots":{
                switch (stat.toLowerCase()){
                    case "attack":
                    case "magic":{
                        base = 2;
                        break;
                    }
                }
                break;
            }

        }

        statString.append(base*level);

        return String.valueOf(statString);
    }

}
