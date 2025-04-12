package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassEquipment.*;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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

import static me.angeloo.mystica.Mystica.*;

public class EquipmentManager {

    private final ProfileManager profileManager;
    private final ItemManager itemManager;

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
        itemManager = main.getItemManager();
        noneEquipment = itemManager.getNoneEquipment();
        assassinEquipment = itemManager.getAssassinEquipment();
        elementalistEquipment = itemManager.getElementalistEquipment();
        mysticEquipment = itemManager.getMysticEquipment();
        paladinEquipment = itemManager.getPaladinEquipment();
        rangerEquipment = itemManager.getRangerEquipment();
        shadowKnightEquipment = itemManager.getShadowKnightEquipment();
        warriorEquipment = itemManager.getWarriorEquipment();
    }

    private int getItemLevel(ItemStack equipment){

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

    private int getItemTier(ItemStack equipment){

        ItemMeta meta = equipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;

        int tier = 0;
        String tierRegex = ".*\\b(?i:tier:)\\s*(\\d+).*";
        Pattern tierPattern = Pattern.compile(tierRegex);
        for(String lore : lores){
            String colorlessString = lore.replaceAll("§.", "");
            Matcher tierMatcher = tierPattern.matcher(colorlessString);
            if(tierMatcher.matches()){
                tier = Integer.parseInt(tierMatcher.group(1));
                break;
            }

        }
        return tier;
    }

    private int getEquipmentTier(ItemStack equipment){

        ItemMeta meta = equipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;

        int tier = 0;
        Pattern tierPattern = Pattern.compile("(?i)\\bTier\\s+(\\d+)");
        for(String lore : lores){
            String colorlessString = lore.replaceAll("§.", "");
            Matcher tierMatcher = tierPattern.matcher(colorlessString);

            if(tierMatcher.find()){
                tier = Integer.parseInt(tierMatcher.group(1));
            }

        }

        return tier;
    }

    private int getGearType(ItemStack equipment){

        ItemMeta meta = equipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;

        String name = meta.getDisplayName();
        name = name.replaceAll("§.", "");
        name = name.replaceAll("Assassin's ", "");
        name = name.replaceAll("Elementalist's ", "");
        name = name.replaceAll("Mystic's ", "");
        name = name.replaceAll("Paladin's ", "");
        name = name.replaceAll("Ranger's ", "");
        name = name.replaceAll("Shadow Knight's ", "");
        name = name.replaceAll("Warrior's ", "");


        switch (name.toLowerCase()){
            case "dagger":
            case "catalyst":
            case "staff":
            case "sword":
            case "bow":
            case "greatsword":
            case "axe":{
                return 0;
            }
            case "scarf":
            case "hood":
            case "helmet":{
                return 1;
            }
            case "tunic":
            case "plate":{
                return 2;
            }
            case "breeches":{
                return 3;
            }
            case "boots":{
                return 4;
            }
        }

        return -1;
    }



    public ItemStack identify(Player player, ItemStack equipment){

        ItemMeta meta = equipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;
        int level = getItemLevel(equipment);
        int tier = getItemTier(equipment);

        //Bukkit.getLogger().info("level is " + level);
        //Bukkit.getLogger().info("tier is " + tier);

        String name = meta.getDisplayName();
        name = name.replaceAll("Unidentified ", "");
        String colorlessName = name.replaceAll("§.", "");
        int gearType = -1;

        switch (colorlessName.toLowerCase()){
            case "weapon":{
                gearType = 0;
                break;
            }
            case "helmet":{
                gearType = 1;
                break;
            }
            case "chestplate":{
                gearType = 2;
                break;
            }
            case "leggings":{
                gearType = 3;
                break;
            }
            case "boots":{
                gearType = 4;
                break;
            }
        }

        return generateItem(player, level, tier, gearType);
    }

    public ItemStack generateItem(Player player, int level, int tier, int gearType){

        ItemStack baseGear = new ItemStack(Material.AIR);

        //randomly generate
        if(gearType == -1){
            gearType = new Random().nextInt(5);
        }

        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        switch (clazz.toLowerCase()) {
            case "assassin": {
                switch (gearType) {
                    case 0: {
                        baseGear = assassinEquipment.getBaseWeapon(level);
                        break;
                    }
                    case 1: {
                        baseGear = assassinEquipment.getBaseHelmet(level);
                        break;
                    }
                    case 2: {
                        baseGear = assassinEquipment.getBaseChestPlate(level);
                        break;
                    }
                    case 3: {
                        baseGear = assassinEquipment.getBaseLeggings(level);
                        break;
                    }
                    case 4: {
                        baseGear = assassinEquipment.getBaseBoots(level);
                        break;
                    }
                }
                break;
            }
            case "elementalist": {
                switch (gearType) {
                    case 0: {
                        baseGear = elementalistEquipment.getBaseWeapon(level);
                        break;
                    }
                    case 1: {
                        baseGear = elementalistEquipment.getBaseHelmet(level);
                        break;
                    }
                    case 2: {
                        baseGear = elementalistEquipment.getBaseChestPlate(level);
                        break;
                    }
                    case 3: {
                        baseGear = elementalistEquipment.getBaseLeggings(level);
                        break;
                    }
                    case 4: {
                        baseGear = elementalistEquipment.getBaseBoots(level);
                        break;
                    }
                }
                break;
            }
            case "mystic": {
                switch (gearType) {
                    case 0: {
                        baseGear = mysticEquipment.getBaseWeapon(level);
                        break;
                    }
                    case 1: {
                        baseGear = mysticEquipment.getBaseHelmet(level);
                        break;
                    }
                    case 2: {
                        baseGear = mysticEquipment.getBaseChestPlate(level);
                        break;
                    }
                    case 3: {
                        baseGear = mysticEquipment.getBaseLeggings(level);
                        break;
                    }
                    case 4: {
                        baseGear = mysticEquipment.getBaseBoots(level);
                        break;
                    }
                }
                break;
            }
            case "paladin": {
                switch (gearType) {
                    case 0: {
                        baseGear = paladinEquipment.getBaseWeapon(level);
                        break;
                    }
                    case 1: {
                        baseGear = paladinEquipment.getBaseHelmet(level);
                        break;
                    }
                    case 2: {
                        baseGear = paladinEquipment.getBaseChestPlate(level);
                        break;
                    }
                    case 3: {
                        baseGear = paladinEquipment.getBaseLeggings(level);
                        break;
                    }
                    case 4: {
                        baseGear = paladinEquipment.getBaseBoots(level);
                        break;
                    }
                }
                break;
            }
            case "ranger": {
                switch (gearType) {
                    case 0: {
                        baseGear = rangerEquipment.getBaseWeapon(level);
                        break;
                    }
                    case 1: {
                        baseGear = rangerEquipment.getBaseHelmet(level);
                        break;
                    }
                    case 2: {
                        baseGear = rangerEquipment.getBaseChestPlate(level);
                        break;
                    }
                    case 3: {
                        baseGear = rangerEquipment.getBaseLeggings(level);
                        break;
                    }
                    case 4: {
                        baseGear = rangerEquipment.getBaseBoots(level);
                        break;
                    }
                }
                break;
            }
            case "shadow knight": {
                switch (gearType) {
                    case 0: {
                        baseGear = shadowKnightEquipment.getBaseWeapon(level);
                        break;
                    }
                    case 1: {
                        baseGear = shadowKnightEquipment.getBaseHelmet(level);
                        break;
                    }
                    case 2: {
                        baseGear = shadowKnightEquipment.getBaseChestPlate(level);
                        break;
                    }
                    case 3: {
                        baseGear = shadowKnightEquipment.getBaseLeggings(level);
                        break;
                    }
                    case 4: {
                        baseGear = shadowKnightEquipment.getBaseBoots(level);
                        break;
                    }
                }
                break;
            }
            case "warrior": {
                switch (gearType) {
                    case 0: {
                        baseGear = warriorEquipment.getBaseWeapon(level);
                        break;
                    }
                    case 1: {
                        baseGear = warriorEquipment.getBaseHelmet(level);
                        break;
                    }
                    case 2: {
                        baseGear = warriorEquipment.getBaseChestPlate(level);
                        break;
                    }
                    case 3: {
                        baseGear = warriorEquipment.getBaseLeggings(level);
                        break;
                    }
                    case 4: {
                        baseGear = warriorEquipment.getBaseBoots(level);
                        break;
                    }
                }
                break;
            }
        }

        if(tier==1){
            return baseGear;
        }

        ItemMeta meta;

        if(baseGear.hasItemMeta()){
            meta = baseGear.getItemMeta();
        }
        else{
            Bukkit.getLogger().info("attempted to generate new T2+ equipment without setting up default");
            return baseGear;
        }

        assert meta != null;
        List<String> lores = meta.getLore();

        assert lores != null;
        int tierLine = -1;
        for(String loreLine : lores){
            if(loreLine.contains("Tier")){
                tierLine = lores.indexOf(loreLine);
                break;
            }
        }

        String oldTier = lores.get(tierLine);
        String newTier = oldTier.replaceAll("(?i)(\\bTier\\s*)\\d+", "$1" + tier);
        lores.set(tierLine, newTier);

        if(tier>=2){
            //add bonus stats

            //removes the bottom temporarily
            lores.remove(lores.size()-1);

            if(tier == 2){

                //this is the top part
                lores.set(0, itemManager.buildUncommonTop(2));

                String commonDivider = itemManager.buildCommonDivider(2).replaceAll("§.", "");

                for(String lore : lores){
                    if(lore.contains(commonDivider)){
                        lores.set(lores.indexOf(lore), itemManager.buildUncommonDivider(2));
                    }
                }


            }

            if(tier == 3){
                //this is the top part
                lores.set(0, itemManager.buildRareTop(2));

                String commonDivider = itemManager.buildCommonDivider(2).replaceAll("§.", "");

                for(String lore : lores){
                    if(lore.contains(commonDivider)){
                        lores.set(lores.indexOf(lore), itemManager.buildRareDivider(2));
                    }
                }


            }

            List<String> availableStats = new ArrayList<>();
            availableStats.add("Attack");
            availableStats.add("Crit");
            availableStats.add("Health");
            availableStats.add("Defense");
            availableStats.add("Magic Defense");

            Collections.shuffle(availableStats);

            String highStat = availableStats.get(0);
            int highStatNumber = 0;
            String lowStat = availableStats.get(1);
            int lowStatNumber = 0;

            switch (highStat.toLowerCase()){
                case "attack":
                case "defense":
                case "magic defense":{
                    highStatNumber = getHighAttackOrDefense(level);
                    break;
                }
                case "crit":{
                    highStatNumber = getHighCrit();
                    break;
                }
                case "health":{
                    highStatNumber = getHighHealth(level);
                    break;
                }
            }

            switch (lowStat.toLowerCase()){
                case "attack":
                case "defense":
                case "magic defense":{
                    lowStatNumber = getLowAttackOrDefense(level);
                    break;
                }
                case "crit":{
                    lowStatNumber = getLowCrit();
                    break;
                }
                case "health":{
                    lowStatNumber = getLowHealth(level);
                    break;
                }
            }


            lores.add(ChatColor.of(menuColor) + "Bonus Attributes");
            lores.add(ChatColor.of(uncommonColor) + highStat + " + " + highStatNumber);
            lores.add(ChatColor.of(uncommonColor) + lowStat + " + " + lowStatNumber);

            //this is data to serialize/deserialize on saving it as a string in storage
            /*NamespacedKey high_stat = new NamespacedKey(Mystica.getPlugin(), "high_stat");
            statRolls.set(high_stat, PersistentDataType.STRING, highStat);
            NamespacedKey low_stat = new NamespacedKey(Mystica.getPlugin(), "low_stat");
            statRolls.set(low_stat, PersistentDataType.STRING, lowStat); */
        }

        if(tier == 2){
            lores.add(itemManager.buildUncommonBottom(2));
            meta.setLore(lores);
            baseGear.setItemMeta(meta);

            return baseGear;
        }

        if(tier == 3){
            lores.add(itemManager.buildRareDivider(2));
        }

        //add more divider rarities in the future

        lores.add(ChatColor.of(menuColor) + "Special Attribute");

        int statAmount = new Random().nextInt(5) + 1;
        int statAmount2 = new Random().nextInt(5) + 1;
        int skillNumber = new Random().nextInt(8) + 1;
        int skillNumber2 = new Random().nextInt(8) + 1;

        //NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "skill_" + i);
        //statRolls.set(key, PersistentDataType.INTEGER, statAmount);

        lores.add(ChatColor.of(rareColor) + "Skill " + skillNumber + " + " + statAmount);
        lores.add(ChatColor.of(rareColor) + "Skill " + skillNumber2 + " + " + statAmount2);

        lores.add(itemManager.buildRareBottom(2));
        meta.setLore(lores);
        baseGear.setItemMeta(meta);

        return baseGear;
    }

    public ItemStack upgrade(Player player, ItemStack equipment, int newLevel){

        if(equipment.getType().equals(Material.AIR)){
            Bukkit.getLogger().info("no item");
            return equipment;
        }

        int tier = getEquipmentTier(equipment);
        int gearType = getGearType(equipment);

        Bukkit.getLogger().info("tier " + tier);
        Bukkit.getLogger().info("geartype " + gearType);

        if(gearType == -1){
            Bukkit.getLogger().info("invalid equipment type attempted upgrade");
            return equipment;
        }

        if(tier == 1){
            Bukkit.getLogger().info("generating level " + newLevel);
            return generateItem(player, newLevel, tier, gearType);
        }

        ItemMeta meta = equipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();





        return equipment;
    }

    /*public ItemStack upgrade(ItemStack equipment, int newLevel){

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
        String[] valid = {"attack","health","defense","magic defense","crit"};
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
                                newLore.add("Health + " + statCalculatorHealth(newLevel, value));
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

        for(String lore : newLore){
            Bukkit.getLogger().info(lore);
        }

        ItemStack newItem = equipment.clone();
        ItemMeta newMeta = newItem.getItemMeta();
        assert newMeta != null;
        newMeta.setLore(newLore);
        newItem.setItemMeta(newMeta);

        return newItem;
    } */

    /*public ItemStack reforge(ItemStack equipment){


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
        int defenseCounter = 0;
        int magicDefenseCounter = 0;
        int skillCounter = 0;

        List<String> availableStats = new ArrayList<>();
        availableStats.add("offense");
        availableStats.add("crit");
        availableStats.add("health");
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

            newRandomStats.add("Health + " + statCalculatorHealth(level, statPercent));
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
    } */

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

    private int statCalculatorHealth(int level, double percent){

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


    /*public ItemStack deserialize(int gearType, int level, int tier, String clazz, String ... data){

        ItemStack baseGear = new ItemStack(Material.AIR);

        switch (clazz.toLowerCase()){
            case "assassin":{
                switch (gearType){
                    case 0:{
                        baseGear = assassinEquipment.getBaseWeapon();
                        break;
                    }
                    case 1:{
                        baseGear = assassinEquipment.getBaseHelmet();
                        break;
                    }
                    case 2:{
                        baseGear = assassinEquipment.getBaseChestPlate();
                        break;
                    }
                    case 3:{
                        baseGear = assassinEquipment.getBaseLeggings();
                        break;
                    }
                    case 4:{
                        baseGear = assassinEquipment.getBaseBoots();
                        break;
                    }
                }
                break;
            }
        }

        return baseGear;
    }*/

    private int getLowAttackOrDefense(int level){
        level--;
        return 10 * (1 + level);
    }
    private int getHighAttackOrDefense(int level){
        level--;
        return 20 * (1 + level);
    }
    private int getLowHealth(int level){
        level--;
        return 5 * (1 + level);
    }
    private int getHighHealth(int level){
        level--;
        return 10 * (1 + level);
    }
    private int getLowCrit(){
        return 5;
    }
    private int getHighCrit(){
        return 10;
    }

}
