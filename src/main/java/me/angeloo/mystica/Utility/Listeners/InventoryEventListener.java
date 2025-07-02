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
    private final DisplayWeapons displayWeapons;
    private final BossLevelInv bossLevelInv;
    private final Locations locations;
    private final CustomItemConverter customItemConverter;

    public InventoryEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        equipmentManager = new EquipmentManager(main);
        inventoryIndexingManager = main.getInventoryIndexingManager();
        displayWeapons = new DisplayWeapons(main);
        bossLevelInv = new BossLevelInv(main);
        locations = new Locations(main);
        customItemConverter = new CustomItemConverter();
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
