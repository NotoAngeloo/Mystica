package me.angeloo.mystica.Components.Items;

import com.google.gson.Gson;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.EquipmentSlot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static me.angeloo.mystica.Mystica.*;
import static me.angeloo.mystica.Mystica.rareColor;

public class MysticaEquipment extends MysticaItem{

    public enum StatType{
        Attack,
        Health,
        Defense,
        Magic_Defense,
        Crit
    }

    //cosmetics later

    private final EquipmentSlot equipmentSlot;
    private PlayerClass playerClass;
    private int level;

    private StatType highStat;
    private StatType lowStat;

    private List<Integer> skillOne;
    private List<Integer> skillTwo;


    //Tier 1
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass, int level){
        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;
    }


    //Tier 2
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass,int level, StatType highStat, StatType lowStat){
        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;
        this.highStat = highStat;
        this.lowStat = lowStat;
    }

    //Tier 3
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass,int level,StatType highStat,StatType lowStat,List<Integer> skillOne,List<Integer> skillTwo){
        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;
        this.highStat = highStat;
        this.lowStat = lowStat;
        this.skillOne = skillOne;
        this.skillTwo = skillTwo;
    }

    //generate a completely random one
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass, int level, int tier){

        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;

        if(tier == 1){
            return;
        }

        List<StatType> availableStats = new ArrayList<>();
        availableStats.add(StatType.Attack);
        availableStats.add(StatType.Health);
        availableStats.add(StatType.Defense);
        availableStats.add(StatType.Magic_Defense);
        availableStats.add(StatType.Crit);
        Collections.shuffle(availableStats);

        this.highStat = availableStats.get(0);
        this.lowStat = availableStats.get(1);

        if(tier == 2){
            return;
        }

        int statAmount = new Random().nextInt(5) + 1;
        int statAmount2 = new Random().nextInt(5) + 1;
        int skillNumber = new Random().nextInt(8) + 1;
        int skillNumber2 = new Random().nextInt(8) + 1;

        this.skillOne = List.of(skillNumber, statAmount);
        this.skillTwo = List.of(skillNumber2, statAmount2);
    }

    //generate one with random stat types
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass, int level, int tier, List<Integer> skillOne, List<Integer> skillTwo){

        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;

        if(tier == 1){
            return;
        }

        List<StatType> availableStats = new ArrayList<>();
        availableStats.add(StatType.Attack);
        availableStats.add(StatType.Health);
        availableStats.add(StatType.Defense);
        availableStats.add(StatType.Magic_Defense);
        availableStats.add(StatType.Crit);
        Collections.shuffle(availableStats);

        this.highStat = availableStats.get(0);
        this.lowStat = availableStats.get(1);

        if(tier == 2){
            return;
        }


        this.skillOne = skillOne;
        this.skillTwo = skillTwo;
    }

    //generate one with random skill levels
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass, int level, int tier, StatType highStat, StatType lowStat){

        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;

        if(tier == 1){
            return;
        }

        this.highStat = highStat;
        this.lowStat = lowStat;

        if(tier == 2){
            return;
        }

        int statAmount = new Random().nextInt(5) + 1;
        int statAmount2 = new Random().nextInt(5) + 1;
        int skillNumber = new Random().nextInt(8) + 1;
        int skillNumber2 = new Random().nextInt(8) + 1;

        this.skillOne = List.of(skillNumber, statAmount);
        this.skillTwo = List.of(skillNumber2, statAmount2);
    }




    @Override
    public MysticaItemFormat format() {
        return MysticaItemFormat.EQUIPMENT;
    }

    @Override
    public String identifier() {
        return this.equipmentSlot.name();
    }

    @Override
    public ItemStack build(){


        Material material = Material.KELP;
        PlayerClass playerClass = this.playerClass;
        EquipmentSlot equipmentSlot = this.equipmentSlot;
        int level = this.level;

        switch (equipmentSlot) {
            case HEAD -> {
                material = Material.CHAIN;
            }
            case CHEST -> {
                material = Material.CHAINMAIL_CHESTPLATE;
            }
            case LEGS -> {
                material = Material.CHAINMAIL_LEGGINGS;
            }
            case BOOTS -> {
                material = Material.CHAINMAIL_BOOTS;
            }
            case WEAPON -> {
                switch (playerClass){
                    case Ranger -> {
                        material = Material.FEATHER;
                    }
                    case Elementalist -> {
                        material = Material.STICK;
                    }
                    case Mystic -> {
                        material = Material.BLAZE_ROD;
                    }
                    case Assassin -> {
                        material = Material.FLINT;
                    }
                    case Shadow_Knight -> {
                        material = Material.DIAMOND_SWORD;
                    }
                    case Warrior -> {
                        material = Material.BRICK;
                    }
                    case Paladin -> {
                        material = Material.IRON_SWORD;
                    }
                    case NONE -> {
                    }
                }
            }
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;


        //look into this
        //meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroer);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        List<String> lores = new ArrayList<>();

        //name and model data
        switch (equipmentSlot){
            case WEAPON -> {
                switch (playerClass){
                    case Elementalist -> {
                        meta.setDisplayName(ChatColor.of(elementalistColor) + "Catalyst");
                    }
                    case Mystic -> {
                        meta.setDisplayName(ChatColor.of(mysticColor) + "Staff");
                    }
                    case Ranger -> {
                        meta.setDisplayName(ChatColor.of(rangerColor) + "Bow");
                    }
                    case Shadow_Knight -> {
                        meta.setDisplayName(ChatColor.of(shadowKnightColor) + "Greatsword");
                    }
                    case Paladin -> {
                        meta.setDisplayName(ChatColor.of(paladinColor) + "Sword");
                    }
                    case Warrior -> {
                        meta.setDisplayName(ChatColor.of(warriorColor) + "Axe");
                    }
                    case Assassin -> {
                        meta.setDisplayName(ChatColor.of(assassinColor) + "Dagger");
                    }

                }

                meta.setCustomModelData(1);
            }
            case HEAD -> {
                switch (playerClass){
                    case Elementalist -> {
                        meta.setDisplayName(ChatColor.of(elementalistColor) + "Elementalist's Hood");
                        meta.setCustomModelData(1);
                    }
                    case Ranger -> {
                        meta.setDisplayName(ChatColor.of(rangerColor) + "Ranger's Hood");
                        meta.setCustomModelData(2);
                    }
                    case Mystic -> {
                        meta.setDisplayName(ChatColor.of(mysticColor) + "Mystic's Hood");
                        meta.setCustomModelData(3);
                    }
                    case Shadow_Knight -> {
                        meta.setDisplayName(ChatColor.of(shadowKnightColor) + "Shadow Knight's Helmet");
                        meta.setCustomModelData(4);
                    }
                    case Paladin -> {
                        meta.setDisplayName(ChatColor.of(paladinColor) + "Paladin's Helmet");
                        meta.setCustomModelData(5);
                    }
                    case Warrior -> {
                        meta.setDisplayName(ChatColor.of(warriorColor) + "Warrior's Helmet");
                        meta.setCustomModelData(6);
                    }
                    case Assassin -> {
                        meta.setDisplayName(ChatColor.of(assassinColor) + "Assassin's Scarf");
                        meta.setCustomModelData(7);
                    }
                }
            }
            case CHEST -> {
                switch (playerClass){
                    case Elementalist -> {
                        meta.setDisplayName(ChatColor.of(elementalistColor) + "Elementalist's Tunic");
                        meta.setCustomModelData(1);
                    }
                    case Ranger -> {
                        meta.setDisplayName(ChatColor.of(rangerColor) + "Ranger's Tunic");
                        meta.setCustomModelData(2);
                    }
                    case Mystic -> {
                        meta.setDisplayName(ChatColor.of(mysticColor) + "Mystic's Tunic");
                        meta.setCustomModelData(3);
                    }
                    case Shadow_Knight -> {
                        meta.setDisplayName(ChatColor.of(shadowKnightColor) + "Shadow Knight's Plate");
                        meta.setCustomModelData(4);
                    }
                    case Paladin -> {
                        meta.setDisplayName(ChatColor.of(paladinColor) + "Paladin's Plate");
                        meta.setCustomModelData(5);
                    }
                    case Warrior -> {
                        meta.setDisplayName(ChatColor.of(warriorColor) + "Warrior's Plate");
                        meta.setCustomModelData(6);
                    }
                    case Assassin -> {
                        meta.setDisplayName(ChatColor.of(assassinColor) + "Assassin's Tunic");
                        meta.setCustomModelData(7);
                    }
                }
            }
            case LEGS -> {
                switch (playerClass){
                    case Elementalist -> {
                        meta.setDisplayName(ChatColor.of(elementalistColor) + "Elementalist's Breeches");
                        meta.setCustomModelData(1);
                    }
                    case Ranger -> {
                        meta.setDisplayName(ChatColor.of(rangerColor) + "Ranger's Breeches");
                        meta.setCustomModelData(2);
                    }
                    case Mystic -> {
                        meta.setDisplayName(ChatColor.of(mysticColor) + "Mystic's Breeches");
                        meta.setCustomModelData(3);
                    }
                    case Shadow_Knight -> {
                        meta.setDisplayName(ChatColor.of(shadowKnightColor) + "Shadow Knight's Breeches");
                        meta.setCustomModelData(4);
                    }
                    case Paladin -> {
                        meta.setDisplayName(ChatColor.of(paladinColor) + "Paladin's Breeches");
                        meta.setCustomModelData(5);
                    }
                    case Warrior -> {
                        meta.setDisplayName(ChatColor.of(warriorColor) + "Warrior's Breeches");
                        meta.setCustomModelData(6);
                    }
                    case Assassin -> {
                        meta.setDisplayName(ChatColor.of(assassinColor) + "Assassin's Breeches");
                        meta.setCustomModelData(6);
                    }
                }
            }
            case BOOTS -> {
                switch (playerClass){
                    case Elementalist -> {
                        meta.setDisplayName(ChatColor.of(elementalistColor) + "Elementalist's Boots");
                        meta.setCustomModelData(1);
                    }
                    case Ranger -> {
                        meta.setDisplayName(ChatColor.of(rangerColor) + "Ranger's Boots");
                        meta.setCustomModelData(2);
                    }
                    case Mystic -> {
                        meta.setDisplayName(ChatColor.of(mysticColor) + "Mystic's Boots");
                        meta.setCustomModelData(3);
                    }
                    case Shadow_Knight -> {
                        meta.setDisplayName(ChatColor.of(shadowKnightColor) + "Shadow Knight's Boots");
                        meta.setCustomModelData(4);
                    }
                    case Paladin -> {
                        meta.setDisplayName(ChatColor.of(paladinColor) + "Paladin's Boots");
                        meta.setCustomModelData(5);
                    }
                    case Warrior -> {
                        meta.setDisplayName(ChatColor.of(warriorColor) + "Warrior's Boots");
                        meta.setCustomModelData(6);
                    }
                    case Assassin -> {
                        meta.setDisplayName(ChatColor.of(assassinColor) + "Assassin's Boots");
                        meta.setCustomModelData(7);
                    }
                }
            }
        }

        //TODO: gearscore tiers

        lores.add(ChatColor.of(menuColor) + "Level: " + this.level);
        lores.add("");

        //base stats
        switch (equipmentSlot){
            case WEAPON -> {
                lores.add(ChatColor.WHITE + "Attack + " + getWeaponBaseAttack(level));
                lores.add(ChatColor.WHITE + "Health + " + getWeaponBaseHealth(level));
                lores.add(ChatColor.WHITE + "Defense + " + getWeaponBaseDefense(level));
                lores.add(ChatColor.WHITE + "Magic Defense + " + getWeaponBaseDefense(level));
            }
            case HEAD -> {
                lores.add(ChatColor.WHITE + "Health + " + getHelmetBaseHealth(level));
            }
            case CHEST -> {
                lores.add(ChatColor.WHITE + "Health + " + getChestBaseHealth(level));
                lores.add(ChatColor.WHITE + "Defense + " + getChestBaseDefense(level));
                lores.add(ChatColor.WHITE + "Magic Defense + " + getChestBaseDefense(level));
            }
            case LEGS -> {
                lores.add(ChatColor.WHITE + "Attack + " + getLeggingBaseAttack(level));
            }
            case BOOTS -> {
                lores.add(ChatColor.WHITE + "Attack + " + getBootsBaseAttack(level));
            }
        }

        NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
        Gson gson = new Gson();
        String json = gson.toJson(this);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, json);

        //Bukkit.getLogger().info(json);

        if(this.highStat == null){

            meta.setLore(lores);
            item.setItemMeta(meta);
            return item;
        }

        lores.add(ChatColor.of(uncommonColor) + this.highStat.toString().replaceAll("_"," ") + " + " + getHighStatAmount(this.highStat, level));
        lores.add(ChatColor.of(uncommonColor) + this.lowStat.toString().replaceAll("_"," ") + " + " + getLowStatAmount(this.lowStat, level));

        if(this.skillOne == null){
            meta.setLore(lores);
            item.setItemMeta(meta);
            return item;
        }

        lores.add(ChatColor.of(rareColor) + "Skill " + this.skillOne.get(0) + " + " + this.skillOne.get(1));
        lores.add(ChatColor.of(rareColor) + "Skill " + this.skillTwo.get(0) + " + " + this.skillTwo.get(1));

        //lores.add(ChatColor.of(rareColor) + "Skill " + this.skillOne.getKey() + " + " + this.skillOne.getValue());
        //lores.add(ChatColor.of(rareColor) + "Skill " + this.skillTwo.getKey() + " + " + this.skillTwo.getValue());

        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean questItem() {
        return false;
    }

    @Override
    public Map<String, Object> serialize(){
        Map<String, Object> map = new HashMap<>();
        map.put("identifier",identifier());
        map.put("slot",equipmentSlot.name());
        map.put("class",playerClass.name());
        map.put("level",level);
        map.put("format", format().name());

        if(highStat != null){
            map.put("highstat",highStat.name());
        }

        if(lowStat != null){
            map.put("lowstat",lowStat.name());
        }

        if(skillOne != null){
            map.put("skill_roll_1", skillOne);
        }

        if(skillTwo != null){
            map.put("skill_roll_2", skillTwo);
        }


        return map;
    }

    public static MysticaEquipment deserialize(Map<String, Object> map){

        EquipmentSlot slot = EquipmentSlot.valueOf((String)map.get("slot"));
        PlayerClass playerClass = PlayerClass.valueOf((String) map.get("class"));
        int level = (int) map.get("level");
        StatType highstat = null;
        StatType lowstat = null;
        List<Integer> skillOne = null;
        List<Integer> skillTwo = null;


        if(map.containsKey("highstat")){
            highstat = StatType.valueOf((String) map.get("highstat"));
        }

        if(map.containsKey("lowstat")){
            lowstat = StatType.valueOf((String) map.get("lowstat"));
        }

        if(map.containsKey("skill_roll_1")){
            skillOne = (List<Integer>) map.get("skill_roll_1");
        }

        if(map.containsKey("skill_roll_2")){
            skillTwo = (List<Integer>) map.get("skill_roll_2");

        }

        if(highstat == null){
            return new MysticaEquipment(slot,playerClass,level);
        }

        if(skillOne == null){
            return new MysticaEquipment(slot,playerClass,level,highstat,lowstat);
        }


        return new MysticaEquipment(slot,playerClass,level,highstat,lowstat,skillOne,skillTwo);
    }


    public int getWeaponBaseAttack(int level){
        return 3 * level;
    }
    public int getWeaponBaseHealth(int level){
        return 18 * level;
    }
    public int getWeaponBaseDefense(int level){
        return 4 * level;
    }
    public int getHelmetBaseHealth(int level){
        return 50 * level;
    }
    public int getChestBaseHealth(int level){
        return 31 * level;
    }
    public int getChestBaseDefense(int level){
        return 4 * level;
    }
    public int getLeggingBaseAttack(int level){
        return 4 * level;
    }
    public int getBootsBaseAttack(int level){
        return 2 * level;
    }

    public int getHighStatAmount(StatType statType, int level){
        switch (statType) {
            case Attack, Defense, Magic_Defense -> {
                return getHighAttackOrDefense(level);
            }
            case Health -> {
                return getHighHealth(level);
            }
            case Crit -> {
                return getHighCrit();
            }
        }
        return 0;
    }
    public int getLowStatAmount(StatType statType, int level){
        switch (statType) {
            case Attack, Defense, Magic_Defense -> {
                return getLowAttackOrDefense(level);
            }
            case Health -> {
                return getLowHealth(level);
            }
            case Crit -> {
                return getLowCrit();
            }
        }
        return 0;
    }


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

    public int getLevel(){
        return this.level;
    }
    public int getTier(){

        int tier = 1;

        if(highStat != null){
            tier = 2;
        }

        if(skillOne != null){
            tier = 3;
        }

        return tier;
    }

    public EquipmentSlot getEquipmentSlot(){
        return this.equipmentSlot;
    }

    public PlayerClass getPlayerClass(){
        return this.playerClass;
    }

    public StatType getHighStat(){
        return this.highStat;
    }

    public StatType getLowStat(){
        return this.lowStat;
    }

    public List<Integer> getSkillOne(){
        return skillOne;
    }

    public List<Integer> getSkillTwo(){
        return skillTwo;
    }

    public void setLevel(int newLevel){
        this.level = newLevel;
    }

    public void setPlayerClass(PlayerClass playerClass){this.playerClass = playerClass;}
}
