package me.angeloo.mystica.Utility.Listeners;


import me.angeloo.mystica.Components.Inventories.*;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.BoardValueUpdateEvent;
import me.angeloo.mystica.CustomEvents.HelpfulHintEvent;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.InventoryIndexingManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
import org.bukkit.Bukkit;
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
    private final InventoryIndexingManager inventoryIndexingManager;
    private final BagInventory bagInventory;
    private final BuyInvSlotsInventory buyInvSlotsInventory;
    private final EquipmentInventory equipmentInventory;
    private final AbilityInventory abilityInventory;
    private final SpecInventory specInventory;
    private final ReforgeInventory reforgeInventory;
    private final UpgradeInventory upgradeInventory;
    private final IdentifyInventory identifyInventory;
    private final EquipmentInformation equipmentInformation;
    private final DisplayWeapons displayWeapons;
    private final GearReader gearReader;
    private final BossLevelInv bossLevelInv;
    private final CustomItemConverter customItemConverter;

    public InventoryEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        equipmentManager = new EquipmentManager(main);
        classSetter = main.getClassSetter();
        inventoryIndexingManager = main.getInventoryIndexingManager();
        bagInventory = main.getBagInventory();
        buyInvSlotsInventory = new BuyInvSlotsInventory(main);
        equipmentInventory = new EquipmentInventory(main);
        abilityInventory = new AbilityInventory(main);
        specInventory = new SpecInventory(main);
        reforgeInventory = new ReforgeInventory(main);
        upgradeInventory = new UpgradeInventory(main);
        identifyInventory = new IdentifyInventory();
        equipmentInformation = new EquipmentInformation();
        displayWeapons = new DisplayWeapons(main);
        gearReader = new GearReader(main);
        bossLevelInv = new BossLevelInv(main);
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

        displayWeapons.displayWeapons(player);
        displayWeapons.displayArmor(player);
        gearReader.setGearStats(player);
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
            profileManager.getAnyProfile(player).getStats().setLevelStats(profileManager.getAnyProfile(player).getStats().getLevel(), name);
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

            new BukkitRunnable(){
                @Override
                public void run(){
                    Bukkit.getServer().getPluginManager().callEvent(new HelpfulHintEvent(player));
                }
            }.runTaskLater(main, 60);


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

                    if(!bottomInv.contains(comparison)){
                        return;
                    }

                    bottomInv.remove(comparison);

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

}
