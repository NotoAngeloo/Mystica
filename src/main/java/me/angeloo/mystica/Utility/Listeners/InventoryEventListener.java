package me.angeloo.mystica.Utility.Listeners;


import me.angeloo.mystica.Components.Inventories.*;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.InventoryIndexingManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ClassSetter;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.EquipmentInformation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryEventListener implements Listener {

    private final ProfileManager profileManager;
    private final ClassSetter classSetter;
    private final InventoryIndexingManager inventoryIndexingManager;
    private final BagInventory bagInventory;
    private final BuyInvSlotsInventory buyInvSlotsInventory;
    private final EquipmentInventory equipmentInventory;
    private final AbilityInventory abilityInventory;
    private final SpecInventory specInventory;
    private final EquipmentInformation equipmentInformation;
    private final DisplayWeapons displayWeapons;

    public InventoryEventListener(Mystica main){
        profileManager = main.getProfileManager();
        classSetter = main.getClassSetter();
        inventoryIndexingManager = main.getInventoryIndexingManager();
        bagInventory = main.getBagInventory();
        buyInvSlotsInventory = new BuyInvSlotsInventory(main);
        equipmentInventory = new EquipmentInventory(main);
        abilityInventory = new AbilityInventory(main);
        specInventory = new SpecInventory(main);
        equipmentInformation = new EquipmentInformation();
        displayWeapons = new DisplayWeapons(main);
    }

    @EventHandler
    public void setPlayerItemsInBag(InventoryCloseEvent event){
        if(!event.getView().getTitle().equals(event.getPlayer().getName() + "'s Bag")){
            return;
        }

        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        int index = inventoryIndexingManager.getBagIndex(player);

        ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (i == 8 || i == 53) {
                continue; // skip these slots
            }
            ItemStack item = inv.getItem(i);
            if (item != null && !item.getType().equals(Material.AIR)) {
                items.add(item);
            }
            else{
                //item has to be air or it breaks setting items
                items.add(new ItemStack(Material.AIR));
            }
        }
        bagInventory.addItemsToPlayerBagByInventoryClose(player, items, index);

    }

    @EventHandler
    public void gearClose(InventoryCloseEvent event){
        if(!event.getView().getTitle().equals(event.getPlayer().getName() + "'s Equipment")){
            return;
        }

        Player player = (Player) event.getPlayer();

        displayWeapons.displayWeapons(player);
        displayWeapons.displayArmor(player);
    }

    @EventHandler
    public void playerBagArrows(InventoryClickEvent event){
        if(!event.getView().getTitle().equals(event.getWhoClicked().getName() + "'s Bag")){
            return;
        }

        if(event.getClickedInventory() == null){
            return;
        }

        Inventory inv = event.getView().getTopInventory();

        if(event.getClickedInventory() != inv){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        int slot = event.getSlot();

        if(slot == 8 || slot == 53){
            event.setCancelled(true);
        }

        if(event.getCurrentItem() == null){
            return;
        }

        if(!event.getCurrentItem().hasItemMeta()){
            return;
        }


        int index = inventoryIndexingManager.getBagIndex(player);

        switch (event.getCurrentItem().getItemMeta().getDisplayName()){
            case "Scroll Up":{
                if(index == 0){
                    return;
                }
                index--;
                player.openInventory(bagInventory.openBagInventory(player, index));
                inventoryIndexingManager.setBagIndex(player, index);
                break;
            }
            case "Scroll Down": {
                //scroll down, if in range
                int range = profileManager.getAnyProfile(player).getPlayerBag().getNumUnlocks();
                if(range <= index){
                    player.openInventory(buyInvSlotsInventory.openBuyInv(player));
                    return;
                }

                index++;
                player.openInventory(bagInventory.openBagInventory(player, index));
                inventoryIndexingManager.setBagIndex(player, index);
                break;
            }
        }
    }

    @EventHandler
    public void buyMoreSlots(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Purchase More Space?")){
            return;
        }
        event.setCancelled(true);

        if(event.getClickedInventory() == null){
            return;
        }

        if(event.getInventory().getItem(event.getSlot()) == null){
            return;
        }

        ItemStack item = event.getCurrentItem();

        if(item == null){
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int index = inventoryIndexingManager.getBagIndex(player);

        int numLocks = profileManager.getAnyProfile(player).getPlayerBag().getNumUnlocks();
        int price = (20 + (20 * numLocks));

        if(item.getItemMeta().getDisplayName().equalsIgnoreCase("Buy")){

            int bal = profileManager.getAnyProfile(player).getPoints().getBal();

            if(bal < price){
                player.sendMessage("You cannot afford");
                return;
            }

            player.sendMessage("Purchase Successful");
            profileManager.getAnyProfile(player).getPoints().setBal(bal - price);
            profileManager.getAnyProfile(player).getPlayerBag().setNumUnlocks(numLocks + 1);
            player.openInventory(buyInvSlotsInventory.openBuyInv(player));
        }

        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Cancel")){
            player.openInventory(bagInventory.openBagInventory(player, index));
        }
    }

    @EventHandler
    public void gearClick(InventoryClickEvent event){
        if(!event.getView().getTitle().contains(event.getWhoClicked().getName()+ "'s Equipment")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory() == null){
            return;
        }

        ItemStack item = event.getCurrentItem();

        if(item == null){
            return;
        }

        Inventory topInv = event.getView().getTopInventory();

        if(event.getClickedInventory() == topInv){

            int slot = event.getSlot();

            List<Integer> equipmentSlots = new ArrayList<>();
            equipmentSlots.add(5);
            equipmentSlots.add(7);
            equipmentSlots.add(13);
            equipmentSlots.add(14);
            equipmentSlots.add(15);
            equipmentSlots.add(16);

            if(equipmentSlots.contains(slot)){
                player.openInventory(equipmentInventory.openEquipmentInventory(player, item, true));
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

            switch(item.getItemMeta().getDisplayName().toLowerCase()){
                case "remove":{
                    removeEquipment(player, equipSlot);
                    break;
                }
                case "equip":{
                    addEquipmentIfValid(player, equipSlot);
                    break;
                }

            }

            displayWeapons.displayWeapons(player);
            displayWeapons.displayArmor(player);
            return;
        }

        List<Material> validEquipment = equipmentInformation.getAllEquipmentTypes();
        Material itemType = item.getType();

        if(!validEquipment.contains(itemType)){
            return;
        }

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
            player.openInventory(equipmentInventory.openEquipmentInventory(player, item, false));
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
            case "main hand":{
                player.getInventory().addItem(equipment.getWeapon());
                equipment.setWeapon(null);
                break;
            }
            case "secondary":{
                player.getInventory().addItem(equipment.getOffhand());
                equipment.setOffhand(null);
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

        player.openInventory(equipmentInventory.openEquipmentInventory(player, null, false));
    }

    private void addEquipmentIfValid(Player player, String equipSlot){

        Inventory inventory = player.getOpenInventory().getTopInventory();
        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();
        ItemStack selectedItem = inventory.getItem(10);

        //instead of returning if not null, swap the items

        switch (equipSlot.toLowerCase()){
            case "main hand":{
                if(inventory.getItem(5) != null){
                    return;
                }
                equipment.setWeapon(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
            case "secondary":{
                if(inventory.getItem(7) != null){
                    return;
                }
                equipment.setOffhand(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
            case "helmet":{
                if(inventory.getItem(13) != null){
                    return;
                }
                equipment.setHelmet(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
            case "chestplate":{
                if(inventory.getItem(14) != null){
                    return;
                }
                equipment.setChestPlate(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
            case "leggings":{
                if(inventory.getItem(15) != null){
                    return;
                }
                equipment.setLeggings(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
            case "boots":{
                if(inventory.getItem(16) != null){
                    return;
                }
                equipment.setBoots(selectedItem);
                assert selectedItem != null;
                player.getInventory().remove(selectedItem);
                break;
            }
        }
        player.openInventory(equipmentInventory.openEquipmentInventory(player, null, false));
    }


    @EventHandler
    public void activeSkills(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase("Active Skill")) {
            return;
        }
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory() == null){
            return;
        }
        Inventory topInv = event.getView().getTopInventory();

        if(event.getClickedInventory() != topInv){
            return;
        }

        int slot = event.getSlot();

        ItemStack item = event.getCurrentItem();

        if(item != null){
            switch(item.getItemMeta().getDisplayName().toLowerCase()){
                case "specializations":{
                    player.openInventory(specInventory.openSpecInventory(player));
                    return;
                }
                case "see equipped skills":{
                    player.openInventory(abilityInventory.openAbilityInventory(player, null, true));
                    return;
                }
                case "hide equipped skills":{
                    player.openInventory(abilityInventory.openAbilityInventory(player, null, false));
                    return;
                }
            }
        }

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        List<Integer> skillSlots = new ArrayList<>();
        for(int i=0;i<=8;i++){
            skillSlots.add(i);
        }

        if(skillSlots.contains(slot)){

            int skillNumber = slot + 1;

            if(equipSkills.whichSlotIsTheSkillEquippedIn(skillNumber) != -1){
                equipSkills.setAnySlot(equipSkills.whichSlotIsTheSkillEquippedIn(skillNumber), 0);
                player.openInventory(abilityInventory.openAbilityInventory(player, item, true));
                return;
            }


            player.openInventory(abilityInventory.openAbilityInventory(player, item, true));
            return;
        }

        List<Integer> equipSlots = new ArrayList<>();
        for(int i=36;i<=43;i++){
            equipSlots.add(i);
        }
        //not 44, its statically ultimate

        if(item != null && equipSlots.contains(slot)){

            int putTheSkillHere = slot - 36;
            equipSkills.setAnySlot(putTheSkillHere, 0);

            ItemStack seeSkills = topInv.getItem(22);
            assert seeSkills != null;
            String name = seeSkills.getItemMeta().getDisplayName();

            if(name.equalsIgnoreCase("Hide Equipped Skills")){
                player.openInventory(abilityInventory.openAbilityInventory(player, null, true));
            }
            else{
                player.openInventory(abilityInventory.openAbilityInventory(player, null, false));
            }
            return;
        }

        ItemStack selectedItem = topInv.getItem(18);

        if(selectedItem == null){
            return;
        }

        String selectedName = selectedItem.getItemMeta().getDisplayName();
        selectedName = selectedName.replaceAll("§.", "");

        int target = 0;

        for(int i=0; i<topInv.getSize(); i++){

            if(i==18){
                continue;
            }

            ItemStack current = topInv.getItem(i);

            if(current == null){
                continue;
            }

            String abilityName = current.getItemMeta().getDisplayName();
            abilityName = abilityName.replaceAll("§.", "");
            if(abilityName.equalsIgnoreCase(selectedName)){
                target = i;
                break;
            }
        }


        int skillNumber = target + 1;

        if(equipSlots.contains(slot)){
            int putTheSkillHere = slot - 36;
            equipSkills.setAnySlot(putTheSkillHere, skillNumber);

            //check slot 22 first
            ItemStack seeSkills = topInv.getItem(22);
            assert seeSkills != null;
            String name = seeSkills.getItemMeta().getDisplayName();

            if(name.equalsIgnoreCase("Hide Equipped Skills")){
                player.openInventory(abilityInventory.openAbilityInventory(player, null, true));
            }
            else{
                player.openInventory(abilityInventory.openAbilityInventory(player, null, false));
            }

        }
    }

    @EventHandler
    public void specSelector(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase("Specializations")) {
            return;
        }
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory() == null){
            return;
        }

        Inventory inv = event.getView().getTopInventory();

        if(event.getClickedInventory() != inv){
            return;
        }

        int slot = event.getSlot();

        ItemStack item = event.getCurrentItem();

        if(item == null){
            return;
        }

        //first check if valid item
        List<Integer> validSlots = new ArrayList<>();
        validSlots.add(11);
        validSlots.add(13);
        validSlots.add(15);

        String name = event.getCurrentItem().getItemMeta().getDisplayName();

        if(validSlots.contains(slot)){
            name = name.replaceAll("§.", "");

            profileManager.getAnyProfile(player).setPlayerSubclass(name);
            player.openInventory(specInventory.openSpecInventory(player));
            return;
        }

        if(name.equalsIgnoreCase("back")){
            player.openInventory(abilityInventory.openAbilityInventory(player, null, false));
        }

    }

    @EventHandler
    public void classSelector(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Select a Class")){
            return;
        }
        event.setCancelled(true);

        if(event.getClickedInventory() == null){
            return;
        }

        Inventory inv = event.getView().getTopInventory();

        if(event.getClickedInventory() != inv){
            return;
        }

        if(event.getInventory().getItem(event.getSlot()) == null){
            return;
        }

        ItemStack item = event.getCurrentItem();

        if(item == null){
            return;
        }

        if(!event.getCurrentItem().hasItemMeta()){
            return;
        }

        if(!event.getCurrentItem().getItemMeta().hasDisplayName()){
            return;
        }

        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        name = name.replaceAll("§.", "");

        ItemStack classItem = event.getInventory().getItem(4);
        String className = "None";

        if(classItem != null){
            className = classItem.getItemMeta().getDisplayName();
            className = className.replaceAll("§.", "");
        }

        Player player = (Player) event.getWhoClicked();

        if (name.equalsIgnoreCase("select")) {
            if (classItem == null) {
                return;
            }

            if (className.equals("None")) {
                return;
            }

            classSetter.setClass(player, className);
            player.closeInventory();
            return;
        }

        player.openInventory(new ClassSelectInventory().openClassSelect(name));

    }

}
