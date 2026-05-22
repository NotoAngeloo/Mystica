package me.angeloo.mystica.Components.MysticaGui;

import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

public class GuiListener implements Listener {

    private final Mystica main;
    private final GuiManager guiManager;

    public GuiListener(Mystica main, GuiManager guiManager) {
        this.main = main;
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        GuiSession session =
                guiManager.getSession(player);

        if(session == null)
            return;

        event.setCancelled(true);

        guiManager.handleClick(player, event);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {

        Player player = (Player) event.getWhoClicked();

        GuiSession session = guiManager.getSession(player);

        if(session == null)
            return;

        event.setCancelled(true);

        guiManager.handleDrag(player, event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();

        new BukkitRunnable(){
            @Override
            public void run(){
                InventoryView inv = player.getOpenInventory();

                if(!inv.getTitle().equalsIgnoreCase("crafting")){
                    return;
                }

                guiManager.handleClose(player, event);
            }
        }.runTaskLater(main, 1);


    }

}
