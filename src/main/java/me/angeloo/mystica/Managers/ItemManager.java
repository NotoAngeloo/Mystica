package me.angeloo.mystica.Managers;


import me.angeloo.mystica.Components.ClassEquipment.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ItemManager {

    private final AssassinEquipment assassinEquipment;
    private final ElementalistEquipment elementalistEquipment;
    private final MysticEquipment mysticEquipment;
    private final NoneEquipment noneEquipment;
    private final PaladinEquipment paladinEquipment;
    private final RangerEquipment rangerEquipment;
    private final ShadowKnightEquipment shadowKnightEquipment;
    private final WarriorEquipment warriorEquipment;

    public ItemManager(){
        assassinEquipment = new AssassinEquipment(this);
        elementalistEquipment = new ElementalistEquipment(this);
        mysticEquipment = new MysticEquipment(this);
        noneEquipment = new NoneEquipment(this);
        paladinEquipment = new PaladinEquipment(this);
        rangerEquipment = new RangerEquipment(this);
        shadowKnightEquipment = new ShadowKnightEquipment(this);
        warriorEquipment = new WarriorEquipment(this);
    }

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

        return tooltip.toString();
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

        return tooltip.toString();
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

        return tooltip.toString();
    }

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
}
