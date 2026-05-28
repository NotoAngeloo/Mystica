package me.angeloo.mystica.Components.Items.Equipment;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class EquipmentDisplayRenderer {

    public ItemStack render(MysticaEquipment item){
        Material material = resolveMaterial(item);

        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();;

        if(meta == null){
            return stack;
        }

        meta.setDisplayName(
                resolveDisplayName(item)
        );

        meta.setCustomModelData(resolveModelData(item));

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        //this prevents hitting things for extra damage
        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroer);

        stack.setItemMeta(meta);

        return stack;
    }

    private Material resolveMaterial(
            MysticaEquipment item
    ) {

        return switch (item.getSlot()) {

            case HEAD ->
                    Material.CHAIN;

            case CHEST ->
                    Material.CHAINMAIL_CHESTPLATE;

            case LEGS ->
                    Material.CHAINMAIL_LEGGINGS;

            case BOOTS ->
                    Material.CHAINMAIL_BOOTS;

            case WEAPON -> switch (
                    item.getPlayerClass()
                    ) {

                case RANGER ->
                        Material.FEATHER;

                case ELEMENTALIST ->
                        Material.STICK;

                case MYSTIC ->
                        Material.BLAZE_ROD;

                case ASSASSIN ->
                        Material.FLINT;

                case SHADOW_KNIGHT ->
                        Material.DIAMOND_SWORD;

                case WARRIOR ->
                        Material.BRICK;

                case PALADIN ->
                        Material.IRON_SWORD;

                default ->
                        Material.BARRIER;
            };
        };
    }

    private int resolveModelData(
            MysticaEquipment item
    ) {

        return switch (
                item.getPlayerClass()
                ) {

            case ELEMENTALIST -> 1;
            case RANGER -> 2;
            case MYSTIC -> 3;
            case SHADOW_KNIGHT -> 4;
            case PALADIN -> 5;
            case WARRIOR -> 6;
            case ASSASSIN -> 7;

            default -> 0;
        };


    }

    private String resolveDisplayName(
            MysticaEquipment item
    ) {

        return ChatColor.of(
                item.getPlayerClass().getColor()
        ) + item.getName();
    }

}
