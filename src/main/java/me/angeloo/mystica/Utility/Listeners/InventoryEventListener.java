package me.angeloo.mystica.Utility.Listeners;


import me.angeloo.mystica.Components.Inventories.*;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryEventListener implements Listener {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final EquipmentManager equipmentManager;

    private final CustomInventoryManager customInventoryManager;
    private final DisplayWeapons displayWeapons;
    private final BossLevelInv bossLevelInv;
    private final Locations locations;
    private final CustomItemConverter customItemConverter;

    public InventoryEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        equipmentManager = new EquipmentManager(main);
        customInventoryManager = main.getInventoryIndexingManager();
        displayWeapons = new DisplayWeapons(main);
        bossLevelInv = new BossLevelInv(main);
        locations = new Locations(main);
        customItemConverter = new CustomItemConverter();
    }


    @EventHandler
    public void menuClick(InventoryClickEvent event){

        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            return;
        }

        String title = event.getView().getTitle();

        if(!title.equalsIgnoreCase("crafting")){
            return;
        }

        if(clickedInv.getType() != InventoryType.PLAYER){
            return;
        }

        Player player = (Player) event.getWhoClicked();

        //Bukkit.getLogger().info("Slot: " + event.getSlot());

        if(event.getSlot() == 0){

            profileManager.getAnyProfile(player).getMysticaBagCollection().openMysticaBag(player, 0);

            return;
        }

    }

    @EventHandler
    public void guiClose(InventoryCloseEvent event){

        Player player = (Player) event.getPlayer();


        if(profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }


        player.getInventory().clear();
    }

    @EventHandler
    public void clickBagSlot(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE05C")){
            return;
        }

        Inventory clickedInv = event.getClickedInventory();
        Inventory botInv = event.getView().getBottomInventory();

        if(clickedInv != botInv){
            return;
        }

        List<Integer> bagSlots = new ArrayList<>();
        bagSlots.add(0);
        bagSlots.add(1);
        bagSlots.add(2);
        bagSlots.add(3);
        bagSlots.add(4);
        bagSlots.add(5);
        bagSlots.add(6);
        bagSlots.add(7);
        bagSlots.add(8);

        if(!bagSlots.contains(event.getSlot())){
            return;
        }

        event.setCancelled(true);

    }


    /*@EventHandler
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
    }*/



}
