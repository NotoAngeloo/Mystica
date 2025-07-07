package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassEquipment.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.PlayerClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public int getItemTier(ItemStack equipment){

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

    public int getEquipmentTier(ItemStack equipment){

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

    public String getEquipmentClass(ItemStack equipment){

        ItemMeta meta = equipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;

        String equipmentClass = "";
        Pattern pattern = Pattern.compile("(?i)Class:\\s*(.*)");
        for(String lore : lores){
            String colorlessString = lore.replaceAll("§.", "");
            Matcher tierMatcher = pattern.matcher(colorlessString);

            if(tierMatcher.find()){
                equipmentClass = tierMatcher.group(1);
            }

        }


        //Bukkit.getLogger().info(equipmentClass);

        return equipmentClass;
    }

    public int getGearType(ItemStack equipment){

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

        PlayerClass clazz = profileManager.getAnyProfile(player).getPlayerClass();

        switch (clazz) {
            case Assassin: {
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
            case Elementalist: {
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
            case Mystic: {
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
            case Paladin: {
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
            case Ranger: {
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
            case Shadow_Knight: {
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
            case Warrior: {
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


            //lores.add(ChatColor.of(menuColor) + "Bonus Attributes");
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

        //Bukkit.getLogger().info("tier " + tier);
        //Bukkit.getLogger().info("geartype " + gearType);

        if(gearType == -1){
            Bukkit.getLogger().info("invalid equipment type attempted upgrade");
            return equipment;
        }

        if(tier == 1){
            //Bukkit.getLogger().info("generating level " + newLevel);
            return generateItem(player, newLevel, tier, gearType);
        }

        ItemStack newEquipment = equipment.clone();
        ItemMeta meta = newEquipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;
        //copies first 4 lines
        List<String> newLores = new ArrayList<>(lores.subList(0, Math.min(4, lores.size())));

        newLores.add(ChatColor.of(menuColor) + "Level: " + newLevel);

        switch (tier){
            case 2:{
                newLores.add(itemManager.buildUncommonDivider(2));
                break;
            }
            case 3:{
                newLores.add(itemManager.buildRareDivider(2));
                break;
            }
        }

        switch (gearType) {
            case 0: {
                newLores.addAll(itemManager.getWeaponBaseStats(newLevel));
                break;
            }
            case 1: {
                newLores.addAll(itemManager.getHelmetBaseStats(newLevel));
                break;
            }
            case 2: {
                newLores.addAll(itemManager.getChestplateBaseStats(newLevel));
                break;
            }
            case 3: {
                newLores.addAll(itemManager.getLeggingsBaseStats(newLevel));
                break;
            }
            case 4: {
                newLores.addAll(itemManager.getBootsBaseStats(newLevel));
                break;
            }
        }

        //newLores.add(ChatColor.of(menuColor) + "Bonus Attributes");
        //now highstat lowstat

        int pointer = newLores.size();
        String highStat = lores.get(pointer);
        String lowStat = lores.get(pointer + 1);

        highStat = highStat.replaceAll("§.", "");
        lowStat = lowStat.replaceAll("§.", "");

        highStat = highStat.replaceFirst("\\s*\\+\\s*\\d+$", "");
        lowStat = lowStat.replaceFirst("\\s*\\+\\s*\\d+$", "");

        int highStatNumber = 0;
        int lowStatNumber = 0;

        switch (highStat.toLowerCase()){
            case "attack":
            case "defense":
            case "magic defense":{
                highStatNumber = getHighAttackOrDefense(newLevel);
                break;
            }
            case "crit":{
                highStatNumber = getHighCrit();
                break;
            }
            case "health":{
                highStatNumber = getHighHealth(newLevel);
                break;
            }
        }

        switch (lowStat.toLowerCase()){
            case "attack":
            case "defense":
            case "magic defense":{
                lowStatNumber = getLowAttackOrDefense(newLevel);
                break;
            }
            case "crit":{
                lowStatNumber = getLowCrit();
                break;
            }
            case "health":{
                lowStatNumber = getLowHealth(newLevel);
                break;
            }
        }

        newLores.add(ChatColor.of(uncommonColor) + highStat + " + " + highStatNumber);
        newLores.add(ChatColor.of(uncommonColor) + lowStat + " + " + lowStatNumber);

        if(tier == 2){
            newLores.add(itemManager.buildUncommonBottom(2));
            meta.setLore(newLores);
            newEquipment.setItemMeta(meta);

            return newEquipment;
        }

        switch (tier){
            case 3:{
                newLores.add(itemManager.buildRareDivider(2));
                break;
            }
        }



        pointer = newLores.size();

        newLores.addAll(lores.subList(pointer, Math.min(pointer + 3, lores.size())));

        //Bukkit.getLogger().info(lores.get(pointer));

        if(tier == 3){
            newLores.add(itemManager.buildRareBottom(2));
            meta.setLore(newLores);
            newEquipment.setItemMeta(meta);
            return newEquipment;
        }

        meta.setLore(newLores);
        newEquipment.setItemMeta(meta);


        return newEquipment;
    }

    public ItemStack reforge(ItemStack equipment){

        if(equipment.getType().equals(Material.AIR)){
            //Bukkit.getLogger().info("no item");
            return equipment;
        }

        ItemStack newEquipment = equipment.clone();
        ItemMeta meta = newEquipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;

        int pointer = -1;
        for(String loreLine : lores){
            /*if(loreLine.contains("Bonus Attributes")){
                pointer = lores.indexOf(loreLine);
                break;
            }*/
            if(loreLine.contains("§x§8§A§D§D§1§F")){
                pointer = lores.indexOf(loreLine);
                //Bukkit.getLogger().info("bonus attribute detected");
            }
        }

        if(pointer == -1){
            //Bukkit.getLogger().info("invalid equipment");
            return equipment;
        }

        int level = getItemLevel(equipment);


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

        lores.set(pointer - 1, ChatColor.of(uncommonColor) + highStat + " + " + highStatNumber);
        lores.set(pointer, ChatColor.of(uncommonColor) + lowStat + " + " + lowStatNumber);

        meta.setLore(lores);
        newEquipment.setItemMeta(meta);

        return newEquipment;
    }

    public ItemStack refine(ItemStack equipment){
        if(equipment.getType().equals(Material.AIR)){
            //Bukkit.getLogger().info("no item");
            return equipment;
        }

        ItemStack newEquipment = equipment.clone();
        ItemMeta meta = newEquipment.getItemMeta();
        assert meta != null;
        List<String> lores = meta.getLore();
        assert lores != null;

        int pointer = -1;
        for(String loreLine : lores){
            if(loreLine.contains("Special Attribute")){
                pointer = lores.indexOf(loreLine);
                break;
            }
        }

        if(pointer == -1){
            //Bukkit.getLogger().info("invalid equipment");
            return equipment;
        }

        int statAmount = new Random().nextInt(5) + 1;
        int statAmount2 = new Random().nextInt(5) + 1;
        int skillNumber = new Random().nextInt(8) + 1;
        int skillNumber2 = new Random().nextInt(8) + 1;


        lores.set(pointer + 1, ChatColor.of(rareColor) + "Skill " + skillNumber + " + " + statAmount);
        lores.set(pointer + 2, ChatColor.of(rareColor) + "Skill " + skillNumber2 + " + " + statAmount2);

        meta.setLore(lores);
        newEquipment.setItemMeta(meta);

        return newEquipment;
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
