package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EquipmentInventory implements Listener {

    private final ProfileManager profileManager;
    private final DisplayWeapons displayWeapons;

    public EquipmentInventory(Mystica main) {
        profileManager = main.getProfileManager();
        displayWeapons = new DisplayWeapons(main);
    }


    public Inventory openEquipmentInventory(Player player, ItemStack actionItem, boolean fromTop) {
        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.WHITE + "\uF808\uE066\uF828");

        inv.setItem(14, playerEquipment.getWeapon());
        inv.setItem(22, playerEquipment.getHelmet());
        inv.setItem(23, playerEquipment.getChestPlate());
        inv.setItem(24, playerEquipment.getLeggings());
        inv.setItem(25, playerEquipment.getBoots());

        if (actionItem == null) {
            return inv;
        }

        inv.setItem(10, actionItem);

        inv.setItem(48, getAddItem());

        if (fromTop) {
            inv.setItem(48, getRemoveItem());
        }

        return inv;
    }

    @EventHandler
    public void gearClick(InventoryClickEvent event){
        if(!event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF808\uE066\uF828")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory() == null){
            return;
        }

        ItemStack item = event.getCurrentItem();


        Inventory topInv = event.getView().getTopInventory();

        if(event.getClickedInventory() == topInv){

            int slot = event.getSlot();

            List<Integer> equipmentSlots = new ArrayList<>();
            equipmentSlots.add(14);
            equipmentSlots.add(22);
            equipmentSlots.add(23);
            equipmentSlots.add(24);
            equipmentSlots.add(25);

            if(equipmentSlots.contains(slot)){
                if(item == null){
                    return;
                }
                player.openInventory(openEquipmentInventory(player, item, true));
                return;
            }

            ItemStack selectedItem = event.getInventory().getItem(10);

            if(selectedItem == null){
                return;
            }

            ItemMeta selectedMeta = selectedItem.getItemMeta();
            assert selectedMeta != null;
            List<String> lore = selectedMeta.getLore();
            assert lore != null;
            String equipSlot = lore.get(1);
            equipSlot = equipSlot.replaceAll("§.", "");

            List<Integer> actionSlots = new ArrayList<>();
            actionSlots.add(47);
            actionSlots.add(46);
            actionSlots.add(45);

            if(actionSlots.contains(slot)){
                ItemStack actionItem = event.getClickedInventory().getItem(48);

                if(actionItem == null){
                    return;
                }

                switch(actionItem.getItemMeta().getCustomModelData()){
                    case 4:{
                        removeEquipment(player, equipSlot);
                        break;
                    }
                    case 3:{
                        addEquipmentIfValid(player, equipSlot);
                        break;
                    }

                }

                displayWeapons.displayWeapons(player);
                displayWeapons.displayArmor(player);
                return;
            }


        }

        //List<Material> validEquipment = equipmentInformation.getAllEquipmentTypes();
        assert item != null;
        Material itemType = item.getType();

        /*if(!validEquipment.contains(itemType)){
            return;
        }*/

        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        List<String> lores = meta.getLore();

        String requirement = "none";

        assert lores != null;
        for(String lore : lores){
            if(lore.contains("Requires")){
                requirement = lore;
                requirement = requirement.replaceAll("§.", "");
                requirement = requirement.replaceAll("Requires ", "");
            }
        }

        String clazz = profileManager.getAnyProfile(player).getPlayerClass();

        if(requirement.equalsIgnoreCase("none") || requirement.equalsIgnoreCase(clazz)){
            player.openInventory(openEquipmentInventory(player, item, false));
            return;
        }

        player.sendMessage("you do not meet the requirements");
    }

    private void removeEquipment(Player player, String equipSlot){

        boolean isInventoryFull = player.getInventory().firstEmpty() == -1;

        if(isInventoryFull){
            player.sendMessage("inventory full");
            return;
        }

        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();
        switch (equipSlot.toLowerCase()){
            case "weapon":{
                player.getInventory().addItem(equipment.getWeapon());
                equipment.setWeapon(null);
                break;
            }
            case "helmet":{
                player.getInventory().addItem(equipment.getHelmet());
                equipment.setHelmet(null);
                break;
            }
            case "chestplate":{
                player.getInventory().addItem(equipment.getChestPlate());
                equipment.setChestPlate(null);
                break;
            }
            case "leggings":{
                player.getInventory().addItem(equipment.getLeggings());
                equipment.setLeggings(null);
                break;
            }
            case "boots":{
                player.getInventory().addItem(equipment.getBoots());
                equipment.setBoots(null);
                break;
            }
        }

        player.openInventory(openEquipmentInventory(player, null, false));
    }

    private void addEquipmentIfValid(Player player, String equipSlot){

        Inventory inventory = player.getOpenInventory().getTopInventory();
        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();
        ItemStack selectedItem = inventory.getItem(10);

        //instead of returning if not null, swap the items

        switch (equipSlot.toLowerCase()){
            case "weapon":{
                if(inventory.getItem(14) != null){
                    return;
                }
                equipment.setWeapon(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
            case "helmet":{
                if(inventory.getItem(22) != null){
                    return;
                }
                equipment.setHelmet(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
            case "chestplate":{
                if(inventory.getItem(23) != null){
                    return;
                }
                equipment.setChestPlate(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
            case "leggings":{
                if(inventory.getItem(24) != null){
                    return;
                }
                equipment.setLeggings(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
            case "boots":{
                if(inventory.getItem(25) != null){
                    return;
                }
                equipment.setBoots(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
        }
        player.openInventory(openEquipmentInventory(player, null, false));
        displayWeapons.displayWeapons(player);
        displayWeapons.displayArmor(player);
    }

    private ItemStack getRemoveItem(){
        return getItem(Material.EMERALD, 4,
                " ");
    }

    private ItemStack getAddItem(){
        return getItem(Material.EMERALD, 3,
                " ");
    }

    private ItemStack getItem(Material material, int modelData, String name, String ... lore){
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);

        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }

}

