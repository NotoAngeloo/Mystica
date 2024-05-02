package me.angeloo.mystica.Utility.Listeners;


import me.angeloo.mystica.Components.Inventories.*;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.HelpfulHintEvent;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.InventoryIndexingManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Managers.QuestManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryEventListener implements Listener {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final EquipmentManager equipmentManager;
    private final ClassSetter classSetter;
    private final ClassSwapper classSwapper;
    private final QuestManager questManager;
    private final InventoryIndexingManager inventoryIndexingManager;
    private final BagInventory bagInventory;
    private final QuestInventory questInventory;
    private final BuyInvSlotsInventory buyInvSlotsInventory;
    private final EquipmentInventory equipmentInventory;
    private final AbilityInventory abilityInventory;
    private final ReforgeInventory reforgeInventory;
    private final UpgradeInventory upgradeInventory;
    private final IdentifyInventory identifyInventory;
    private final EquipmentInformation equipmentInformation;
    private final DisplayWeapons displayWeapons;
    private final GearReader gearReader;
    private final BossLevelInv bossLevelInv;
    private final FastTravelInv fastTravelInv;
    private final Locations locations;
    private final CustomItemConverter customItemConverter;

    public InventoryEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        equipmentManager = new EquipmentManager(main);
        questManager = main.getQuestManager();
        classSetter = main.getClassSetter();
        classSwapper = main.getClassSwapper();
        inventoryIndexingManager = main.getInventoryIndexingManager();
        bagInventory = main.getBagInventory();
        questInventory = main.getQuestInventory();
        buyInvSlotsInventory = new BuyInvSlotsInventory(main);
        equipmentInventory = new EquipmentInventory(main);
        abilityInventory = new AbilityInventory(main);
        reforgeInventory = new ReforgeInventory(main);
        upgradeInventory = new UpgradeInventory(main);
        identifyInventory = new IdentifyInventory();
        equipmentInformation = new EquipmentInformation();
        displayWeapons = new DisplayWeapons(main);
        gearReader = new GearReader(main);
        bossLevelInv = new BossLevelInv(main);
        fastTravelInv = new FastTravelInv(main);
        locations = new Locations(main);
        customItemConverter = new CustomItemConverter();
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

        new BukkitRunnable(){
            @Override
            public void run(){
                displayWeapons.displayWeapons(player);
                displayWeapons.displayArmor(player);
                gearReader.setGearStats(player);
            }
        }.runTaskLater(main, 1);

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
    public void questBookClick(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Quests")){
            return;
        }

        event.setCancelled(true);

        if(event.getClickedInventory() == null){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        Inventory inventory = player.getOpenInventory().getTopInventory();

        if(event.getClickedInventory() != inventory){
            return;
        }

        ItemStack item = event.getCurrentItem();

        if(item == null){
            return;
        }

        int index = inventoryIndexingManager.getBagIndex(player);

        switch (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase()){
            /*case "Scroll Up":{
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
            }*/
            case "a helping hand":{
                questManager.rereadQuest(player, "helping_hand");
                break;
            }
            case "the archbishop's request":{
                questManager.rereadQuest(player,"sewer");
                break;
            }
            case "heart of corruption":{
                questManager.rereadQuest(player, "sewer2");
                break;
            }
            case "cave of the lindwyrm":{
                questManager.rereadQuest(player, "lindwyrm");
                break;
            }
            case "the general's arrival":{
                questManager.rereadQuest(player, "ho_lee");
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

            int bal = profileManager.getAnyProfile(player).getBal().getBal();

            if(bal < price){
                player.sendMessage("You cannot afford");
                return;
            }

            player.sendMessage("Purchase Successful");
            profileManager.getAnyProfile(player).getBal().setBal(bal - price);
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

        if(!profileManager.getAnyProfile(player).getMilestones().getMilestone("tutorial")){
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
        displayWeapons.displayWeapons(player);
        displayWeapons.displayArmor(player);
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

        ItemStack item = event.getCurrentItem();

        if(item == null){
            return;
        }

        if(!item.hasItemMeta()){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        String name = item.getItemMeta().getDisplayName();
        name = name.replaceAll("§.", "");

        ItemStack classItem = event.getView().getTopInventory().getItem(13);
        assert classItem != null;
        String className = classItem.getItemMeta().getDisplayName().replaceAll("§.", "");

        if (name.equalsIgnoreCase("select")) {
            classSetter.setClass(player, className);
            player.closeInventory();
            return;
        }

        int index = inventoryIndexingManager.getClassIndex(player);

        if(name.equalsIgnoreCase("next")){
            index++;
        }

        if(name.equalsIgnoreCase("previous")){
            index--;
        }

        if(index<0){
            index = 6;
        }

        if(index>6){
            index = 0;
        }

        inventoryIndexingManager.setClassIndex(player, index);

        player.openInventory(new ClassSelectInventory().openClassSelect(index));

    }

    @EventHandler
    public void classSwapper(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Swap Class")){
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

        ItemStack item = event.getCurrentItem();

        if(item == null){
            return;
        }

        if(!item.hasItemMeta()){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        String name = item.getItemMeta().getDisplayName();
        name = name.replaceAll("§.", "");

        ItemStack classItem = event.getView().getTopInventory().getItem(13);
        assert classItem != null;
        String className = classItem.getItemMeta().getDisplayName().replaceAll("§.", "");

        if (name.equalsIgnoreCase("select")) {

            if(classSwapper.hasEquipment(player)){
                player.sendMessage("Remove your Equipment");
                return;
            }

            if(profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase(className)){
                player.sendMessage("You are already this class");
                return;
            }

            classSwapper.swapClass(player, className);
            player.closeInventory();
            return;
        }

        int index = inventoryIndexingManager.getClassIndex(player);

        if(name.equalsIgnoreCase("next")){
            index++;
        }

        if(name.equalsIgnoreCase("previous")){
            index--;
        }

        if(index<0){
            index = 6;
        }

        if(index>6){
            index = 0;
        }

        inventoryIndexingManager.setClassIndex(player, index);

        player.openInventory(new ClassSelectInventory().openClassSwap(index));

    }

    @EventHandler
    public void IdentifyClick(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Identify")){
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

        ItemStack old = topInv.getItem(13);

        if(event.getClickedInventory() == topInv){

            if(old == null){
                return;
            }

            if(old.getType() != Material.IRON_INGOT){
                return;
            }

            if(!old.hasItemMeta()){
                return;
            }

            String colorlessName = old.getItemMeta().getDisplayName().replaceAll("§.", "");

            if(!colorlessName.equalsIgnoreCase("unidentified equipment")){
                return;
            }

            int slot = event.getSlot();

            switch (slot){
                case 22:{

                    //check for stones here

                    //check level
                    ItemMeta meta = old.getItemMeta();
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

                    if(level == 0){
                        return;
                    }

                    player.getInventory().remove(old);
                    player.getInventory().addItem(equipmentManager.generate(player,level));
                    player.openInventory(identifyInventory.openIdentifyInventory(new ItemStack(Material.AIR)));
                    return;
                }

            }

            return;
        }

        if(!item.hasItemMeta()){
            return;
        }

        String colorlessString = item.getItemMeta().getDisplayName().replaceAll("§.", "");

        if(!colorlessString.equalsIgnoreCase("unidentified equipment")){
            return;
        }

        player.openInventory(identifyInventory.openIdentifyInventory(item));
    }

    @EventHandler
    public void reforgeClick(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Reforge")){
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
        Inventory bottomInv = event.getView().getBottomInventory();

        ItemStack old = topInv.getItem(11);

        if(event.getClickedInventory() == topInv){

            if(old == null){
                return;
            }

            int slot = event.getSlot();

            switch (slot){
                case 13:{

                    //check for stones here
                    ItemStack comparison = customItemConverter.convert(new SoulStone(), 1);

                    ItemStack stone = new ItemStack(Material.LAPIS_LAZULI);
                    boolean hasItem = false;
                    for(ItemStack thisItem : bottomInv.getContents()){

                        if(thisItem == null){
                            continue;
                        }

                        if(thisItem.isSimilar(comparison)){
                            hasItem = true;
                            stone = thisItem;
                            break;
                        }

                    }

                    if(!hasItem){
                        return;
                    }

                    if(stone.getAmount() >= 1){
                        stone.setAmount(stone.getAmount() - 1);
                    }


                    player.openInventory(reforgeInventory.openReforgeInventory(player, old, true));
                    return;
                }
                case 11:{
                    player.openInventory(reforgeInventory.openReforgeInventory(player, null, false));
                    return;
                }
                case 15:{

                    ItemStack newItem = event.getView().getTopInventory().getItem(15);

                    assert newItem != null;
                    if(newItem.getType().equals(Material.AIR)){
                        return;
                    }

                    player.getInventory().remove(old);
                    player.getInventory().addItem(newItem);
                    player.openInventory(reforgeInventory.openReforgeInventory(player, null, false));
                    return;
                }
            }


            return;
        }

        List<Material> validEquipment = equipmentInformation.getAllEquipmentTypes();
        Material itemType = item.getType();

        if(!validEquipment.contains(itemType)){
            return;
        }

        //add it to the inv

        player.openInventory(reforgeInventory.openReforgeInventory(player, item, false));

    }

    @EventHandler
    public void gearSwapClick(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Convert Equipment")){
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
        Inventory bottomInv = event.getView().getBottomInventory();


       if(event.getClickedInventory()==bottomInv){
           List<Material> validEquipment = equipmentInformation.getAllEquipmentTypes();
           Material itemType = item.getType();

           if(!validEquipment.contains(itemType)){
               return;
           }

           if(!isInventoryFull(topInv)){
               bottomInv.remove(item);
               topInv.addItem(item);
           }

       }

        List<Material> validEquipment = equipmentInformation.getAllEquipmentTypes();
        Material itemType = item.getType();

        if(event.getClickedInventory()==topInv){

            if(item.getItemMeta().getDisplayName().equalsIgnoreCase("swap")){
                //swap all items
                for (ItemStack equipment : topInv.getContents()){

                    if(equipment == null){
                        continue;
                    }

                    if(!validEquipment.contains(equipment.getType())){
                        continue;
                    }

                    topInv.remove(equipment);
                    topInv.addItem(equipmentManager.swap(player, equipment));

                }

                return;
            }



            if(!validEquipment.contains(itemType)){
                return;
            }

            if(!isInventoryFull(bottomInv)){
                topInv.remove(item);
                bottomInv.addItem(item);
            }

        }


    }

    @EventHandler
    public void gearSwapClose(InventoryCloseEvent event){
        if(!event.getView().getTitle().equals("Convert Equipment")){
            return;
        }

        Player player = (Player) event.getPlayer();


        Inventory topInv = event.getView().getTopInventory();

        List<Material> validEquipment = equipmentInformation.getAllEquipmentTypes();
        for(ItemStack item : topInv.getContents()){

            if(item == null){
                continue;
            }

            Material itemType = item.getType();

            if(!validEquipment.contains(itemType)){
                continue;
            }

            player.getInventory().addItem(item);
        }


    }

    private boolean isInventoryFull(Inventory inventory) {
        int occupiedSlots = 0;
        int totalSlots = inventory.getSize();

        for (ItemStack item : inventory.getContents()) {
            if (item != null && !item.getType().isAir()) {
                occupiedSlots++;
            }
        }

        return occupiedSlots >= totalSlots;
    }

    @EventHandler
    public void upgradeClick(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Upgrade")){
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

        ItemStack selected = topInv.getItem(9);
        if(selected == null){
            selected = new ItemStack(Material.AIR);
        }

        ItemStack oldItem = topInv.getItem(11);
        if(oldItem == null){
            oldItem = new ItemStack(Material.AIR);
        }

        ItemStack fodder = topInv.getItem(13);
        if(fodder == null){
            fodder = new ItemStack(Material.AIR);
        }

        ItemStack newItem = topInv.getItem(15);
        if(newItem == null){
            newItem = new ItemStack(Material.AIR);
        }

        if(event.getClickedInventory() == topInv){

            int slot = event.getSlot();

            switch (slot){
                case 20:{
                    if(selected.getType() == Material.AIR){
                        return;
                    }
                    player.openInventory(upgradeInventory.openUpgradeInventory(player, new ItemStack(Material.AIR), selected, fodder));
                    return;
                }
                case 22:{
                    if(selected.getType() == Material.AIR){
                        return;
                    }
                    player.openInventory(upgradeInventory.openUpgradeInventory(player, new ItemStack(Material.AIR), oldItem, selected));
                    return;
                }
                case 11:{
                    player.openInventory(upgradeInventory.openUpgradeInventory(player, new ItemStack(Material.AIR), new ItemStack(Material.AIR), fodder));
                    return;
                }
                case 13:{
                    player.openInventory(upgradeInventory.openUpgradeInventory(player, new ItemStack(Material.AIR), oldItem, new ItemStack(Material.AIR)));
                    return;
                }
                case 15:{
                    if(newItem.getType() == Material.AIR){
                        return;
                    }

                    if(newItem.getType() == Material.RED_DYE){
                        return;
                    }

                    player.getInventory().remove(oldItem);
                    player.getInventory().remove(fodder);
                    player.getInventory().addItem(newItem);
                    player.openInventory(upgradeInventory.openUpgradeInventory(player, new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)));
                    return;
                }
            }

            return;
        }

        List<Material> validEquipment = equipmentInformation.getAllEquipmentTypes();
        Material itemType = item.getType();

        if(!validEquipment.contains(itemType)){
            return;
        }

        //check duplicate protection here too, cannot put in an item that is in already
        List<ItemStack> currentItems = new ArrayList<>();
        currentItems.add(topInv.getItem(11));
        currentItems.add(topInv.getItem(13));

        if(currentItems.contains(item)){
            player.sendMessage("Item already present");
            return;
        }

        player.openInventory(upgradeInventory.openUpgradeInventory(player, item, oldItem, fodder));

        //add to whichever slot is available, if any

    }

    @EventHandler
    public void onBossInvClick(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Change Boss Level")) {
            return;
        }
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory() == null){
            return;
        }

        if(event.getClickedInventory().getHolder() instanceof Player){
            //no player inv stuff
            return;
        }

        if(event.getInventory().getItem(event.getSlot()) == null){
            return;
        }

        if(!event.getClick().isLeftClick() && !event.getClick().isRightClick()){
            //only left clicks allowed in this fine establishment
            return;
        }

        int level = profileManager.getAnyProfile(player).getPlayerBossLevel().getBossLevel();

        switch ((event.getCurrentItem().getItemMeta().getDisplayName())) {
            case "Decrease":{
                if(level <=1){
                    player.sendMessage("You cannot go any lower");
                    break;
                }
                profileManager.getAnyProfile(player).getPlayerBossLevel().setBossLevel(level - 1);
                break;
            }
            case "Increase":{
                profileManager.getAnyProfile(player).getPlayerBossLevel().setBossLevel(level + 1);
                break;
            }
        }

        player.openInventory(bossLevelInv.openBossLevelInv(player));
    }

    @EventHandler
    public void onFastTravelClick(InventoryClickEvent event){
        if(!event.getView().getTitle().equals("Fast Travel")) {
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

        ItemStack item = event.getCurrentItem();

        if(item == null){
            return;
        }

        ItemStack selected = topInv.getItem(21);
        assert selected != null;

        switch ((event.getCurrentItem().getItemMeta().getDisplayName()).toLowerCase()) {
            case " ":{
                break;
            }
            case "buy":{

                if(selected.getType().isAir()){
                    return;
                }

                //check money first

                player.getInventory().addItem(selected);

                break;
            }
            case "teleport":{

                if(selected.getType().isAir()){
                    return;
                }

                Location closest = locations.getNearestLocation(player);

                String colorlessName = selected.getItemMeta().getDisplayName().replaceAll("§.", "");

                switch (colorlessName.toLowerCase()){
                    case "teleport: stonemont":{

                        //this isnt working properly
                        if(closest != locations.stonemont()){
                            player.teleport(locations.stonemont());
                        }

                        break;
                    }
                    case "teleport: cave of the lindwyrm":{

                        if(closest != locations.caveOfLindwyrm()){
                            player.teleport(locations.caveOfLindwyrm());
                        }

                        break;
                    }
                    case "teleport: windbluff prison":{

                        if(closest != locations.windbluff()){
                            player.teleport(locations.windbluff());
                        }

                        break;
                    }
                    case "teleport: traders outpost":{

                        if(closest != locations.outpost()){
                            player.teleport(locations.outpost());
                        }

                        break;
                    }
                }

                player.closeInventory();
                return;
            }
            case "set respawn":{
                Location closest = locations.getNearestLocation(player);
                player.getWorld().setSpawnLocation(closest);
                break;
            }
            default:{
                player.openInventory(fastTravelInv.openFastTravelInv(player, item));
                return;
            }
        }

        player.openInventory(fastTravelInv.openFastTravelInv(player, selected));
    }


}
