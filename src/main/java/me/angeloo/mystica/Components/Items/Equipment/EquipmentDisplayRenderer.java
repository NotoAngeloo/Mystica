package me.angeloo.mystica.Components.Items.Equipment;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EquipmentDisplayRenderer {

    private final ProfileManager profileManager;

    public EquipmentDisplayRenderer(Mystica main){
        profileManager = main.getProfileManager();
    }

    public void renderAllArmor(Player player){
        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        if(equipment.getHelmet() != null){
            player.getInventory().setHelmet(render(equipment.getHelmet()));
        }

        if(equipment.getChestPlate() != null){
            player.getInventory().setChestplate(render(equipment.getChestPlate()));
        }

        if(equipment.getLeggings() != null){
            player.getInventory().setLeggings(render(equipment.getLeggings()));
        }

        if(equipment.getBoots() != null){
            player.getInventory().setBoots(render(equipment.getBoots()));
        }
    }

    public void renderSheathedWeapons(Player player){

        player.getInventory().setItemInMainHand(renderNothing());

        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        if(equipment.getWeapon() != null){
            ItemStack main = render(equipment.getWeapon());
            ItemStack off = renderOffHandSheathed(main);
            player.getInventory().setItemInOffHand(off);
        }
    }

    public ItemStack render(MysticaEquipment item){
        Material material = resolveMaterial(item);

        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();;

        if(meta == null){
            return stack;
        }

        meta.setDisplayName(resolveDisplayName(item));

        meta.setCustomModelData(resolveModelData(item));

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        //this prevents hitting things for extra damage
        /*AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroer);*/

        stack.setItemMeta(meta);

        return stack;
    }

    public void renderOffHand(Player player){

        MysticaEquipment weapon = profileManager.getAnyProfile(player).getPlayerEquipment().getWeapon();

        if(weapon==null){
            return;
        }

        ItemStack mainHand = render(weapon);

        ItemStack offhand = mainHand.clone();
        ItemMeta meta = offhand.getItemMeta();
        if(meta==null){
            return;
        }
        int data = meta.getCustomModelData();
        data+=1;
        meta.setCustomModelData(data);
        offhand.setItemMeta(meta);
        player.getInventory().setItemInOffHand(offhand);
    }

    public ItemStack renderOffHandSheathed(ItemStack mainHand){
        ItemStack offhand = mainHand.clone();
        ItemMeta meta = offhand.getItemMeta();
        if(meta==null){
            return offhand;
        }
        int data = meta.getCustomModelData();
        data+=2;
        meta.setCustomModelData(data);
        offhand.setItemMeta(meta);
        return offhand;
    }

    public ItemStack renderNothing(){
        Material material = Material.WHITE_DYE;
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        if(meta==null){
            return stack;
        }

        meta.setDisplayName(" ");
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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

            case WEAPON -> item.getPlayerClass().getWeaponMaterial();
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

    public void clearWeapons(Player player){
        player.getInventory().setItemInMainHand(renderNothing());
        player.getInventory().setItemInOffHand(renderNothing());
    }

    public void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public void showWeapons(Player player){
        MysticaEquipment weapon = profileManager.getAnyProfile(player).getPlayerEquipment().getWeapon();

        if(weapon==null){
            clearWeapons(player);
            return;
        }

        ItemStack main = render(weapon);
        player.getInventory().setItemInMainHand(main);
        renderOffHand(player);
    }

    private String resolveDisplayName(
            MysticaEquipment item
    ) {

        return ChatColor.of(
                item.getPlayerClass().getColor()
        ) + item.getName();
    }

}
