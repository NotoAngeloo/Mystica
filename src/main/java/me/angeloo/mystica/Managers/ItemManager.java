package me.angeloo.mystica.Managers;


import me.angeloo.mystica.Components.ClassEquipment.*;
import me.angeloo.mystica.Components.Items.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ItemManager {

    private final List<Material> equipmentTypes;

    private final UnidentifiedHelmet unidentifiedHelmet;
    private final UnidentifiedChestplate unidentifiedChestplate;
    private final UnidentifiedLeggings unidentifiedLeggings;
    private final UnidentifiedBoots unidentifiedBoots;
    private final UnidentifiedWeapon unidentifiedWeapon;

    private final SoulStone soulStone;

    private final AssassinEquipment assassinEquipment;
    private final ElementalistEquipment elementalistEquipment;
    private final MysticEquipment mysticEquipment;
    private final NoneEquipment noneEquipment;
    private final PaladinEquipment paladinEquipment;
    private final RangerEquipment rangerEquipment;
    private final ShadowKnightEquipment shadowKnightEquipment;
    private final WarriorEquipment warriorEquipment;

    public ItemManager(){
        unidentifiedHelmet = new UnidentifiedHelmet(this);
        unidentifiedChestplate = new UnidentifiedChestplate(this);
        unidentifiedLeggings = new UnidentifiedLeggings(this);
        unidentifiedBoots = new UnidentifiedBoots(this);
        unidentifiedWeapon =new UnidentifiedWeapon(this);

        soulStone = new SoulStone(this);

        assassinEquipment = new AssassinEquipment(this);
        elementalistEquipment = new ElementalistEquipment(this);
        mysticEquipment = new MysticEquipment(this);
        noneEquipment = new NoneEquipment(this);
        paladinEquipment = new PaladinEquipment(this);
        rangerEquipment = new RangerEquipment(this);
        shadowKnightEquipment = new ShadowKnightEquipment(this);
        warriorEquipment = new WarriorEquipment(this);

        equipmentTypes = new ArrayList<>();
        equipmentTypes.add(Material.STICK);
        equipmentTypes.add(Material.FLINT);
        equipmentTypes.add(Material.BLAZE_ROD);
        equipmentTypes.add(Material.IRON_SWORD);
        equipmentTypes.add(Material.FEATHER);
        equipmentTypes.add(Material.DIAMOND_SWORD);
        equipmentTypes.add(Material.BRICK);
        equipmentTypes.add(Material.CHAIN);
        equipmentTypes.add(Material.KELP);
        equipmentTypes.add(Material.CHAINMAIL_CHESTPLATE);
        equipmentTypes.add(Material.CHAINMAIL_LEGGINGS);
        equipmentTypes.add(Material.CHAINMAIL_BOOTS);
        //i forgot what this last one is
        //equipmentTypes.add(Material.IRON_NUGGET);
    }

    public List<Material> getEquipmentTypes(){return equipmentTypes;}

    public ItemStack getItem(Material material, int modelData, String name, String ... lore){

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setUnbreakable(true);

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroer);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getStackableItem(Material material, int modelData, String name, String ... lore){

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setUnbreakable(true);

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }

    public String buildCommonTop(int width){

        StringBuilder tooltip = new StringBuilder();

        String topLeft = "\uE093";
        String topMid = "\uE094";
        String topRight = "\uE095";

        tooltip.append("\uF809\uF806").append(topLeft); //-22 space

        //-1 space
        tooltip.append("\uF801");

        StringBuilder center = new StringBuilder();
        for(int i=0;i<width;i++){
            center.append(topMid);
            //-1 space
            center.append("\uF801");
        }

        tooltip.append(center);

        //-23 space
        tooltip.append("\uF809\uF807");


        //22 offset
        tooltip.append("\uF829\uF826").append(topRight).append("\uF809\uF806");

        return ChatColor.of(Color.WHITE) + tooltip.toString();
    }
    public String buildCommonDivider(int width){

        StringBuilder tooltip = new StringBuilder();

        String left = "\uE096";
        String mid = "\uE097";
        String right = "\uE098";

        //-5 space
        tooltip.append("\uF805");

        tooltip.append(left);

        //-1 space
        tooltip.append("\uF801");

        StringBuilder center = new StringBuilder();
        for(int i=0;i<width;i++){
            center.append(mid);
            //-1 space
            center.append("\uF801");
        }

        tooltip.append(center);


        //22 offset
        tooltip.append(right);

        return ChatColor.of(Color.WHITE) + tooltip.toString();
    }
    public String buildCommonBottom(int width){

        StringBuilder tooltip = new StringBuilder();

        String topLeft = "\uE099";
        String topMid = "\uE09A";
        String topRight = "\uE09B";

        tooltip.append("\uF809\uF806").append(topLeft); //-22 space

        //-1 space
        tooltip.append("\uF801");

        StringBuilder center = new StringBuilder();
        for(int i=0;i<width;i++){
            center.append(topMid);
            //-1 space
            center.append("\uF801");
        }

        tooltip.append(center);

        //-23 space
        tooltip.append("\uF809\uF807");


        //22 offset
        tooltip.append("\uF829\uF826").append(topRight).append("\uF809\uF806");

        return ChatColor.of(Color.WHITE) + tooltip.toString();
    }
    public String buildUncommonTop(int width){

        StringBuilder tooltip = new StringBuilder();

        String topLeft = "\uE09C";
        String topMid = "\uE09D";
        String topRight = "\uE09E";

        tooltip.append("\uF809\uF806").append(topLeft); //-22 space

        //-1 space
        tooltip.append("\uF801");

        StringBuilder center = new StringBuilder();
        for(int i=0;i<width;i++){
            center.append(topMid);
            //-1 space
            center.append("\uF801");
        }

        tooltip.append(center);

        //-23 space
        tooltip.append("\uF809\uF807");


        //22 offset
        tooltip.append("\uF829\uF826").append(topRight).append("\uF809\uF806");

        return ChatColor.of(Color.WHITE) + tooltip.toString();
    }
    public String buildUncommonDivider(int width){

        StringBuilder tooltip = new StringBuilder();

        String left = "\uE09F";
        String mid = "\uE0A0";
        String right = "\uE0A1";

        //-5 space
        tooltip.append("\uF805");

        tooltip.append(left);

        //-1 space
        tooltip.append("\uF801");

        StringBuilder center = new StringBuilder();
        for(int i=0;i<width;i++){
            center.append(mid);
            //-1 space
            center.append("\uF801");
        }

        tooltip.append(center);


        //22 offset
        tooltip.append(right);

        return ChatColor.of(Color.WHITE) + tooltip.toString();
    }
    public String buildUncommonBottom(int width){

        StringBuilder tooltip = new StringBuilder();

        String topLeft = "\uE0A2";
        String topMid = "\uE0A3";
        String topRight = "\uE0A4";

        tooltip.append("\uF809\uF806").append(topLeft); //-22 space

        //-1 space
        tooltip.append("\uF801");

        StringBuilder center = new StringBuilder();
        for(int i=0;i<width;i++){
            center.append(topMid);
            //-1 space
            center.append("\uF801");
        }

        tooltip.append(center);

        //-23 space
        tooltip.append("\uF809\uF807");


        //22 offset
        tooltip.append("\uF829\uF826").append(topRight).append("\uF809\uF806");

        return ChatColor.of(Color.WHITE) +tooltip.toString();
    }
    public String buildRareTop(int width){

        StringBuilder tooltip = new StringBuilder();

        String topLeft = "\uE0A5";
        String topMid = "\uE0A6";
        String topRight = "\uE0A7";

        tooltip.append("\uF809\uF806").append(topLeft); //-22 space

        //-1 space
        tooltip.append("\uF801");

        StringBuilder center = new StringBuilder();
        for(int i=0;i<width;i++){
            center.append(topMid);
            //-1 space
            center.append("\uF801");
        }

        tooltip.append(center);

        //-23 space
        tooltip.append("\uF809\uF807");


        //22 offset
        tooltip.append("\uF829\uF826").append(topRight).append("\uF809\uF806");

        return ChatColor.of(Color.WHITE) + tooltip.toString();
    }
    public String buildRareDivider(int width){

        StringBuilder tooltip = new StringBuilder();

        String left = "\uE0A8";
        String mid = "\uE0A9";
        String right = "\uE0AA";

        //-5 space
        tooltip.append("\uF805");

        tooltip.append(left);

        //-1 space
        tooltip.append("\uF801");

        StringBuilder center = new StringBuilder();
        for(int i=0;i<width;i++){
            center.append(mid);
            //-1 space
            center.append("\uF801");
        }

        tooltip.append(center);


        //22 offset
        tooltip.append(right);

        return ChatColor.of(Color.WHITE) + tooltip.toString();
    }
    public String buildRareBottom(int width){

        StringBuilder tooltip = new StringBuilder();

        String topLeft = "\uE0AB";
        String topMid = "\uE0AC";
        String topRight = "\uE0AD";

        tooltip.append("\uF809\uF806").append(topLeft); //-22 space

        //-1 space
        tooltip.append("\uF801");

        StringBuilder center = new StringBuilder();
        for(int i=0;i<width;i++){
            center.append(topMid);
            //-1 space
            center.append("\uF801");
        }

        tooltip.append(center);

        //-23 space
        tooltip.append("\uF809\uF807");


        //22 offset
        tooltip.append("\uF829\uF826").append(topRight).append("\uF809\uF806");

        return ChatColor.of(Color.WHITE) +tooltip.toString();
    }

    public UnidentifiedHelmet getUnidentifiedHelmet(){return unidentifiedHelmet;}
    public UnidentifiedChestplate getUnidentifiedChestplate() {
        return unidentifiedChestplate;
    }
    public UnidentifiedLeggings getUnidentifiedLeggings() {
        return unidentifiedLeggings;
    }
    public UnidentifiedBoots getUnidentifiedBoots() {
        return unidentifiedBoots;
    }
    public UnidentifiedWeapon getUnidentifiedWeapon() {
        return unidentifiedWeapon;
    }

    public SoulStone getSoulStone(){return soulStone;}

    public AssassinEquipment getAssassinEquipment() {
        return assassinEquipment;
    }
    public ElementalistEquipment getElementalistEquipment() {
        return elementalistEquipment;
    }
    public MysticEquipment getMysticEquipment() {
        return mysticEquipment;
    }
    public NoneEquipment getNoneEquipment() {
        return noneEquipment;
    }
    public PaladinEquipment getPaladinEquipment() {
        return paladinEquipment;
    }
    public RangerEquipment getRangerEquipment() {
        return rangerEquipment;
    }
    public ShadowKnightEquipment getShadowKnightEquipment() {
        return shadowKnightEquipment;
    }
    public WarriorEquipment getWarriorEquipment() {
        return warriorEquipment;
    }

    public List<String> getWeaponBaseStats(int level){

        List<String> baseLores = new ArrayList<>();

        baseLores.add(ChatColor.of(Color.WHITE) + "Attack + " + getWeaponBaseAttack(level));
        baseLores.add(ChatColor.of(Color.WHITE) + "Health + " + getWeaponBaseHealth(level));
        baseLores.add(ChatColor.of(Color.WHITE) + "Defense + " + getWeaponBaseDefense(level));
        baseLores.add(ChatColor.of(Color.WHITE) + "Magic Defense + " + getWeaponBaseDefense(level));

        return baseLores;
    }

    public List<String> getHelmetBaseStats(int level){

        List<String> baseLores = new ArrayList<>();
        baseLores.add(ChatColor.of(Color.WHITE) + "Health + " + getHelmetBaseHealth(level));
        return baseLores;
    }

    public List<String> getChestplateBaseStats(int level){

        List<String> baseLores = new ArrayList<>();

        baseLores.add(ChatColor.of(Color.WHITE) + "Health + " + getChestBaseHealth(level));
        baseLores.add(ChatColor.of(Color.WHITE) + "Defense + " + getChestBaseDefense(level));
        baseLores.add(ChatColor.of(Color.WHITE) + "Magic Defense + " + getChestBaseDefense(level));

        return baseLores;
    }

    public List<String> getLeggingsBaseStats(int level){

        List<String> baseLores = new ArrayList<>();

        baseLores.add(ChatColor.of(Color.WHITE) + "Attack + " + getLeggingBaseAttack(level));

        return baseLores;
    }

    public List<String> getBootsBaseStats(int level){

        List<String> baseLores = new ArrayList<>();

        baseLores.add(ChatColor.of(Color.WHITE) + "Attack + " + getBootsBaseAttack(level));

        return baseLores;
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

}
