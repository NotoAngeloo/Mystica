package me.angeloo.mystica.Components.Items;

import me.angeloo.mystica.Utility.PlayerClass;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public enum EquipmentSlot{
        HEAD,
        CHEST,
        LEGS,
        BOOTS,
        WEAPON
    }

    //cosmetics later

    private final EquipmentSlot equipmentSlot;
    private PlayerClass playerClass;
    private int level;
    private StatType highStat;
    private StatType lowStat;
    private Pair<Integer, Integer> skillOne;
    private Pair<Integer, Integer> skillTwo;

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
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass,int level,StatType highStat,StatType lowStat,Pair<Integer,Integer> skillOne,Pair<Integer,Integer> skillTwo){
        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;
        this.highStat = highStat;
        this.lowStat = lowStat;
        this.skillOne = skillOne;
        this.skillTwo = skillTwo;
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

        if(this.highStat == null){
            meta.setLore(lores);
            item.setItemMeta(meta);
            return item;
        }

        lores.add(ChatColor.of(uncommonColor) + this.highStat.toString().replaceAll("_","") + " + " + getHighStatAmount(this.highStat, level));
        lores.add(ChatColor.of(uncommonColor) + this.lowStat.toString().replaceAll("_","") + " + " + getLowStatAmount(this.lowStat, level));

        if(this.skillOne == null){
            meta.setLore(lores);
            item.setItemMeta(meta);
            return item;
        }

        lores.add(ChatColor.of(rareColor) + "Skill " + this.skillOne.getKey() + " + " + this.skillOne.getValue());
        lores.add(ChatColor.of(rareColor) + "Skill " + this.skillTwo.getKey() + " + " + this.skillTwo.getValue());

        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Map<String, Object> serialize(){
        Map<String, Object> map = new HashMap<>();
        map.put("slot",equipmentSlot.name());
        map.put("class",playerClass.name());
        map.put("level",level);
        map.put("highstat",highStat.name());
        map.put("lowstat",lowStat.name());
        map.put("skill_roll_1", skillOne);
        map.put("skill_roll_2", skillTwo);
        return map;
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


    /*public EquipmentSlot getEquipmentSlot(){
        return equipmentSlot;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public int getLevel() {
        return level;
    }

    public StatType getHighStat() {
        return highStat;
    }
    public StatType getLowStat() {
        return lowStat;
    }

    public Pair<Integer, Integer> getSkillOne(){
        return skillOne;
    }

    public Pair<Integer, Integer> getSkillTwo(){return skillTwo;}*/
}
