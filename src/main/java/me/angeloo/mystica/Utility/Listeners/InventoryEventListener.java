package me.angeloo.mystica.Utility.Listeners;


import me.angeloo.mystica.Components.Inventories.*;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.InventoryIndexingManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class InventoryEventListener implements Listener {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final EquipmentManager equipmentManager;

    private final InventoryIndexingManager inventoryIndexingManager;
    private final BagInventory bagInventory;
    private final BuyInvSlotsInventory buyInvSlotsInventory;
    private final ReforgeInventory reforgeInventory;
    private final UpgradeInventory upgradeInventory;
    private final IdentifyInventory identifyInventory;
    private final EquipmentInformation equipmentInformation;
    private final DisplayWeapons displayWeapons;
    private final GearReader gearReader;
    private final BossLevelInv bossLevelInv;
    private final Locations locations;
    private final CustomItemConverter customItemConverter;

    public InventoryEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        equipmentManager = new EquipmentManager(main);
        inventoryIndexingManager = main.getInventoryIndexingManager();
        bagInventory = main.getBagInventory();
        buyInvSlotsInventory = new BuyInvSlotsInventory(main);
        reforgeInventory = new ReforgeInventory(main);
        upgradeInventory = new UpgradeInventory(main);
        identifyInventory = new IdentifyInventory(main);
        equipmentInformation = new EquipmentInformation();
        displayWeapons = new DisplayWeapons(main);
        gearReader = new GearReader(main);
        bossLevelInv = new BossLevelInv(main);
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
